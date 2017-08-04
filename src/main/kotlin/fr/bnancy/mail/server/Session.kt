package fr.bnancy.mail.server

data class Session(
        var from: String = "",
        var to: ArrayList<String> = ArrayList(),
        var content: String = "",
        var expectData: Boolean = false,
        var end: Boolean = false,
        var netAddress: String = ""
)