package edu.gwu.zhihongliang.findacat.model.schema

import com.squareup.moshi.Json


data class Breeds(@Json(name = "breed") val breed: List<StringWrapper>)