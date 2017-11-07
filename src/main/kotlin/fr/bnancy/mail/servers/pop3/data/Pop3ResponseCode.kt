package fr.bnancy.mail.servers.pop3.data

enum class Pop3ResponseCode(var code: String) {
    OK("+OK"),
    ERR("-ERR");

    operator fun invoke(s: String = ""): Pop3ResponseCode {
        if(s == "") {
            code = when(this) {
                OK -> "+OK"
                ERR -> "-ERR"
            }
            return this
        }
        code = code.substring(0, this.name.length + 1) + " " + s
        return this
    }
}