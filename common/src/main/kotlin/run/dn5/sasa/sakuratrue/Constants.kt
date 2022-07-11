package run.dn5.sasa.sakuratrue

object Constants {
    const val CHANNEL_ID: String = "sakuratrue"
    const val CHANNEL_NAME: String = "message"
    const val CHANNEL_FULL: String = "${CHANNEL_ID}:${CHANNEL_NAME}"

    enum class SUB_P_TO_S(val channel: String) {
        CHECK_AUTH("check_auth")
    }

    enum class SUB_S_TO_P(val channel: String) {
        CHECK_AUTH("check_auth")
    }
}