package edu.gwu.zhihongliang.findacat.ui.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.squareup.picasso.Picasso
import edu.gwu.zhihongliang.findacat.PersistenceManager
import edu.gwu.zhihongliang.findacat.R
import edu.gwu.zhihongliang.findacat.model.CatInfo
import kotlinx.android.synthetic.main.activity_pet_detail.*


class PetDetailActivity : AppCompatActivity() {

    private val TAG = "PetDetailActivity"
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
        //set up toolbar
        setSupportActionBar(pet_detail_toolbar)
        pet_detail_toolbar.setNavigationOnClickListener { onBackPressed() }
        this.title = ""
        //fill content
        catInfo = intent.getParcelableExtra(KEY_DATA)
        Picasso.with(this).load(catInfo.photo).centerCrop().fit().into(imageView)
        name_tv.text = getString(R.string.cat_name, catInfo.name)
        gender_tv.text = getString(R.string.cat_gender, catInfo.sex.value)
        breed_tv.text = getString(R.string.cat_breed, catInfo.breeds.joinToString())
        zip_tv.text = getString(R.string.cat_zip, catInfo.zip)
        description_tv.text = catInfo.description
        //check if this cat is favourite
        isFavourite = favouriteCats.any { it.id == catInfo.id }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_pet_detail, menu)
        //init the icon of favourite
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
                //change icon
                item.setIcon(R.drawable.ic_outline_favorite_24px)
                //add a favourite cat
                if (!favouriteCats.contains(catInfo)) favouriteCats.add(catInfo)
            }
            else -> {
                item.setIcon(R.drawable.ic_outline_favorite_border_24px)
                //remove a favourite cat
                favouriteCats.remove(catInfo)
            }
        }
        return true
    }


    private fun menuMailSelected(): Boolean {
        val to = catInfo.email
        val text = getString(R.string.email_text, catInfo.name)
        val emailIntent = Intent().apply {
            action = Intent.ACTION_SEND
            data = Uri.parse("mailto:")
            type = "text/plain"
            putExtra(Intent.EXTRA_EMAIL, to)
            putExtra(Intent.EXTRA_SUBJECT, catInfo.name)
            putExtra(Intent.EXTRA_TEXT, text)
        }
        startActivity(Intent.createChooser(emailIntent, getString(R.string.mail)))
        return true
    }

    private fun menuShareSelected(): Boolean {
        val text = getString(R.string.share_text, catInfo.name, getString(R.string.app_name), catInfo.photo)
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
        //save sharePreference
        persistenceManager.saveFavouriteCats(favouriteCats)
        if (!isFavourite) {
            val returnIntent = Intent()
            returnIntent.putExtra(KEY_ID, catInfo.id)
            setResult(RESULT_NOT_FAVOURITE, returnIntent)
        }
        super.onBackPressed()
    }

}
