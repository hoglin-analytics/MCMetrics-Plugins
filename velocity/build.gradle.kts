dependencies {
    implementation(project(":common"))
    implementation(libs.cloud.velocity)
    compileOnly(libs.velocity.api)
    annotationProcessor(libs.velocity.api)
}
