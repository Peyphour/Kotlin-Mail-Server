package fr.bnancy.mail.smtp_server.data.entities

import javax.persistence.*

@Entity
data class Mail(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long = 0,
        var sender: String = "",
        var recipients: ArrayList<String> = ArrayList(),
        @Lob
        var headers: String = "",
        @Lob
        var content: String = ""
)