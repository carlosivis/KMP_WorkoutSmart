package dev.carlosivis.workoutsmart.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import dev.carlosivis.workoutsmart.database.WorkoutSmartDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual fun platformModule() = module {
    single<SqlDriver> {
        AndroidSqliteDriver(
            schema = WorkoutSmartDatabase.Schema,
            context = androidContext(),
            name = "workout_smart.db"
        )
    }
}

