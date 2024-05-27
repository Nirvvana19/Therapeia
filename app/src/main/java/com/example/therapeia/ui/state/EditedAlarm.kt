package com.example.therapeia.ui.state

import com.example.therapeia.data.AlarmValue

/** Created by Yuriy on 09.08.2017. */
data class EditedAlarm(
    val isNew: Boolean = false,
    val value: AlarmValue,
)
