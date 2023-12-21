package com.dicoding.myiconnect.ui.videoplayer.api


import com.dicoding.myiconnect.ui.videoplayer.dictionary.DataItem
import com.dicoding.myiconnect.ui.videoplayer.dictionary.ResponseDictionary
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("library")
    fun getLibrary() : Call<ResponseDictionary>

}