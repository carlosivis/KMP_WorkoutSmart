package dev.carlosivis.workoutsmart.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import dev.carlosivis.workoutsmart.database.WorkoutSmartDatabase
import org.koin.dsl.module

actual fun platformModule() = module {
    single<SqlDriver> {
        NativeSqliteDriver(
            schema = WorkoutSmartDatabase.Schema,
            name = "workout_smart.db"
        )
    }
}

