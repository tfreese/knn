plugins {
    id("de.freese.gradle.conventions").apply(false)
    id("io.spring.dependency-management").apply(false)
}

allprojects {
    plugins.apply("base")
}

subprojects {
    plugins.apply("de.freese.gradle.conventions")
    plugins.apply("io.spring.dependency-management")

    extensions.configure(io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension::class.java) {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:" + property("version_springBoot"))
        }

        dependencies {
            dependency("com.lmax:disruptor:" + property("version_disruptor"))
        }
    }

    pluginManager.withPlugin("java") {
        dependencies {
            add("testImplementation", "org.junit.jupiter:junit-jupiter")

            add("testRuntimeOnly", "org.junit.platform:junit-platform-launcher")
        }
    }
}


