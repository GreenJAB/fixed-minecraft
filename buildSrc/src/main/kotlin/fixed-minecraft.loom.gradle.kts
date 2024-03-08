plugins {
    id("fabric-loom")
}

fabricApi {
    configureDataGeneration()
}

loom {
    val projectName = project.name.lowercase()
    accessWidenerPath = sourceSets["main"].resources.srcDirs.map { it.resolve("$projectName.accesswidener") }
        .first { it.exists() }

    mixin {
        defaultRefmapName.set("mixins/$projectName/refmap.json")
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
