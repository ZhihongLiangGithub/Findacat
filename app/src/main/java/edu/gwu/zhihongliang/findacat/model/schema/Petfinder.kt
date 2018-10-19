package edu.gwu.zhihongliang.findacat.model.schema

import com.squareup.moshi.Json

data class Petfinder(@Json(name = "pets") val pets: Pets?,
                     @Json(name = "header") val header: Header)