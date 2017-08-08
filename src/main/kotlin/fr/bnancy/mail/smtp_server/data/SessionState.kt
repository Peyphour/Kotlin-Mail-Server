package fr.bnancy.mail.smtp_server.data

enum class SessionState {
    START,
    HELO,
    DATA,
    MAIL,
    RECIPIENT_ADDED,
    QUIT
}