package edu.gwu.zhihongliang.findacat.model.schema

import com.squareup.moshi.Json


data class PetfinderResponse(@Json(name = "petfinder") val petfinder: Petfinder)