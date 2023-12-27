package bangkit.capstone.vloc.data.local.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavoritesDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(favorites: Favorites)

    @Delete
    suspend fun delete(favorites: Favorites)

    @Query("SELECT * from favorites ORDER BY timeStamp DESC")
    fun getFavorites(): LiveData<List<Favorites>>

    @Query("SELECT EXISTS(SELECT * from favorites where id = :id)")
    fun isFavoriteUser(id: String?): LiveData<Boolean>
}