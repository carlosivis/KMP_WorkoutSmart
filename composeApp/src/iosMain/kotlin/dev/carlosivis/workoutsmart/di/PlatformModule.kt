package dev.carlosivis.workoutsmart.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import dev.carlosivis.workoutsmart.database.WorkoutSmartDatabase
import dev.carlosivis.workoutsmart.screens.components.expect.IosVibratorHelper
import dev.carlosivis.workoutsmart.screens.components.expect.VibratorHelper
import org.koin.dsl.module

actual fun platformModule() = module {
    single<SqlDriver> {
        NativeSqliteDriver(
            schema = WorkoutSmartDatabase.Schema,
            name = "workout_smart.db"
        )
    }
    single<VibratorHelper> { IosVibratorHelper() }
}

