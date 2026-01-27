plugins {
    `java-library`
}

dependencies {
    api(libs.jtoml)
    api(libs.cloud.core)
    api(libs.cloud.annotations)
    compileOnlyApi(libs.jetbrains.annotations)
}
