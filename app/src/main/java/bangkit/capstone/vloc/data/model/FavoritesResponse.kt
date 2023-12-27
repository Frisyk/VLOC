package bangkit.capstone.vloc.data.model

import com.google.gson.annotations.SerializedName

data class FavoritesResponse(

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("favorite")
	val favorite: List<FavoriteItem>,

	@field:SerializedName("status")
	val status: String
)

data class FavoriteItem(

	@field:SerializedName("destination_img_url")
	val destinationImgUrl: String,

	@field:SerializedName("user_id")
	val userId: Int,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("location_id")
	val locationId: String
)
