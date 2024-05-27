package com.example.therapeia.platform

interface Wakelocks {
  fun acquireServiceLock()

  fun releaseServiceLock()
}
