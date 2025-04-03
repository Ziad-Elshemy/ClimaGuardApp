package eg.iti.mad.climaguard.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import eg.iti.mad.climaguard.model.AlarmEntity
import eg.iti.mad.climaguard.model.LocationEntity

@Database(entities = arrayOf(LocationEntity::class, AlarmEntity::class), version = 1, exportSchema = false)
abstract class MyDatabase :RoomDatabase() {


    abstract fun locationDao(): LocationDao
    abstract fun alarmDao(): AlarmDao

    companion object {
        @Volatile
        private var INSTANCE: MyDatabase? = null

        fun getInstance(context: Context): MyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MyDatabase::class.java,
                    "location_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }


}