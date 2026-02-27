plugins {
    alias(libs.plugins.plugin.yml.bukkit)
    alias(libs.plugins.run.paper)
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

tasks.runServer {
    minecraftVersion("1.21.6")
    jvmArgs("-Dhoglin.base.url=\"http://localhost:3100\"")
}