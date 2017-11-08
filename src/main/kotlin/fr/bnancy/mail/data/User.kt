package fr.bnancy.mail.data

import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import javax.persistence.*

@Entity
data class User(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long = 0,

        @Column(unique = true)
        var mail: String= "",

        var password: String = "",

        @Enumerated(EnumType.ORDINAL)
        @ElementCollection(targetClass = UserAuthority::class)
        @Fetch(FetchMode.JOIN)
        var authorities: MutableSet<UserAuthority> = mutableSetOf(),

        var enabled: Boolean = true
)