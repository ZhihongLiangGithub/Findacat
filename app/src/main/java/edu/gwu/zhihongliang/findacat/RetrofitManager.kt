package edu.gwu.zhihongliang.findacat

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class RetrofitManager {
    companion object {
        fun getCatFactsInstance(): Retrofit {
            return Retrofit.Builder()
                    .baseUrl(Const.CATFACTS_URL)
                    .addConverterFactory(MoshiConverterFactory.create(MoshiManager.getInstance()))
                    .build()
        }

        fun getPetfinderInstance(): Retrofit {
            return Retrofit.Builder()
                    .baseUrl(Const.PETFINDER_URL)
                    .addConverterFactory(MoshiConverterFactory.create(MoshiManager.getInstance()))
                    .build()
        }
    }
}