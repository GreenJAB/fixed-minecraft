plugins {
    `fixed-minecraft`.repositories
    `fixed-minecraft`.compilation
    `fixed-minecraft`.loom
    `fixed-minecraft`.tasks
}

version = "0.1-1.20.4"
group = "net.green_jab.fixed_minecraft"

dependencies {
    minecraft(libs.minecraft)

    mappings(variantOf(libs.yarn.mappings) { classifier("v2") })


    modImplementation(libs.fabric.loader)

    modImplementation(libs.fabric.api)
    modImplementation(libs.fabric.language.kotlin)


    annotationProcessor(libs.sponge.mixin)


    implementation(libs.slf4k) {
        include(this)
    }


    modImplementation(libs.bundles.silk) {
        exclude(group = "net.fabricmc.fabric-api")
        include(this)
    }


    modImplementation(libs.cloth.config) {
        exclude(group = "net.fabricmc.fabric-api")
        include(this)
    }


    modImplementation(libs.patchouli) {
        exclude(group = "net.fabricmc.fabric-api")
        include(this)
    }

    modImplementation(libs.modmenu)
}

tasks.processResources {
    filesMatching("/fabric.mod.json") {
        expand(
            "version" to project.version,
            "versions" to mapOf(
                "fabric" to mapOf(
                    "api" to libs.versions.fabric.api.get(),
                    "loader" to libs.versions.fabric.loader.get(),
                    "languageKotlin" to libs.versions.fabric.language.kotlin.get(),
                ),
                "clothconfig" to libs.versions.cloth.config.get(),
                "minecraft" to libs.versions.minecraft.get(),
                "silk" to libs.versions.silk.get(),
                "patchouli" to libs.versions.patchouli.get(),
            )
        )
    }
}
