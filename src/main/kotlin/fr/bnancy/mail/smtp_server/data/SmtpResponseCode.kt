package fr.bnancy.mail.smtp_server.data

enum class SmtpResponseCode(var code: String) {
    HELO("220"),
    EHLO("250-mail.bnancy.ovh\r\n" +
            "250-SIZE 51200000\r\n" +
            "250-ETRN\r\n" +
            "250-STARTTLS\r\n" +
            "250-8BITMIME\r\n" +
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