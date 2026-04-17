plugins {
    alias(libs.plugins.plugin.yml.paper)
    alias(libs.plugins.run.paper)
}

dependencies {
    implementation(project(":common"))
//    compileOnly(libs.paper.api)
    compileOnly(libs.folia.api)
    implementation(libs.cloud.paper)
}

paper {
    name = "MCMetrics"
    main = "net.mcmetrics.bukkit.MCMetrics"
    apiVersion = "1.21"
    website = "https://mcmetrics.net/"
    description = "Advanced analytics tracking for your Minecraft server."
    foliaSupported = true
}

tasks.runServer {
    minecraftVersion("1.21.6")
    jvmArgs("-Dhoglin.base.url=\"http://localhost:3100\"")
}