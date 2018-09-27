package edu.gwu.zhihongliang.findacat.model.schema


import com.squareup.moshi.Json


data class CatFactResponse(@Json(name = "fact") val fact: String)