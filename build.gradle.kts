import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    java
    kotlin("jvm") version "1.9.22"
    id("fabric-loom") version "1.5-SNAPSHOT"
}

version = "0.1-1.20.4"
group = "net.green_jab.fixedminecraft"

base {
    archivesName = project.name
}

loom {
    splitEnvironmentSourceSets()

    mods {
        register("fixedminecraft") {
            sourceSet(sourceSets["main"])
            sourceSet(sourceSets["client"])
        }
    }
}

repositories {
    // maven("https://maven.solo-studios.ca/releases/") {
    //     name = "Solo Studios"
    // }
    maven("https://maven.fabricmc.net/") {
        name = "Fabric"
    }
    maven("https://maven.shedaniel.me/") {
        name = "Shedaniel"
        content {
            includeGroup("me.shedaniel.cloth")
        }
    }
    maven("https://maven.terraformersmc.com/releases/") {
        name = "TerraformersMC"
        content {
            includeGroup("com.terraformersmc")
        }
    }
    mavenCentral()
}

dependencies {
    minecraft(libs.minecraft)
    mappings(variantOf(libs.yarn.mappings) { classifier("v2") })
    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.api)
    modImplementation(libs.fabric.language.kotlin)

    // Enable when needed
    // implementation(libs.slf4k) {
    //     include(this)
    // }

    // modImplementation(libs.modmenu)

    // modImplementation(libs.cloth.config) {
    //     exclude(group = "net.fabricmc.fabric-api")
    //     include(this)
    // }
}

fabricApi {
    configureDataGeneration()
}

loom {
    val projectName = project.name.lowercase()
    accessWidenerPath = sourceSets["main"].resources.srcDirs.map { it.resolve("$projectName.accesswidener") }
        .first { it.exists() }
}

tasks.processResources {
    filesMatching("fabric.mod.json") {
        expand(
            "version" to project.version,
            "fabric_api" to libs.versions.fabric.api.get(),
            "minecraft" to libs.versions.minecraft.get(),
        )
    }
}

tasks {
    withType<JavaCompile>().configureEach {
        with(options) {
            encoding = "UTF-8"
            isDeprecation = true
            compilerArgs.add("-Xlint:all")
        }
    }

    withType<Javadoc>().configureEach {
        options {
            encoding = "UTF-8"
        }
    }

    withType<AbstractArchiveTask>().configureEach {
        archiveBaseName = project.name
    }

    withType<Jar>().configureEach {
        from("LICENSE") {
            rename { "${it}_${rootProject.name}" }
        }
    }

    named<Task>("build") {
        dependsOn(withType<Jar>())
    }
}

java {
    withSourcesJar()
}

kotlin {
    jvmToolchain(17)

    compilerOptions {
        apiVersion = KotlinVersion.KOTLIN_1_9
        languageVersion = KotlinVersion.KOTLIN_1_9
    }
}

afterEvaluate {
    // must be done in after evaluate, otherwise (iirc) dependencies can't be properly resolved yet
    loom {
        runs {
            configureEach {
                vmArgs("-Xmx2G")

                property("fabric.development", "true")
                property("mixin.debug", "true")
                property("mixin.debug.export.decompile", "false")
                property("mixin.debug.verbose", "true")
                property("mixin.dumpTargetOnFailure", "true")
                // makes silent failures into hard-failures
                // property("mixin.checks", "true")
                property("mixin.hotSwap", "true")

                // find sponge mixin and add it as a java agent (for runtime class hot swapping)
                val mixinJarFile = configurations.compileClasspath.get().files {
                    it.group == "net.fabricmc" && it.name == "sponge-mixin"
                }.firstOrNull()
                if (mixinJarFile != null)
                    vmArg("-javaagent:$mixinJarFile")

                ideConfigGenerated(true)
            }
        }
    }
}
