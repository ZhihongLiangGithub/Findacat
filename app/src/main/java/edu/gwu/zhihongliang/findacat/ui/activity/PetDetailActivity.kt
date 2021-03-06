package edu.gwu.zhihongliang.findacat.ui.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ShareCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.squareup.picasso.Picasso
import edu.gwu.zhihongliang.findacat.Const
import edu.gwu.zhihongliang.findacat.PersistenceManager
import edu.gwu.zhihongliang.findacat.R
import edu.gwu.zhihongliang.findacat.model.CatInfo
import kotlinx.android.synthetic.main.activity_pet_detail.*
import java.util.*


class PetDetailActivity : AppCompatActivity() {

    private lateinit var catInfo: CatInfo
    private var isFavourite = true
    private val persistenceManager: PersistenceManager by lazy {
        PersistenceManager(this)
    }
    private val favouriteCats: MutableList<CatInfo> by lazy {
        persistenceManager.findAllFavouriteCats()
    }

    companion object {

        const val KEY_DATA = "data"

        fun newIntent(context: Context, catInfo: CatInfo): Intent {
            val intent = Intent(context, PetDetailActivity::class.java)
            intent.putExtra(KEY_DATA, catInfo)
            return intent
        }

        const val RESULT_NOT_FAVOURITE = 2

        const val KEY_ID = "id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet_detail)
        // set up toolbar
        setSupportActionBar(pet_detail_toolbar)
        pet_detail_toolbar.setNavigationOnClickListener { onBackPressed() }
        this.title = ""
        // fill content
        catInfo = intent.getParcelableExtra(KEY_DATA)
        Picasso.with(this).load(catInfo.photo).resize(Const.DETAIL_IMAGE_SIZE, Const.DETAIL_IMAGE_SIZE).centerInside().into(imageView)
        name_tv.text = getString(R.string.cat_name, catInfo.name)
        gender_tv.text = getString(R.string.cat_gender, catInfo.sex)
        breed_tv.text = getString(R.string.cat_breed, catInfo.breeds.joinToString())
        zip_tv.text = getString(R.string.cat_zip, catInfo.zip)
        description_tv.text = catInfo.description
        // check if this cat is favourite
        isFavourite = favouriteCats.any { it.id == catInfo.id }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_pet_detail, menu)
        // init the icon of favourite
        val item = menu?.findItem(R.id.menu_favourite)
        item?.let {
            when (isFavourite) {
                true -> it.setIcon(R.drawable.ic_outline_favorite_24px)
                else -> it.setIcon(R.drawable.ic_outline_favorite_border_24px)
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.menu_favourite -> menuFavouriteSelected()
            R.id.menu_mail -> menuMailSelected()
            R.id.menu_share -> menuShareSelected()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun menuFavouriteSelected(): Boolean {
        isFavourite = !isFavourite
        val item = pet_detail_toolbar.menu.findItem(R.id.menu_favourite)
        when (isFavourite) {
            true -> {
                // change icon
                item.setIcon(R.drawable.ic_outline_favorite_24px)
                // add a favourite cat
                if (!favouriteCats.contains(catInfo)) favouriteCats.add(catInfo)
            }
            else -> {
                item.setIcon(R.drawable.ic_outline_favorite_border_24px)
                // remove a favourite cat
                favouriteCats.remove(catInfo)
            }
        }
        return true
    }


    private fun menuMailSelected(): Boolean {
        ShareCompat.IntentBuilder.from(this)
                .setType("message/rfc822")
                .addEmailTo(catInfo.email)
                .setSubject(catInfo.name)
                .setText(getString(R.string.email_text, catInfo.name))
                //.setHtmlText(body) //If you are using HTML in your body text
                .setChooserTitle(getString(R.string.mail))
                .startChooser()
        return true
    }

    private fun menuShareSelected(): Boolean {
        val text = if (Locale.getDefault().language == Locale.CHINA.language) { // "zh"
            getString(R.string.share_text, getString(R.string.app_name), catInfo.name, catInfo.photo)
        } else {
            getString(R.string.share_text, catInfo.name, getString(R.string.app_name), catInfo.photo)
        }
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_SUBJECT, catInfo.name)
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(sendIntent, getString(R.string.share)))
        return true
    }

    override fun onBackPressed() {
        // save sharePreference
        persistenceManager.saveFavouriteCats(favouriteCats)
        if (!isFavourite) {
            val returnIntent = Intent()
            returnIntent.putExtra(KEY_ID, catInfo.id)
            setResult(RESULT_NOT_FAVOURITE, returnIntent)
        }
        super.onBackPressed()
    }

}
