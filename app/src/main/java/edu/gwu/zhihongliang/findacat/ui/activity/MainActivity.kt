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
import edu.gwu.zhihongliang.findacat.R
import edu.gwu.zhihongliang.findacat.api.CatFactsApiEndpoint
import edu.gwu.zhihongliang.findacat.model.schema.CatFactResponse
import edu.gwu.zhihongliang.findacat.ui.fragment.FavouriteFragment
import edu.gwu.zhihongliang.findacat.ui.fragment.FindFragment
import edu.gwu.zhihongliang.findacat.util.ConnectivityUtil
import edu.gwu.zhihongliang.findacat.util.NotifyUtil
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity(),
        BottomNavigationView.OnNavigationItemSelectedListener {

    private val TAG = "MainActivity"
    private var prevBottomNaviSelected = -1
    private val MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1

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
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        } else {
            bottomNavigationView.selectedItemId = R.id.navi_home
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                if (permissions.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "ACCESS_FINE_LOCATION permission granted")
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
        searchView.queryHint = getString(R.string.enter_zip)
        searchView.inputType = InputType.TYPE_CLASS_NUMBER
        searchView.isIconified = true
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                Log.i(TAG, query)
                // get zip
                val regex = """
                    \d{5}
                """.trimIndent().toRegex()
                val result = regex.find(query)
                if (!ConnectivityUtil.isConnected(this@MainActivity)) {
                    NotifyUtil.internetNotConnected(this@MainActivity)
                } else if (result != null) {
                    val zip = result.value
                    Log.i(TAG, "zip: $zip")
                    // open FindFragment
                    prevBottomNaviSelected = R.id.navi_home
                    bottomNavigationView.selectedItemId = R.id.navi_home
                    val args = Bundle()
                    args.putString(KEY_SEARCH_ZIP, zip)
                    val fragment = FindFragment.newInstance()
                    fragment.arguments = args
                    supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
                    searchView.isIconified = true
                    searchZip.collapseActionView()
                } else {
                    NotifyUtil.showToast(this@MainActivity, getString(R.string.zip_invalid))
                }
                return true
            }

            override fun onQueryTextChange(s: String): Boolean {
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_cat_fact -> displayCatFact()
        }
        return super.onOptionsItemSelected(item)
    }


    private fun displayCatFact() {
        CatFactsApiEndpoint.apiEndPoint.getFact()
                .enqueue(object : Callback<CatFactResponse> {
                    override fun onFailure(call: Call<CatFactResponse>?, t: Throwable?) {
                        Log.e(TAG, "Cat Fact Api failure!", t)
                        if (!ConnectivityUtil.isConnected(this@MainActivity)) {
                            NotifyUtil.internetNotConnected(this@MainActivity)
                        } else {
                            NotifyUtil.showToast(this@MainActivity, getString(R.string.cat_fact_failure))
                        }
                    }

                    override fun onResponse(call: Call<CatFactResponse>, response: Response<CatFactResponse>) {
                        val result = response.body()
                        if (result != null) {
                            // show cat fact
                            AlertDialog.Builder(this@MainActivity)
                                    .setTitle(R.string.cat_fact).setMessage(result.fact).show()
                        } else {
                            if (!ConnectivityUtil.isConnected(this@MainActivity)) {
                                NotifyUtil.internetNotConnected(this@MainActivity)
                            } else {
                                NotifyUtil.showToast(this@MainActivity, getString(R.string.cat_fact_failure))
                            }
                        }
                    }
                })
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
