package edu.gwu.zhihongliang.findacat.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import edu.gwu.zhihongliang.findacat.PersistenceManager
import edu.gwu.zhihongliang.findacat.R
import edu.gwu.zhihongliang.findacat.model.CatInfo
import edu.gwu.zhihongliang.findacat.ui.activity.PetDetailActivity
import edu.gwu.zhihongliang.findacat.ui.adapter.CatInfoItemAdapter
import kotlinx.android.synthetic.main.fragment_favourite.*


class FavouriteFragment : Fragment(), CatInfoItemAdapter.OnItemClickListener {

    private lateinit var persistenceManager: PersistenceManager

    lateinit var catInfoList: MutableList<CatInfo>

    companion object {
        @JvmStatic
        fun newInstance() = FavouriteFragment()

        val FAVOURITE_DETAIL_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        persistenceManager = PersistenceManager(activity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favourite, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //turn on progress bar
        progressBar.visibility = View.VISIBLE
        displayFavouriteCat()

    }

    private fun displayFavouriteCat() {
        catInfoList = persistenceManager.findAllFavouriteCats()
        if (!catInfoList.isEmpty()) {
            if (catInfo_rv != null) {
                catInfo_rv.adapter = CatInfoItemAdapter(catInfoList, activity, this)
            }
        }
        //turn off progress bar
        progressBar.visibility = View.INVISIBLE
    }

    override fun onItemClick(catInfo: CatInfo, itemView: View) {
        val intent = PetDetailActivity.newIntent(activity, catInfo)
        startActivityForResult(intent, FAVOURITE_DETAIL_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == FAVOURITE_DETAIL_REQUEST) {
            if (resultCode == PetDetailActivity.RESULT_NOT_FAVOURITE) {
                // if unfavourite a cat in PetDetailActivity, remove it from the list
                val id = data?.getStringExtra(PetDetailActivity.KEY_ID)
                catInfoList.removeIf { it.id == id }
                if (catInfo_rv != null) {
                    catInfo_rv.adapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onDetach() {
        persistenceManager.saveFavouriteCats(catInfoList)
        super.onDetach()
    }
}
