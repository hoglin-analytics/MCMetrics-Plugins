plugins {
    id("java")
    alias(libs.plugins.shadow)
}

allprojects {
    group = "net.mcmetrics"
    version = "1.0.0"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = rootProject.libs.plugins.shadow.get().pluginId)

    tasks.build {
        dependsOn("shadowJar")
    }
}
