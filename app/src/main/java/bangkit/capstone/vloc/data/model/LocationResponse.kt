package bangkit.capstone.vloc.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class LocationResponse(

	@field:SerializedName("LocationResponse")
	val locationResponse: List<LocationResponseItem>
)

@Entity(tableName = "destination")
data class LocationResponseItem(

	@field:SerializedName("address")
	val address: String,

	@field:SerializedName("fee")
	val fee: String,

	@field:SerializedName("rating")
	val rating: String,

	@field:SerializedName("link_to_gmaps")
	val linkToGmaps: String,

	@field:SerializedName("open_time")
	val openTime: String,

	@field:SerializedName("updateAt")
	val updateAt: String,

	@field:SerializedName("destination_name")
	val destinationName: String,

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("destination_img_url")
	val destinationImgUrl: String,

	@field:SerializedName("destination_details")
	val destinationDetails: String,

	@PrimaryKey
	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("predict_number")
	val predictNumber: Int,

	@field:SerializedName("category")
	val category: String
)
