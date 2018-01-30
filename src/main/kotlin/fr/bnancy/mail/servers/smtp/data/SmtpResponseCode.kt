package fr.bnancy.mail.servers.smtp.data

enum class SmtpResponseCode(var code: String) {
    HELO("220"),
    EHLO("250"),
    OK("250"),
    DATA("354"),
    AUTH_GO_ON("334"),
    AUTH_OK("235"),
    QUIT("221"),
    INVALID("500"),
    BAD_SEQUENCE("503"),
    MAILBOX_UNAVAILABLE("550"),
    SENDER_BLACKLIST("551"),
    NOT_AVAILABLE("454"),
    ARGUMENT_ERROR("501"),
    EMPTY(""),
    UNKNOWN_COMMAND("550");

    operator fun invoke(s: String = ""): SmtpResponseCode {
        if (code.length >= 3)
            code = code.substring(0, 3) + when (this) {
                EHLO -> ""
                else -> " " // Add a space when not EHLO
            } + s
        return this
    }
}