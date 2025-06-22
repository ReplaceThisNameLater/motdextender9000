plugins {
    id("checkstyle")
    alias(libs.plugins.loom)
}

version = "1.0.1"
group = "com.github.amyavi"

repositories {
    maven("https://maven.neoforged.net/releases")
}

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.officialMojangMappings())

    neoForge(libs.neoforge)
    modImplementation(include("net.kyori:adventure-platform-neoforge:${libs.versions.adventure.get()}")!!)

    include("net.kyori:option") {
        version {
            because("HACK: option 1.0.0 (bundled by adventure-platform-neoforge) breaks configurate 4.2.0")
            strictly("1.1.0")
        }
    }
}

tasks.processResources {
    val properties = mapOf(
        "version" to version,
        "java_version" to libs.versions.java.get(),
        "loader_version" to libs.versions.neoforge.get()
    )

    inputs.properties(properties)
    filesMatching(listOf("META-INF/neoforge.mods.toml", "motdextender9000.mixins.json")) {
        expand(properties)
    }
}

java {
    val javaVersion = JavaVersion.toVersion(libs.versions.java.get())
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}
