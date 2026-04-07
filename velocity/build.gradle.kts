dependencies {
    implementation(project(":common"))
    compileOnly(libs.velocity.api)
    annotationProcessor(libs.velocity.api)
}
