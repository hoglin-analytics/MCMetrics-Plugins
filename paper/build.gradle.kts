plugins {
    alias(libs.plugins.plugin.yml.paper)
    alias(libs.plugins.run.paper)
}

dependencies {
    implementation(project(":common"))
    compileOnly(libs.folia.api)
    implementation(libs.cloud.paper)
}

paper {
    name = "MCMetrics"
    main = "net.mcmetrics.bukkit.MCMetricsPlugin"
    apiVersion = "1.21"
    website = "https://mcmetrics.net/"
    description = "Advanced analytics tracking for your Minecraft server."
    foliaSupported = true
}

tasks.runServer {
    minecraftVersion("1.21.6")
    jvmArgs("-Dmcmetrics.hoglin.server_key=hgln_P3l9ShsTEi5zX-OUU3-49A", "-Dmcmetrics.instance.id=testserver")
}