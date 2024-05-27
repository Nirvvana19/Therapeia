package com.example.therapeia.ui.main

import androidx.lifecycle.ViewModel
import com.example.therapeia.domain.IAlarmsManager
import com.example.therapeia.logger.BugReporter
import com.example.therapeia.ui.state.EditedAlarm
import com.example.therapeia.ui.state.UiStore
import kotlinx.coroutines.flow.StateFlow

class MainViewModel(
    private val uiStore: UiStore,
    private val alarms: IAlarmsManager,
    private val bugReporter: BugReporter,
) : ViewModel() {
  var openDrawerOnCreate: Boolean = false

  fun editing(): StateFlow<EditedAlarm?> {
    return uiStore.editing()
  }

  fun hideDetails() {
    uiStore.hideDetails()
  }

  fun deleteEdited() {
    uiStore.editing().value?.value?.id?.let { alarms.getAlarm(it)?.delete() }
    uiStore.hideDetails()
  }

  fun sendUserReport() {
    bugReporter.sendUserReport()
  }

  fun edit(restored: EditedAlarm) {
    uiStore.edit(restored.value, restored.isNew)
  }

  fun awaitStored() {}
}
