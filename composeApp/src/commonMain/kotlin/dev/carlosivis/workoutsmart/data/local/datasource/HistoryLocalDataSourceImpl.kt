package dev.carlosivis.workoutsmart.data.local.datasource

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.db.SqlDriver
import dev.carlosivis.workoutsmart.core.transactionWithContext
import dev.carlosivis.workoutsmart.database.WorkoutSmartDatabase
import dev.carlosivis.workoutsmart.models.HistoryModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class HistoryLocalDataSourceImpl(
    sqlDriver: SqlDriver,
    private val backgroundDispatcher: CoroutineDispatcher
): HistoryLocalDataSource {

    private val dbRef: WorkoutSmartDatabase = WorkoutSmartDatabase.Companion(sqlDriver)

    override fun getAllHistory(): Flow<List<HistoryModel>> = dbRef.workoutSmartDatabaseQueries
        .selectAllHistory()
        .asFlow()
        .mapToList(backgroundDispatcher)
        .map { historyEntries ->
            historyEntries.map { history ->
                HistoryModel(
                    id = history.id,
                    workoutName = history.workoutName,
                    date = history.date,
                    duration = history.duration
                )
            }
        }
        .flowOn(backgroundDispatcher)

    override suspend fun insertHistory(
        workoutName: String,
        date: Long,
        duration: Long
    ) {
        dbRef.transactionWithContext(backgroundDispatcher) {
            dbRef.workoutSmartDatabaseQueries.insertHistory(
                workoutName = workoutName,
                date = date,
                duration = duration
            )
        }
    }
}