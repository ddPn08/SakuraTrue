package run.dn5.sasa.sakuratrue

import com.charleskorn.kaml.Yaml
import com.google.inject.Inject
import com.velocitypowered.api.command.BrigadierCommand
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import org.slf4j.Logger
import run.dn5.sasa.sakuratrue.velocity.*
import run.dn5.sasa.sakuratrue.velocity.SakuraTrueCommand
import run.dn5.sasa.sakuratrue.velocity.ban.Ban
import run.dn5.sasa.sakuratrue.velocity.verifier.discord.Discord
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
    companion object {
        lateinit var instance: VelocityPlugin
    }

    init {
        instance = this
    }

    val command = SakuraTrueCommand()

    val messages = Messages()

    val discord = Discord()
    val ban = Ban()

    @Subscribe
    fun onEnable(e: ProxyInitializeEvent) {
        checkResources()
        if (!checkConfiguration()) {
            logger.error("config.yml is invalid. Rewrite it and then execute '/sakuratrue reload' .")
            return
        }
        load()
    }

    fun getConfig(): Configuration {
        return Yaml.default.decodeFromStream(
            Configuration.serializer(),
            File("${dataFolder}/config.yml").inputStream()
        )
    }

    fun load() {
        val config = getConfig()
        when (config.verifier) {
            "discord" -> discord.enable()
        }
        this.ban.enable()

        proxy.commandManager.register(BrigadierCommand(command.build()))
    }

    fun unload() {
        discord.disable()
    }

    private fun checkConfiguration(): Boolean {
        val config = getConfig()
        return config.discord.token.isNotEmpty() && config.discord.guildId.isNotEmpty()
    }

    private fun checkResources() {
        if (!dataFolder.toFile().exists()) dataFolder.toFile().mkdir()
        listOf(
            "auth.yml",
            "config.yml",
            "messages.yml"
        ).forEach {
            val file = File("${dataFolder}/${it}")
            if (!file.exists()) javaClass.getResourceAsStream("/${it}").use { input ->
                if (input == null) return
                Files.copy(input, file.toPath())
            }
        }
    }
}
