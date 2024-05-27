package com.example.therapeia.data

interface DatastoreMigration {
  fun drop()

  fun insertDefaultAlarms()

  fun migrateDatabase()
}
