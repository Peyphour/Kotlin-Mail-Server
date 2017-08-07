package fr.bnancy.mail.smtp_server.data

enum class SessionState {
    START,
    WAIT_FROM,
    WAIT_TO,
    WAIT_DATA,
    WAIT_QUIT,
    END
}