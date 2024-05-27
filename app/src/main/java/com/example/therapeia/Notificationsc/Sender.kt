package com.example.therapeia.Notificationsc

import com.google.android.gms.tasks.Task

data class Sender(
    var data: Data,
    var to: Task<String>
)