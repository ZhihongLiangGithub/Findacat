package edu.gwu.zhihongliang.findacat.ui.activity

import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.text.InputType
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import edu.gwu.zhihongliang.findacat.LocationDetector
import edu.gwu.zhihongliang.findacat.R
import edu.gwu.zhihongliang.findacat.api.CatFactsApi
import edu.gwu.zhihongliang.findacat.ui.fragment.FavouriteFragment
import edu.gwu.zhihongliang.findacat.ui.fragment.FindFragment
import edu.gwu.zhihongliang.findacat.util.ConnectivityUtil
import edu.gwu.zhihongliang.findacat.util.NotifyUtil
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(),
        BottomNavigationView.OnNavigationItemSelectedListener,
        CatFactsApi.OnCompleteListener {

    private val TAG = "MainActivity"
    private var prevBottomNaviSelected = -1
    private val catFacts = CatFactsApi(this)

    companion object {
        val KEY_SEARCH_ZIP = "zip"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //set up toolbar
        setSupportActionBar(main_toolbar)
        this.setTitle(R.string.app_name)
        //set up bottom navigation
        bottomNavigationView.setOnNavigationItemSelectedListener(this)
        //check permission
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //request permission
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LocationDetector.LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            bottomNavigationView.selectedItemId = R.id.navi_home
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            LocationDetector.LOCATION_PERMISSION_REQUEST_CODE -> {
                if (permissions.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "ACCESS_FINE_LOCATION permission granted")
                    prevBottomNaviSelected = -1
                    bottomNavigationView.selectedItemId = R.id.navi_home
                } else {
                    //permission denied
                    finish()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        // set up search zip
        val searchZip = menu?.findItem(R.id.menu_search_zip)
        val searchView = searchZip?.actionView as SearchView
        searchView.apply {
            queryHint = getString(R.string.enter_zip)
            inputType = InputType.TYPE_CLASS_NUMBER
            isIconified = true
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    Log.i(TAG, query)
                    // get zip
                    val regex = "\\d{5}".toRegex()
                    val result = regex.matchEntire(query ?: "")
                    if (!ConnectivityUtil.isConnected(context)) {
                        NotifyUtil.internetNotConnected(context)
                    } else if (result != null) {
                        val zip = result.value
                        Log.i(TAG, "search zip: $zip")
                        prevBottomNaviSelected = R.id.navi_home
                        bottomNavigationView.selectedItemId = R.id.navi_home
                        // open FindFragment with argument
                        val fragment = FindFragment.newInstance().apply {
                            arguments = Bundle().apply { putString(KEY_SEARCH_ZIP, zip) }
                        }
                        supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
                        searchView.isIconified = true
                        searchZip.collapseActionView()
                    } else {
                        NotifyUtil.showToast(context, getString(R.string.zip_invalid))
                    }
                    return true
                }

                override fun onQueryTextChange(s: String?): Boolean {
                    return true
                }
            })
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_cat_fact -> catFacts.getFactData()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun catFactSuccess(fact: String) {
        AlertDialog.Builder(this)
                .setTitle(R.string.cat_fact).setMessage(fact).show()
    }

    override fun catFactFail() {
        Log.e(TAG, "Cat Fact Api failure!")
        if (!ConnectivityUtil.isConnected(this)) {
            NotifyUtil.internetNotConnected(this)
        } else {
            NotifyUtil.showToast(this, getString(R.string.cat_fact_failure))
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.navi_home -> if (prevBottomNaviSelected != R.id.navi_home) {
                //open find cat fragment
                supportFragmentManager.beginTransaction().replace(R.id.container, FindFragment.newInstance()).commit()
                prevBottomNaviSelected = R.id.navi_home
            }

            R.id.navi_favourite -> if (prevBottomNaviSelected != R.id.navi_favourite) {
                //open favourite cat fragment
                supportFragmentManager.beginTransaction().replace(R.id.container, FavouriteFragment.newInstance()).commit()
                prevBottomNaviSelected = R.id.navi_favourite
            }
            else -> return false
        }
        return true
    }

}
