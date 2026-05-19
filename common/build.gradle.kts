plugins {
    `java-library`
}

dependencies {
    api(libs.jackson.toml)
    api(libs.jackson.properties)
    api(libs.cloud.core)
    api(libs.cloud.annotations)
    api(libs.uuid.generator)
    compileOnlyApi(libs.jetbrains.annotations)
}
