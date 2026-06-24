plugins {
    alias(libs.plugins.plugin.yml.bukkit)
    alias(libs.plugins.run.paper)
}

dependencies {
    implementation(project(":common"))
    compileOnly(libs.spigot.api)
    compileOnly(libs.brigadier)
    implementation(libs.cloud.paper)
}

bukkit {
    name = "MCMetrics"
    main = "net.mcmetrics.bukkit.MCMetricsPlugin"
    apiVersion = "1.21"
    website = "https://mcmetrics.net/"
    description = "Advanced analytics tracking for your Minecraft server."
    foliaSupported = true
}

tasks.runServer {
    minecraftVersion("1.21.6")
}