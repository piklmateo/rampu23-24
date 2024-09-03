package hr.foi.rampu.eventmanager.entity

data class User(
    val uid:String?=null,
    val name: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val points: Long = 0,
    val type:UserType=UserType.Normal
)

