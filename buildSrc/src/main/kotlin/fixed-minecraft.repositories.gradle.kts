repositories {
    maven("https://maven.solo-studios.ca/releases/") {
        name = "Solo Studios"
    }

    maven("https://maven.fabricmc.net/") {
        name = "Fabric"
        // content {
        //     includeGroupAndSubgroups("net.fabricmc")
        //     includeModule("me.zeroeightsix", "fiber")
        //     includeModule("io.github.llamalad7", "mixinextras-fabric")
        // }
    }

    maven("https://maven.shedaniel.me/") {
        name = "Shedaniel"
        content {
            includeGroup("me.shedaniel.cloth")
        }
    }

    maven("https://maven.blamejared.com") {
        name = "BlameJared"
        content {
            includeGroup("vazkii.patchouli")
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
