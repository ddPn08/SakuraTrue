package run.dn5.sasa.sakuratrue

import com.charleskorn.kaml.Yaml
import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier
import org.slf4j.Logger
import run.dn5.sasa.sakuratrue.velocity.*
import run.dn5.sasa.sakuratrue.velocity.commands.SakuraTrue
import run.dn5.sasa.sakuratrue.velocity.listeners.minecraft.PluginMessageListener
import run.dn5.sasa.sakuratrue.velocity.listeners.minecraft.PostLoginListener
import java.io.File
import java.nio.file.Files
import java.nio.file.Path


@Plugin(
    id = "sakuratrue",
    name = "SakuraTrue"
)
class VelocityPlugin @Inject constructor(
    val proxy: ProxyServer,
    private val logger: Logger,
    @DataDirectory val dataFolder: Path
) {
    private val discord = DiscordClient(this)

    val authStore = AuthStore(this)
    val codeStore = SessionStore(this)
    val messages = Messages(this)

    @Subscribe
    fun onEnable(e: ProxyInitializeEvent) {
        this.checkResources()
        this.registerCommands()
        if (!this.checkConfiguration()) {
            this.logger.error("config.yml is invalid. Rewrite it and then execute '/sakuratrue reload' .")
            return
        }
        this.load()
    }

    fun getConfig(): Configuration {
        return Yaml.default.decodeFromStream(
            Configuration.serializer(),
            File("${this.dataFolder}/config.yml").inputStream()
        )
    }

    fun unload() {
        this.discord.disable()
    }

    fun load() {
        this.authStore.load()
        this.setupDiscordClient()
        this.registerPluginMessageChannel()
        this.registerListeners()
    }

    private fun checkConfiguration(): Boolean {
        val config = this.getConfig()
        return config.discord.token.isNotEmpty() && config.discord.guildId.isNotEmpty()
    }

    private fun setupDiscordClient() {
        val config = this.getConfig()
        this.discord.enable(config.discord.token)
    }

    private fun registerPluginMessageChannel() {
        this.proxy.channelRegistrar.register(
            MinecraftChannelIdentifier.create(
                Constants.CHANNEL_ID,
                Constants.CHANNEL_NAME
            )
        )
    }

    private fun registerListeners() {
        listOf(
            PostLoginListener(this),
            PluginMessageListener(this)
        ).forEach { this.proxy.eventManager.register(this, it) }
    }

    private fun checkResources() {
        if (!this.dataFolder.toFile().exists()) this.dataFolder.toFile().mkdir()
        listOf(
            "auth.yml",
            "config.yml",
            "messages.yml"
        ).forEach {
            val file = File("${this.dataFolder}/${it}")
            if (!file.exists()) this.javaClass.getResourceAsStream("/${it}").use { input ->
                if (input == null) return
                Files.copy(input, file.toPath())
            }
        }
    }

    private fun registerCommands() {
        SakuraTrue(this).register()
    }
}
