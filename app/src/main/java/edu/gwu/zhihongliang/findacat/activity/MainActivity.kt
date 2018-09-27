package edu.gwu.zhihongliang.findacat.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import edu.gwu.zhihongliang.findacat.CatFactsApiClient
import edu.gwu.zhihongliang.findacat.PetfinderApiClient
import edu.gwu.zhihongliang.findacat.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val catFactApiClient = CatFactsApiClient(this, cat_fact_tv)
        catFactApiClient.parseCatFactData()
    }

    fun findCatBtnClicked(view: View) {
        val petfinderApiClient = PetfinderApiClient()
        petfinderApiClient.parsePetFindData("22202")
    }

    fun favouriteCatsBtnClicked(view: View) {

    }


}
