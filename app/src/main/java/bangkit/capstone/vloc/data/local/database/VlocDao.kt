package bangkit.capstone.vloc.data.local.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import bangkit.capstone.vloc.data.model.LocationResponseItem

@Dao
interface VLocDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(destination: List<LocationResponseItem>)

    @Query("SELECT * FROM destination")
    fun getAllLocation(): PagingSource<Int, LocationResponseItem>

    @Query("DELETE FROM destination")
    suspend fun deleteAll()

}