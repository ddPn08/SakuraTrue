package run.dn5.sasa.sakuratrue.velocity

import kotlinx.serialization.Serializable

@Serializable
data class Configuration(
    val verifier: String,
    val discord: Discord
) {
    @Serializable
    data class Discord(
        val token: String,
        val guildId: String,
        val commandDescriptions: CommandDescriptions,
        val changeNickName: Boolean,
        val addRole: AddRole
    ) {
        @Serializable
        data class CommandDescriptions(
            val root: String,
            val verify: String
        )

        @Serializable
        data class AddRole(
            val enabled: Boolean,
            val roles: List<String>
        )
    }
}
