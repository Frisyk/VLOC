package bangkit.capstone.vloc.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import bangkit.capstone.vloc.data.model.ListDestinationItem

@Database(
    entities = [ListDestinationItem::class, RemoteKeys::class],
    version = 2,
    exportSchema = false
)
abstract class VlocDatabase : RoomDatabase() {

    abstract fun vlocDao(): VLocDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        @Volatile
        private var INSTANCE: VlocDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): VlocDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    VlocDatabase::class.java, "vloc_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}