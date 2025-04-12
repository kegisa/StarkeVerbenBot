rootProject.name = "StarkeVerbenBot"

include("bot")
include("downloader")

pluginManagement {
    plugins {
        id("org.springframework.boot") version "3.3.5"
        id("io.spring.dependency-management") version "1.1.6"
        kotlin("plugin.spring") version "1.8.21"
        kotlin("jvm") version "1.8.21"
    }
}
