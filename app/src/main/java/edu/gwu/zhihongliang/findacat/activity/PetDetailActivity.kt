package edu.gwu.zhihongliang.findacat.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.squareup.picasso.Picasso
import edu.gwu.zhihongliang.findacat.R
import edu.gwu.zhihongliang.findacat.model.CatInfo
import kotlinx.android.synthetic.main.activity_pet_detail.*

class PetDetailActivity : AppCompatActivity() {

    companion object {

        const val DATA = "data"

        fun newIntent(context: Context, catInfo: CatInfo): Intent {
            val intent = Intent(context, PetDetailActivity::class.java)
            intent.putExtra(DATA, catInfo)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet_detail)
        val catInfo = intent.getParcelableExtra<CatInfo>(DATA)
        Picasso.with(this).load(catInfo.photo).into(imageView)
        name_tv.text = getString(R.string.cat_name, catInfo.name)
        gender_tv.text = getString(R.string.cat_gender, catInfo.sex.value)
        breed_tv.text = getString(R.string.cat_breed, catInfo.breeds.joinToString())
        zip_tv.text = getString(R.string.cat_zip, catInfo.zip)
        description_tv.text = catInfo.description
    }
}
