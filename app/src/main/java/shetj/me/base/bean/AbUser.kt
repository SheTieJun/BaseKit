package shetj.me.base.bean

abstract class AbUser(
    val isGuest: Boolean = false,
    val userId: String = "",
    val userName: String = "",
    val userAvatar: String = "",
){

}

object GuestUser : AbUser(isGuest = true, userId = "0", userName = "游客") {
    override fun toString(): String {
        return "GuestUser(isGuest=$isGuest, userId='$userId', userName='$userName', userAvatar='$userAvatar')"
    }
}

class NormalUser : AbUser() {
    override fun toString(): String {
        return "NormalUser(isGuest=$isGuest, userId='$userId', userName='$userName', userAvatar='$userAvatar')"
    }
}