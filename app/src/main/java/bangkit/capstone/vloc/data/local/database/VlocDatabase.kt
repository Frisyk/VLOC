package bangkit.capstone.vloc.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import bangkit.capstone.vloc.data.model.LocationResponseItem

@Database(
    entities = [LocationResponseItem::class, RemoteKeys::class, Favorites::class],
    version = 3,
    exportSchema = false
)
abstract class VlocDatabase : RoomDatabase() {

    abstract fun vlocDao(): VLocDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    abstract fun favoritesDao(): FavoritesDao
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