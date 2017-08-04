package fr.bnancy.mail.smtp_server.data

enum class SmtpResponseCode(val code: String) {
    HELO("220"),
    OK("250"),
    DATA("354"),
    QUIT("221"),
    UNKNOWN("")
}