package com.dicoding.myiconnect.ui.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class ModelProduk (
    var judulBisindo : String? = null,
    var jenisBisindo : String? = null,
    var gambarBisindo : Int = 0
) : Parcelable