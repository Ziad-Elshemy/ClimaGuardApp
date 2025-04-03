package eg.iti.mad.climaguard.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import eg.iti.mad.climaguard.model.AlarmEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: AlarmEntity) :Long

    @Query("SELECT * FROM alarms")
    fun getAllAlarms(): Flow<List<AlarmEntity>>

    @Delete
    suspend fun deleteAlarm(alarm: AlarmEntity) :Int


    @Query("DELETE FROM alarms WHERE dateTime = :alarmId")
    suspend fun deleteAlarmById(alarmId: Long): Int

    @Query("DELETE FROM alarms WHERE uuid = :uuId")
    suspend fun deleteAlarmByUUId(uuId: String): Int

}
