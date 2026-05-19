plugins {
    alias(libs.plugins.plugin.yml.bungee)
}

dependencies {
    implementation(project(":common"))
    implementation(libs.cloud.bungee)
    compileOnly(libs.bungeecord.api)
}

bungee {
    name = "MCMetrics"
    main = "net.mcmetrics.bungee.MCMetricsPlugin"
    description = "Advanced analytics tracking for your Minecraft server."
}