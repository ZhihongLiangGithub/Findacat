package edu.gwu.zhihongliang.findacat

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import edu.gwu.zhihongliang.findacat.model.schema.adapter.ObjectAsListJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class RetrofitManager {
    companion object {
        @JvmStatic
        fun getCatFactsInstance(): Retrofit {
            return Retrofit.Builder()
                    .baseUrl(Const.CATFACTS_URL)
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build()
        }

        @JvmStatic
        fun getPetfinderInstance(): Retrofit {
            val moshi = Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    .add(ObjectAsListJsonAdapterFactory())
                    .build()
            return Retrofit.Builder()
                    .baseUrl(Const.PETFINDER_URL)
                    .addConverterFactory(MoshiConverterFactory.create(moshi))
                    .build()
        }
    }
}