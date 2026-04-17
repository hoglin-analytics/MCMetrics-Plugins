import org.gradle.api.Project.DEFAULT_BUILD_DIR_NAME
import org.gradle.kotlin.dsl.support.uppercaseFirstChar

plugins {
    id("java")
    alias(libs.plugins.shadow)
}

allprojects {
    group = "net.mcmetrics"
    version = "3.0.1"

    repositories {
        mavenLocal() // For when I do local SDK development alongside plugin development
        mavenCentral()
        maven("https://hub.spigotmc.org/nexus/content/groups/public/")
        maven("https://maven.hoglin.gg/releases/")
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

val pluginProjectNames = listOf("paper", "bungee", "velocity")
val pluginProjects = subprojects.filter { it.name in pluginProjectNames }

tasks.register("copyBuiltJars") {
    dependsOn(pluginProjects.map { it.tasks.shadowJar })
    doLast {
        val releaseDir = file("$DEFAULT_BUILD_DIR_NAME/release")
        releaseDir.mkdirs()

        pluginProjects.forEach { project ->
            val shadowJarTask = project.tasks.shadowJar.get()
            val jarFile = shadowJarTask.outputs.files.singleFile

            if (jarFile.exists()) {
                copy {
                    from(jarFile)
                    into(releaseDir)

                    rename {
                        "MCMetrics-${project.name.uppercaseFirstChar()}.jar"
                    }
                }
            }
        }
    }
}

tasks.build {
    dependsOn(subprojects.map { it.tasks.build })
    finalizedBy("copyBuiltJars")
}