package com.example.therapeia.ui.details

import androidx.lifecycle.ViewModel
import com.example.therapeia.data.AlarmValue
import com.example.therapeia.ui.state.EditedAlarm
import com.example.therapeia.ui.state.UiStore
import kotlinx.coroutines.flow.StateFlow

class AlarmDetailsViewModel(private val uiStore: UiStore) : ViewModel() {
  fun editor(): StateFlow<EditedAlarm?> {
    return uiStore.editing()
  }

  fun hideDetails() {
    uiStore.hideDetails()
  }

  fun modify(reason: String, function: (AlarmValue) -> AlarmValue) {
    uiStore.editing().value?.let { prev -> uiStore.edit(function(prev.value), prev.isNew) }
  }

  var newAlarmPopupSeen: Boolean = false
}
