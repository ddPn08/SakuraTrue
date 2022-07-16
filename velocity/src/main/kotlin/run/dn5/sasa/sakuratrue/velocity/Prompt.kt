package run.dn5.sasa.sakuratrue.velocity

import com.velocitypowered.api.command.CommandSource
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage

object Prompt {
    enum class Status(val color: String) {
        INFO("aqua"),
        WARN("yellow"),
        ERROR("red")
    }

    fun template(msg: String, status: Status, prefix: String): Component {
        val mm = MiniMessage.miniMessage()
        return mm.deserialize("<green>${prefix} <${status.color}>${msg}")
    }

    fun template(msg: String, status: Status): Component {
        return template(msg, status, "[SakuraTrue]")
    }

    fun info(sender: CommandSource, message: String): Int {
        sender.sendMessage(template(message, Status.INFO))
        return 0
    }

    fun warn(sender: CommandSource, message: String): Int {
        sender.sendMessage(template(message, Status.WARN))
        return 0
    }

    fun err(sender: CommandSource, message: String): Int {
        sender.sendMessage(template(message, Status.ERROR))
        return 1
    }
}