package com.example.therapeia.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.therapeia.bootstrap.globalInject
import com.example.therapeia.bootstrap.globalLogger
import com.example.therapeia.data.DatastoreMigration
import com.example.therapeia.logger.Logger

class TestReceiver : BroadcastReceiver() {
  private val log: Logger by globalLogger("TestReceiver")
  private val migration: DatastoreMigration by globalInject()

  override fun onReceive(context: Context?, intent: Intent?) {
    log.debug { intent?.action.orEmpty() }
    when (intent?.action) {
      ACTION_DROP -> {
        migration.drop()
      }
      ACTION_DROP_AND_INSERT_DEFAULTS -> {
        migration.drop()
        migration.insertDefaultAlarms()
      }
      ACTION_DROP_AND_MIGRATE_DATABASE -> {
        migration.drop()
        migration.migrateDatabase()
      }
      else -> error("Unexpected $intent")
    }
    intent.getStringExtra("CB")?.let { cbAction -> context?.sendBroadcast(Intent(cbAction)) }
  }

  companion object {
    const val ACTION_DROP = com.example.therapeia.BuildConfig.APPLICATION_ID + ".ACTION_DROP"
    const val ACTION_DROP_AND_INSERT_DEFAULTS =
        com.example.therapeia.BuildConfig.APPLICATION_ID + ".ACTION_DROP_AND_INSERT_DEFAULTS"
    const val ACTION_DROP_AND_MIGRATE_DATABASE =
        com.example.therapeia.BuildConfig.APPLICATION_ID + ".ACTION_DROP_AND_MIGRATE_DATABASE"
  }
}
