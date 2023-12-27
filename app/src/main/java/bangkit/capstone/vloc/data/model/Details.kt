package bangkit.capstone.vloc.data.model

import com.google.gson.annotations.SerializedName

data class Details(

	@field:SerializedName("more_img_url3")
	val moreImgUrl3: String,

	@field:SerializedName("more_img_url1")
	val moreImgUrl1: String,

	@field:SerializedName("more_img_url2")
	val moreImgUrl2: String,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("destionation")
	val destionation: Destionation,

	@field:SerializedName("status")
	val status: String
)

data class Destionation(

	@field:SerializedName("address")
	val address: String,

	@field:SerializedName("fee")
	val fee: Any,

	@field:SerializedName("latitude")
	val latitude: Any,

	@field:SerializedName("rating")
	val rating: Any,

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

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("predict_number")
	val predictNumber: Int,

	@field:SerializedName("category")
	val category: String,

	@field:SerializedName("longitude")
	val longitude: Any
)
