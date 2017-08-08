package fr.bnancy.mail.smtp_server.commands.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Command(val command: String)