package com.dicoding.myiconnect.ui.videoplayer.dictionary


import com.google.gson.annotations.SerializedName

data class ResponseDictionary(

	@field:SerializedName("data")
	val data: List<DataItem?>? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class DataItem(

	@field:SerializedName("thumbnail")
	val thumbnail: String? = null,

	@field:SerializedName("sourceLink")
	val sourceLink: String? = null,

	@field:SerializedName("word")
	val word: String? = null
)

