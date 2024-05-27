package com.example.therapeia.ui.list

import androidx.lifecycle.ViewModel
import com.example.therapeia.data.AlarmValue
import com.example.therapeia.ui.state.UiStore

class ListViewModel(
    private val uiStore: UiStore,
) : ViewModel() {
  @Deprecated("Use state flow instead") var openDrawerOnCreate: Boolean = false

  fun edit(alarmValue: AlarmValue) {
    uiStore.edit(alarmValue)
  }

  fun createNewAlarm() {
    uiStore.createNewAlarm()
  }
}
