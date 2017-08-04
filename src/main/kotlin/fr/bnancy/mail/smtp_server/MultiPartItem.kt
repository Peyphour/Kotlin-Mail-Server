package fr.bnancy.mail.smtp_server

import fr.bnancy.mail.smtp_server.data.Header

data class MultiPartItem(var headers: MutableList<Header>, var content: String)