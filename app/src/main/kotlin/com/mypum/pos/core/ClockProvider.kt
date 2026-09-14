package com.mypum.pos.core
import java.time.Instant
interface ClockProvider { fun now(): Instant }
class SystemClockProvider:ClockProvider { override fun now()=Instant.now() }
