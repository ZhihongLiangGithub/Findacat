package edu.gwu.zhihongliang.findacat.model.schema

import com.squareup.moshi.Json

data class Photos(@Json(name = "photo") val photo: List<PhotoItem>)