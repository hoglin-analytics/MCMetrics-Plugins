import org.gradle.kotlin.dsl.support.uppercaseFirstChar

plugins {
    id("java")
    alias(libs.plugins.shadow)
}

allprojects {
    group = "net.mcmetrics"
    version = "3.0.0"

    repositories {
        mavenCentral()
        maven("https://hub.spigotmc.org/nexus/content/groups/public/")
        maven("https://maven.waypointstudios.com/releases/")
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = rootProject.libs.plugins.shadow.get().pluginId)

    dependencies {
        implementation(rootProject.libs.hoglin)
        compileOnly(rootProject.libs.lombok)
        annotationProcessor(rootProject.libs.lombok)
    }

    tasks.build {
        dependsOn("shadowJar")
    }

    tasks.shadowJar {
        archiveFileName.set("MCMetrics-${this.project.name.uppercaseFirstChar()}-${this.project.version}.jar")
        mergeServiceFiles()
    }
}
