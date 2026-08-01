plugins {
    id("application")
}

description = "Demo for an artificial neural network"

dependencies {
    implementation(project(":knn-net"))

    implementation("org.springframework:spring-jdbc")

    runtimeOnly("org.slf4j:slf4j-simple")

    // testImplementation("org.mariadb.jdbc:mariadb-java-client")
    // testImplementation("org.springframework:spring-jdbc")
}

val mainClazz = "de.freese.knn.buttons.KnnButtonMain"

application {
    mainClass = mainClazz
}

// Done by Convention Plugin.
//    jar {
//        manifest {
//            attributes(
//                    "Main-Class": mainClazz
//            )
//        }
//    }