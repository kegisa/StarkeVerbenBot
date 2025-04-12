rootProject.name = "StarkeVerbenBot"

include("bot")
include("downloader")

pluginManagement {
    plugins {
        id("org.springframework.boot") version "3.3.5"
        id("io.spring.dependency-management") version "1.1.6"
    }
}
