dependencies {
    implementation(project(":common"))
    implementation(libs.cloud.velocity)
    compileOnly(libs.velocity.api)
    annotationProcessor(libs.velocity.api)
}

tasks.processResources {
    inputs.property("version", project.version)
    filesMatching("velocity-plugin.json") {
        expand("version" to project.version)
    }
}