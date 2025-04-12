plugins {
    id("io.spring.dependency-management")
}

allprojects {
    repositories {
        mavenCentral()
    }
}

val springCloudVersion: String by extra("2023.0.3")

subprojects {
    plugins.withId("java") {
        extensions.configure<JavaPluginExtension> {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(17))
            }
        }
    }

    apply(plugin = "io.spring.dependency-management")

    dependencyManagement {
        imports {
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:${springCloudVersion}")
        }
    }
}
