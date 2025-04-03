package eg.iti.mad.climaguard.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val uuid: String,
    val locationName: String,
    val latitude: Double,
    val longitude: Double,
    val dateTime: Long,
    val type: String // "Notification" or "Alarm"
)
