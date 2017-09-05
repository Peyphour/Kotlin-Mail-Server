package fr.bnancy.mail.servers.smtp.data

enum class SmtpResponseCode(var code: String) {
    HELO("220"),
    EHLO("250-mail.bnancy.ovh\n" +
            "250-SIZE 51200000\n" +
            "250-ETRN\n" +
            "250-STARTTLS\n" +
            "250-8BITMIME\n" +
            "250 DSN"),
    OK("250"),
    DATA("354"),
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
        if(code.length >= 3)
            code = code.substring(0, 3) + " " + s
        return this
    }
}