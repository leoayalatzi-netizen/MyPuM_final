package com.mypum.pos.domain.util
import java.security.MessageDigest
object PinHasher { fun hash(pin:String):String=MessageDigest.getInstance("SHA-256").digest(pin.toByteArray()).joinToString(""){"%02x".format(it)} }
