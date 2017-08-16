package fr.bnancy.mail.smtp_server.data

import java.time.Instant
import java.util.*

data class Session(
        var from: String = "",
        var to: ArrayList<String> = ArrayList(),
        var content: String = "",
        var netAddress: String = "",
        var state: MutableList<SessionState> = emptyList<SessionState>().toMutableList(),
        var senderHostname: String = "",
        var receivingData: Boolean = false,
        var receivedAt: Date = Date.from(Instant.now()),
        var delivered: Boolean = false,
        var secured: Boolean = false
)