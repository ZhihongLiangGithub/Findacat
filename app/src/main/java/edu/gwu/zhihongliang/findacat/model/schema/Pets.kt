package edu.gwu.zhihongliang.findacat.model.schema

import com.squareup.moshi.Json

data class Pets(@Json(name = "pet") val pet: List<PetItem>)