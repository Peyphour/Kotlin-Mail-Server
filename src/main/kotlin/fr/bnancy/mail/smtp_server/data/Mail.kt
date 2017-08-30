package fr.bnancy.mail.smtp_server.data

import com.fasterxml.jackson.databind.ObjectMapper
import fr.bnancy.mail.data.Mail

class Mail(session: Session) {

    val sender = session.from
    val recipients = session.to
    val headers = parseHeaders(session.content)
    val content = parseContent(session.content)
    val date = session.receivedAt
    val secured = session.secured

    private fun parseHeaders(content: String): String {
        val headers: MutableList<Header> = ArrayList()
        for (line: String in content.lines()) {
            val header: Header = Header("", "")
            if (line.isEmpty()) // No header: end of header section
                break
            if (line.indexOf(':') >= 0 && line[0].isLetterOrDigit()) { // Header key present
                header.key = line.substring(0, line.indexOf(':'))
                header.value = line.substring(line.indexOf(':') + 1, line.length)
                headers.add(header)
            } else if (!line[0].isLetterOrDigit() && headers.size > 0) { // Follow up of last header content
                headers[headers.size - 1].value += line
            } else
                continue // invalid line : drop it
        }
        return ObjectMapper().writeValueAsString(headers)
    }

    private fun parseContent(content: String): String {
        return content.lines()
                .dropWhile { !it.isEmpty() }
                .joinToString("\n")
    }

    override fun toString(): String {
        return "Mail(sender='$sender', recipients=$recipients, headers=$headers, content='$content')"
    }

    fun toEntity(): Mail {
        return Mail(0, sender, recipients, headers, content, date, secured)
    }
}