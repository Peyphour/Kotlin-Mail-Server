package fr.bnancy.mail.data

interface MailProjection {
    fun getId(): Long
    fun getRecipients(): ArrayList<String>
    fun getHeaders(): String
    fun getSeen(): Boolean
    fun getSpam(): Boolean
}