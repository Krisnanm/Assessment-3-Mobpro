package org.d3if3104.wof.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.d3if3104.wof.model.MessageResponse
import org.d3if3104.wof.model.Cars
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

private const val BASE_URL = "https://wheels-of-passion.vercel.app/"


private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()



private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()


interface UserApi {
    @Multipart
    @POST("cars/")
    suspend fun addData(
        @Part("nama_mobil") namaMobil: RequestBody,
        @Part("tipe_mobil") tipeMobil: RequestBody,
        @Part("tempat") tempat: RequestBody,
        @Part("user_email") userEmail: RequestBody,
        @Part file: MultipartBody.Part
    ): Cars
    @GET("cars/")
    suspend fun getAllData(
        @Query("email") email: String,
    ): List<Cars>

    @DELETE("cars/{car_id}")
    suspend fun deleteData(
        @Path("car_id") id: Int,
        @Query("email") email: String
    ): MessageResponse
}


object Api {
    val service: UserApi by lazy {
        retrofit.create(UserApi::class.java)
    }

    fun getImageUrl(imageId: String): String{
        return BASE_URL + "cars/images/$imageId"
    }
}

enum class ApiStatus { LOADING, SUCCESS, FAILED }