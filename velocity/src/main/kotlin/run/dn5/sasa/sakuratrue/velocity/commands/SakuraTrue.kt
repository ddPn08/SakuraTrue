package run.dn5.sasa.sakuratrue.velocity.commands

import com.velocitypowered.api.command.SimpleCommand
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import run.dn5.sasa.sakuratrue.VelocityPlugin
import run.dn5.sasa.sakuratrue.velocity.Messages
import java.util.concurrent.CompletableFuture

class SakuraTrue(
    private val plugin: VelocityPlugin
) : SimpleCommand {

    fun register() {
        val meta = this.plugin.proxy.commandManager.metaBuilder("sakuratrue").aliases("st").build()
        this.plugin.proxy.commandManager.register(meta, this)
    }

    enum class Status(val color: String) {
        INFO("green"),
        WARN("yellow"),
        ERROR("red")
    }

    private fun template(msg: String, status: Status): Component {
        val mm = MiniMessage.miniMessage()
        return mm.deserialize("<light_purple>[SAKURA TRUE] <${status.color}>${msg}")
    }

    private fun template(msg: String) = this.template(msg, Status.INFO)

    private fun usage(invocation: SimpleCommand.Invocation) = invocation.source()
        .sendMessage(this.template("Please see the documentation <aqua><click:open_url:'https://dn5.run'>[CLICK HERE]</click>"))

    override fun suggestAsync(invocation: SimpleCommand.Invocation?): CompletableFuture<MutableList<String>> {
        val suggestions = mutableListOf(
            "help",
            "reload",
            "reauth"
        )
        return CompletableFuture.completedFuture(suggestions)
    }

    override fun execute(invocation: SimpleCommand.Invocation) {
        val args = invocation.arguments()
        if (args.isEmpty()) {
            invocation.source().sendMessage(this.template("Usage"))
            invocation.source().sendMessage(this.template("<aqua> - /st help"))
            return
        }

        when (args[0]) {
            "help" -> {
                this.usage(invocation)
            }
            "reload" -> {
                this.plugin.unload()
                this.plugin.load()
                invocation.source().sendMessage(this.template("Reloaded"))
            }
            "reauth" -> {
                if (args.size < 2) {
                    invocation.source().sendMessage(this.template("Argument missing.", Status.ERROR))
                    this.usage(invocation)
                    return
                }
                val username = args[1]
                val auth = this.plugin.authStore
                if (!auth.verified(username)) {
                    invocation.source().sendMessage(this.template("User not verified.", Status.ERROR))
                    return
                }
                auth.reauth(username)
                this.plugin.proxy.getPlayer(username).ifPresent {
                    val reason = args.drop(2).joinToString(" ")
                    it.disconnect(
                        this.template(
                            reason.ifEmpty { this.plugin.messages.getMessage(Messages.MessageKey.RE_AUTHENTICATE_REQUESTED) },
                            Status.INFO
                        )
                    )
                }
                invocation.source().sendMessage(this.template("Reauth sent."))
            }
        }
    }

    override fun hasPermission(invocation: SimpleCommand.Invocation): Boolean {
        return invocation.source().hasPermission("sakuratrue.command.sakuratrue")
    }

}