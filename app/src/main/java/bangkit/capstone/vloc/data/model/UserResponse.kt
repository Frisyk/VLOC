package bangkit.capstone.vloc.data.model

import com.google.gson.annotations.SerializedName
import retrofit2.http.Field

data class UserModel(
	val id: Int,
	val name: String,
	val email: String,
	val token: String,
	val isLogin: Boolean = false
)

data class LoginResponse(

	@field:SerializedName("status")
	val status: String,

	@field:SerializedName("token")
	val token: String,

	@field:SerializedName("user_id")
	val userId: Int,

	@field:SerializedName("username")
	val username: String
)



data class PostResponse(

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)
data class UserResponseItem(

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("image")
	val image: String,

	@field:SerializedName("password")
	val password: String,

	@field:SerializedName("updateAt")
	val updateAt: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("email")
	val email: String,

	@field:SerializedName("username")
	val username: String
)
