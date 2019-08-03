package com.infomantri.timer

sealed class Timer
object Pause: Timer()
object Resume: Timer()
object Stop: Timer()
