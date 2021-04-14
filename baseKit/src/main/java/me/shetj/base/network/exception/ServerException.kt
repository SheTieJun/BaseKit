package me.shetj.base.network.exception

class ServerException(val errCode: Int, override val message: String?) : RuntimeException(message)