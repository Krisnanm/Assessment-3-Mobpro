package org.d3if3104.wof.model

import com.squareup.moshi.Json

data class Cars(
    val id: Int,
    val user_email: String,
    val nama_mobil: String,
    val tipe_mobil: String,
    val tempat: String,
    val image_id: String,
    val created_at: String
)