plugins {
    alias(libs.plugins.plugin.yml.bukkit)
}

dependencies {
    implementation(project(":common"))
    implementation(libs.spigot.api)
    implementation(libs.cloud.paper)
}

bukkit {
    name = "MCMetrics"
    main = "net.mcmetrics.bukkit.MCMetrics"
    apiVersion = "1.21"
    website = "https://mcmetrics.net/"
    description = "Advanced analytics tracking for your Minecraft server."
}
