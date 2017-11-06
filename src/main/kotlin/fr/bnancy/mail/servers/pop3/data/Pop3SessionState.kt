package fr.bnancy.mail.servers.pop3.data

enum class Pop3SessionState {
    AUTHORIZATION,
    TRANSACTION,
    UPDATE,
    QUIT
}