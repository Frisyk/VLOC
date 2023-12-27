package bangkit.capstone.vloc.data.local.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "favorites")
@Parcelize
data class Favorites(
    @PrimaryKey(autoGenerate = false)
    var id: String = "",
    var destinationUrl: String? = "",
    var timeStamp: Long = 0
) : Parcelable