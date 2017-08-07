package fr.bnancy.mail.smtp_server.data

data class Session(
        var from: String = "",
        var to: ArrayList<String> = ArrayList(),
        var content: String = "",
        var netAddress: String = "",
        var state: SessionState = SessionState.START
)