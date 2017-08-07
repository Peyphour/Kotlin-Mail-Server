package fr.bnancy.mail.smtp_server.data

enum class SmtpResponseCode(val code: String) {
    HELO("220"),
    OK("250"),
    DATA("354"),
    QUIT("221"),
    INVALID("500 Invalid command"),
    BAD_SEQUENCE("503 Bad sequence"),
    MAILBOX_UNAVAILABLE("450 Mailbox doesn't exists here"),
    SENDER_BLACKLIST("451 Sender blacklisted"),
    UNKNOWN("")
}