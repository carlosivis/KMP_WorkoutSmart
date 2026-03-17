package dev.carlosivis.workoutsmart.data.local.datasource

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import dev.carlosivis.workoutsmart.database.WorkoutSmartDatabase

actual fun createTestDriver(): SqlDriver =
    NativeSqliteDriver(WorkoutSmartDatabase.Schema, ":memory:")