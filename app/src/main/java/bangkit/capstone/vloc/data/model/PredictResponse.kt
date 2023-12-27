package bangkit.capstone.vloc.data.model

import com.google.gson.annotations.SerializedName

data class PredictResponse(

	@field:SerializedName("result")
	val result: Result,

	@field:SerializedName("probabilitis")
	val probabilitis: String,

	@field:SerializedName("status")
	val status: String
)

data class Result(

	@field:SerializedName("address")
	val address: String,

	@field:SerializedName("destination_img_url")
	val destinationImgUrl: String,

	@field:SerializedName("destination_name")
	val destinationName: String,

	@field:SerializedName("id")
	val id: String
)
