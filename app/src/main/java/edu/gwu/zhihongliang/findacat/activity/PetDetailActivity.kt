package edu.gwu.zhihongliang.findacat.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
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

        const val DATA = "data"

        fun newIntent(context: Context, catInfo: CatInfo): Intent {
            val intent = Intent(context, PetDetailActivity::class.java)
            intent.putExtra(DATA, catInfo)
            return intent
        }

        const val RESULT_NOT_FAVOURITE = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet_detail)
        setSupportActionBar(pet_detail_toolbar)
        catInfo = intent.getParcelableExtra(DATA)
        Picasso.with(this).load(catInfo.photo).into(imageView)
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
        val item = menu?.findItem(R.id.menu_favourite)
        item?.let {
            //init favourite icon
            when (isFavourite) {
                true -> it.setIcon(R.drawable.ic_outline_favorite_24px)
                else -> it.setIcon(R.drawable.ic_outline_favorite_border_24px)
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.menu_favourite -> {
                isFavourite = !isFavourite
                //change icon
                when (isFavourite) {
                    true -> item.setIcon(R.drawable.ic_outline_favorite_24px)
                    else -> item.setIcon(R.drawable.ic_outline_favorite_border_24px)
                }
                menuFavouriteSelected()
            }
            R.id.menu_share -> menuShareSelected()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun menuShareSelected(): Boolean {
        Log.i(TAG, "menu share selected")
        return true
    }

    private fun menuFavouriteSelected(): Boolean {
        Log.i(TAG, "menu favourite selected")
        if (isFavourite) {
            //add
            if (!favouriteCats.contains(catInfo)) favouriteCats.add(catInfo)
        } else {
            //delete
            favouriteCats.remove(catInfo)
        }
        return true
    }

    override fun onBackPressed() {
        //save sharePreference
        persistenceManager.saveFavouriteCats(favouriteCats)
        if (!isFavourite) {
            val returnIntent = Intent()
            returnIntent.putExtra("id", catInfo.id)
            setResult(RESULT_NOT_FAVOURITE, returnIntent)
        }
        super.onBackPressed()
    }

}
