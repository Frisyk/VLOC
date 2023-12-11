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
    suspend fun insertDestination(destination: List<ListDestinationItem>)

    @Query("SELECT * FROM destination")
    fun getAllDestination(): PagingSource<Int, ListDestinationItem>

    @Query("DELETE FROM destination")
    suspend fun deleteAll()
}