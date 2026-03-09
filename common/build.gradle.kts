plugins {
    `java-library`
}

dependencies {
    api(libs.jtoml)
    api(libs.cloud.core)
    api(libs.cloud.annotations)
    api(libs.uuid.generator)
    compileOnlyApi(libs.jetbrains.annotations)
}
