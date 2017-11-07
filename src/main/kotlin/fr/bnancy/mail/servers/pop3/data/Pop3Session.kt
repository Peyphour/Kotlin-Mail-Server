package fr.bnancy.mail.servers.pop3.data

data class Pop3Session(
        var user: String = "",
        var currentState: Pop3SessionState = Pop3SessionState.AUTHORIZATION,
        var messageToDelete: MutableList<Int> = mutableListOf()
)