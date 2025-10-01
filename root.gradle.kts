plugins {
    id("dev.deftu.gradle.multiversion-root")
}

preprocess {
    "1.8.9-fabric"(1_08_09, "yarn"){
        "1.8.9-forge"(1_08_09, "srg")
    }
    strictExtraMappings.set(true)
}