package fr.bnancy.mail.data

interface MailWithoutContent {
    fun getId(): Long
    fun getRecipients(): ArrayList<String>
    fun getHeaders(): String
}