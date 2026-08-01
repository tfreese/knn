plugins {
    id("java-library")
}

description = "Künstliches neuronales Netz (Artificial neural network)"

dependencies {
    api("com.lmax:disruptor")
    api("io.projectreactor:reactor-core")
    api("org.slf4j:slf4j-api")
}