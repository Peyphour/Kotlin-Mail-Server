package fr.bnancy.mail.servers.pop3.commands.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Pop3Command(val command: String)