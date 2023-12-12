package bangkit.capstone.vloc.data.local.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import bangkit.capstone.vloc.data.model.ListDestinationItem

@Dao
interface VLocDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(destination: List<ListDestinationItem>)

    @Query("SELECT * FROM destination")
    fun getAllStory(): PagingSource<Int, ListDestinationItem>

    @Query("DELETE FROM destination")
    suspend fun deleteAll()
}