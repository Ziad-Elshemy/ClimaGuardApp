package eg.iti.mad.climaguard.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "favorite_locations")
data class LocationEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val country: String,
    val lat: Double,
    val lon: Double
)