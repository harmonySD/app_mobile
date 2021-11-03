package m1.pmob.veget_eau

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
        entities=[Eplante::class,Earrosage::class],version = 1,
)
@TypeConverters(DateConverter::class)
abstract class PlantsDatabase:RoomDatabase() {
    abstract fun myDao(): DaoBdPlante
    companion object {
        @Volatile
        private var instance: PlantsDatabase? = null
        fun getDatabase( context : Context): PlantsDatabase{
            if( instance != null )
                return instance!!
            val db = Room.databaseBuilder( context.applicationContext,
                    PlantsDatabase::class.java , "plants").build()
            instance = db
            return instance!!
        }
    }
}
