// Created: 11.06.2008
package de.freese.knn;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.NeuralNetBuilder;
import de.freese.knn.net.function.FunctionSigmoide;
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
public class TestMailSpamFilter implements TrainingInputSource
{
    /**
     * @param args String[]
     *
     * @throws Exception Falls was schief geht.
     */
    public static void main(final String[] args) throws Exception
    {
        TestMailSpamFilter spamFilter = new TestMailSpamFilter();
        // spamFilter.cleanUp();

        // @formatter:off
        NeuralNet neuralNet = new NeuralNetBuilder()
                .layerInput(new InputLayer(spamFilter.token.size()))
                .layerHidden(new HiddenLayer(20000, new FunctionSigmoide()))
                .layerOutput(new OutputLayer(1))
                .build()
                ;
          // @formatter:on

        double teachFactor = 0.5D;
        double momentum = 0.5D;
        double maximumError = 0.05D;
        int maximumIteration = 10000;

        NetTrainer trainer = new NetTrainer(teachFactor, momentum, maximumError, maximumIteration);
        // trainer.addNetTrainerListener(new PrintStreamNetTrainerListener(System.out));
        trainer.addNetTrainerListener(new LoggerNetTrainerListener());
        trainer.train(neuralNet, spamFilter);

        neuralNet.close();
        spamFilter.closeDataSource();
    }

    /**
     *
     */
    private JdbcTemplate jdbcTemplate;
    /**
     *
     */
    private List<Map<String, Object>> messages;
    /**
     *
     */
    private List<String> token;

    /**
     * Erstellt ein neues {@link TestMailSpamFilter} Object.
     */
    public TestMailSpamFilter()
    {
        super();

        SingleConnectionDataSource ds = new SingleConnectionDataSource();
        // ds.setDriverClassName("com.mysql.jdbc.Driver");
        ds.setDriverClassName("org.mariadb.jdbc.Driver");
        ds.setUrl("jdbc:mariadb://localhost:3306/tommy?user=tommy&password=tommy");
        ds.setSuppressClose(true);
        ds.setAutoCommit(true);

        this.jdbcTemplate = new JdbcTemplate(ds);

        this.messages = this.jdbcTemplate.queryForList("select message_id, is_spam from message");
        this.token = this.jdbcTemplate.queryForList("select token from token order by token", String.class);
    }

    /**
     *
     */
    public void cleanUp()
    {
        // Entfernen aller Token aus gleichen Zeichen.
        // a-z
        for (char c = 97; c <= 122; c++)
        {
            for (int i = 3; i < 10; i++)
            {
                String t = ("" + c).repeat(i);
                t = "%" + t + "%";
                int deleted = this.jdbcTemplate.update("delete from message_token where token like ?", t);
                deleted += this.jdbcTemplate.update("delete from token where token like ?", t);

                System.out.printf("%s: %d deleted%n", t, deleted);
            }
        }

        // select count(*), is_spam from message group by is_spam
    }

    /**
     *
     */
    public void closeDataSource()
    {
        DataSource dataSource = this.jdbcTemplate.getDataSource();

        if (dataSource instanceof SingleConnectionDataSource)
        {
            ((SingleConnectionDataSource) dataSource).destroy();
        }

        this.jdbcTemplate = null;
    }

    /**
     * @see de.freese.knn.net.trainer.TrainingInputSource#getInputAt(int)
     */
    @Override
    public double[] getInputAt(final int index)
    {
        String messageID = (String) this.messages.get(index).get("MESSAGE_ID");

        final double[] input = new double[this.token.size()];
        Arrays.fill(input, 0.0D);

        this.jdbcTemplate.query("select token from message_token where message_id = ?", rs -> {
            while (rs.next())
            {
                int i = TestMailSpamFilter.this.token.indexOf(rs.getString("token"));
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

    /**
     * @see de.freese.knn.net.trainer.TrainingInputSource#getOutputAt(int)
     */
    @Override
    public double[] getOutputAt(final int index)
    {
        Boolean isSpam = (Boolean) this.messages.get(index).get("IS_SPAM");
        double[] output =
        {
                isSpam ? 1.0D : 0.0D
        };

        return output;
    }

    /**
     * @see de.freese.knn.net.trainer.TrainingInputSource#getSize()
     */
    @Override
    public int getSize()
    {
        return this.messages.size();
    }
}
