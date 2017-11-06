package fr.bnancy.mail.servers.smtp.data

enum class SmtpSessionState {
    START,
    HELO,
    DATA,
    MAIL,
    RECIPIENT_ADDED,
    QUIT,
    TLS_STARTED
}