package dev.carlosivis.workoutsmart.data.local.datasource

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import dev.carlosivis.workoutsmart.database.WorkoutSmartDatabase

actual fun createTestDriver(): SqlDriver =
    JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).also {
        WorkoutSmartDatabase.Schema.create(it)
    }