// Created: 11.06.2008
package de.freese.knn;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.sql.DataSource;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.jdbc.core.JdbcTemplate;

import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.NeuralNetBuilder;
import de.freese.knn.net.function.FunctionSigmoid;
import de.freese.knn.net.layer.HiddenLayer;
import de.freese.knn.net.layer.InputLayer;
import de.freese.knn.net.layer.OutputLayer;
import de.freese.knn.net.trainer.LoggerNetTrainerListener;
import de.freese.knn.net.trainer.NetTrainer;
import de.freese.knn.net.trainer.TrainingInputSource;

/**
 * Klasse zum Test des BinaryPersisters.
 *
 * @author Thomas Freese
 */
public class TestMailSpamFilter implements TrainingInputSource {

    public static void main(final String[] args) throws Exception {

        final DataSource dataSource = createDataSource();

        try {
            final TestMailSpamFilter spamFilter = new TestMailSpamFilter(dataSource);
            // spamFilter.cleanUp();

            final NeuralNet neuralNet = new NeuralNetBuilder()
                    .layerInput(new InputLayer(spamFilter.token.size()))
                    .layerHidden(new HiddenLayer(20000, new FunctionSigmoid()))
                    .layerOutput(new OutputLayer(1))
                    .build();

            final double teachFactor = 0.5D;
            final double momentum = 0.5D;
            final double maximumError = 0.05D;
            final int maximumIteration = 10000;

            final NetTrainer trainer = new NetTrainer(teachFactor, momentum, maximumError, maximumIteration);
            // trainer.addNetTrainerListener(new PrintStreamNetTrainerListener(System.out));
            trainer.addNetTrainerListener(new LoggerNetTrainerListener());
            trainer.train(neuralNet, spamFilter);

            neuralNet.close();
        }
        finally {
            if (dataSource instanceof AutoCloseable ds) {
                ds.close();
            }
            // else if (dataSource instanceof JDBCPool p) {
            //     p.close(1);
            // }
            // else if (dataSource instanceof JdbcConnectionPool p) {
            //     p.dispose();
            // }
            else if (dataSource instanceof DisposableBean db) {
                db.destroy();
            }
        }
    }

    private static DataSource createDataSource() {
        // return new MariaDbPoolDataSource("jdbc:mariadb://localhost:3306/testdb?user=root&password=rootpw&maxPoolSize=3");

        // final SingleConnectionDataSource dataSource = new SingleConnectionDataSource();
        // // dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        // dataSource.setDriverClassName("org.mariadb.jdbc.Driver");
        // dataSource.setUrl("jdbc:mariadb://localhost:3306/mail?user=...&password=...");
        // dataSource.setSuppressClose(true);
        // dataSource.setAutoCommit(true);
        //
        // return dataSource;

        // H2
        // final JdbcConnectionPool pool = JdbcConnectionPool.create("jdbc:h2:file:" + dbPath.resolve("h2"), "sa", "sa");
        // pool.setMaxConnections(3);

        // Hsqldb
        // final JDBCPool pool = new JDBCPool(3);
        // pool.setUrl("jdbc:hsqldb:file:" + dbPath.resolve("hsqldb") + ";shutdown=true");
        // pool.setUser("sa");
        // pool.setPassword("sa");

        // Oracle
        // final HikariConfig config = new HikariConfig();
        // config.setDriverClassName("oracle.jdbc.OracleDriver");
        // config.setJdbcUrl("jdbc:oracle:thin:@//localhost:1521/XEPDB1");
        // config.setUsername("testuser");
        // config.setPassword("testpw");
        // config.setMinimumIdle(1);
        // config.setMaximumPoolSize(3);
        // config.setConnectionTimeout(5 * 1000L); // Sekunden
        // config.addDataSourceProperty("cachePrepStmts", "true");
        // config.addDataSourceProperty("prepStmtCacheSize", "250");
        // config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        //
        // final HikariDataSource pool = new HikariDataSource(config);

        return null;
    }

    private final JdbcTemplate jdbcTemplate;
    private final List<Map<String, Object>> messages;
    private final List<String> token;

    public TestMailSpamFilter(final DataSource dataSource) {
        super();

        this.jdbcTemplate = new JdbcTemplate(Objects.requireNonNull(dataSource, "dataSource required"));

        this.messages = this.jdbcTemplate.queryForList("select message_id, is_spam from message");
        this.token = this.jdbcTemplate.queryForList("select token from token order by token", String.class);
    }

    public void cleanUp() {
        // Entfernen aller Token aus gleichen Zeichen.
        // a-z
        for (char c = 97; c <= 122; c++) {
            for (int i = 3; i < 10; i++) {
                final String t = ("" + c).repeat(i);
                int deleted = this.jdbcTemplate.update("delete from message_token where token like %?%", t);
                deleted += this.jdbcTemplate.update("delete from token where token like %?%", t);

                System.out.printf("%s: %d deleted%n", t, deleted);
            }
        }

        // select count(*), is_spam from message group by is_spam
    }

    @Override
    public double[] getInputAt(final int index) {
        final String messageID = (String) this.messages.get(index).get("MESSAGE_ID");

        final double[] input = new double[this.token.size()];
        Arrays.fill(input, 0.0D);

        this.jdbcTemplate.query("select token from message_token where message_id = ?", rs -> {
            while (rs.next()) {
                final int i = TestMailSpamFilter.this.token.indexOf(rs.getString("token"));
                input[i] = 1.0D;
            }

            return null;
        }, messageID);

        // token: 10184
        // SELECT * FROM message_token WHERE message_id = '<001b01ce0f36$5148a8d0$f3d9fa70$@profcon.de>' ORDER BY token
        //
        // select t.token as token, IF(m.is_spam = 0, 1, 0) as spam from token t
        // left outer join message_token mt on mt.token = t.token
        // left outer join message m on m.message_id = mt.message_id and mt.message_id = '<001b01ce0f36$5148a8d0$f3d9fa70$@profcon.de>'
        // group by token, spam
        // order by token asc
        return input;
    }

    @Override
    public double[] getOutputAt(final int index) {
        final Boolean isSpam = (Boolean) this.messages.get(index).get("IS_SPAM");

        return new double[]{isSpam ? 1.0D : 0.0D};
    }

    @Override
    public int getSize() {
        return this.messages.size();
    }
}
