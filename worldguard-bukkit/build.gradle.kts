import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `java-library`
}

applyPlatformAndCoreConfiguration()
applyShadowConfiguration()

repositories {
    maven {
        name = "paper"
        url = uri("https://papermc.io/repo/repository/maven-public/")
    }
    maven {
        name = "bstats"
        url = uri("https://repo.codemc.org/repository/maven-public")
    }
    maven {
        name = "aikar-timings"
        url = uri("https://repo.aikar.co/nexus/content/groups/aikar/")
    }
    maven {
        name = "spigot"
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
}

dependencies {
    "api"(project(":worldguard-core"))
    //"api"(project(":worldguard-libs:bukkit"))
    // "api"("com.destroystokyo.paper:paper-api:1.16.2-R0.1-SNAPSHOT")
    "api"("org.spigotmc:spigot-api:1.17-R0.1-SNAPSHOT") {
        exclude("junit", "junit")
    }
    "implementation"("io.papermc:paperlib:1.0.6")
    "api"("com.sk89q.worldedit:worldedit-bukkit:${Versions.WORLDEDIT}") { isTransitive = false }
    "implementation"("com.google.guava:guava:${Versions.GUAVA}")
    "implementation"("com.sk89q:commandbook:2.3") { isTransitive = false }
    "implementation"("org.bstats:bstats-bukkit:2.1.0")
    "implementation"("co.aikar:minecraft-timings:1.0.4")
}

tasks.named<Copy>("processResources") {
    val internalVersion = project.ext["internalVersion"]
    inputs.property("internalVersion", internalVersion)
    filesMatching("plugin.yml") {
        expand("internalVersion" to internalVersion)
    }
}

tasks.named<Jar>("jar") {
    val projectVersion = project.version
    inputs.property("projectVersion", projectVersion)
    manifest {
        attributes("Implementation-Version" to projectVersion)
    }
}

tasks.named<ShadowJar>("shadowJar") {
    dependencies {
        include(dependency(":worldguard-core"))
        relocate("org.bstats", "com.sk89q.worldguard.bukkit.bstats") {
            include(dependency("org.bstats:bstats-bukkit"))
        }
        relocate ("io.papermc.lib", "com.sk89q.worldguard.bukkit.paperlib") {
            include(dependency("io.papermc:paperlib"))
        }
        relocate ("co.aikar.timings.lib", "com.sk89q.worldguard.bukkit.timingslib") {
            include(dependency("co.aikar:minecraft-timings"))
        }
    }
}

tasks.named("assemble").configure {
    dependsOn("shadowJar")
}
