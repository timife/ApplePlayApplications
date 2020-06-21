package com.example.top10downloader

import android.content.Context
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import android.widget.ListView
import android.widget.TextView
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URL
import kotlin.properties.Delegates

class FeedEntry {
    var name: String = ""
    var artist: String = ""
    var releaseDate: String = ""
    var summary: String = ""
    var imageURL: String = ""

    override fun toString(): String {
        return """
            name=$name
            artist=$artist
            releaseDate=$releaseDate
            imageURL=$imageURL
        """.trimIndent()
    }           //This is for us to see the actual data rather than the default representation object which has a hashcode rather than the expected data.
 }//This is done  because the class is not a long class, but if it is a long class, then a new class file should be created.

private const val TAG = "MainActivity"
private const val STATE_URL = "feedUrl"
private const val STATE_LIMIT = "feedLimit"

class MainActivity : AppCompatActivity() {
    //the AsyncTask can only be executed once,so we can set downloadData to be initialized every time we want the Task to be
    //executed to solve the problem.The only complication is that we need downloadData to be visible throughout our activity to be able to cancel it if necessary in the onDestroy method.
    //So to be able to cancel out tasks we have to use nullable type.It needed to be set to null here because in kotlin variables need to be initialized to something.
    private var feedUrl: String =
        "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml"
    private var feedLimit = 10
    private val feedViewModel: FeedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {    //when we're dealing with bundle in onCreate, we have to cater for it being null because it will be null someday.
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG,"onCreate called")

        val feedAdapter = FeedAdapter(this, R.layout.list_record, EMPTY_FEED_LIST)
        xmlListView.adapter = feedAdapter
        if (savedInstanceState!=null){

            feedUrl= savedInstanceState.getString(STATE_URL).toString()
            feedLimit=savedInstanceState.getInt(STATE_LIMIT)
        }

        feedViewModel.feedEntries.observe(
            this,
            Observer<List<FeedEntry>> { feedEntries -> feedAdapter.setFeedList(feedEntries ?: EMPTY_FEED_LIST) })

       feedViewModel. downloadUrl(feedUrl.format(feedLimit))
        Log.d(TAG, "onCreate:done")
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(
            R.menu.feeds_menu,
            menu
        )     //though to inflate a view in an adapter , it is necessary to call the inflater from the context,but
        //the mainActivity and the AppCompatActivity is the context, so we can just call the get inflater.

        if (feedLimit == 10) {
            menu?.findItem(R.id.mnu10)?.isChecked = true
        } else {
            menu?.findItem(R.id.mnu25)?.isChecked = true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {  //If we're not sure of what is going on in the background, we better use the safe-call operator.

        when (item.itemId) {                                                        //basically kotlin will ignore this when expression if item is null. But we can modify signatures
            R.id.mnuFree ->                                                           //if we are certain that the item is not null, here we can be certain because the function displays options when the options menu is clicked.
                feedUrl =
                    "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml"
            R.id.mnuPaid ->
                feedUrl =
                    "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml"
            R.id.mnuSongs ->
                feedUrl =
                    "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml"
            R.id.mnuAlbums ->
                feedUrl =
                    "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topalbums/limit=%d/xml"
            R.id.mnu10, R.id.mnu25 -> {
                if (!item.isChecked) {
                    item.isChecked = true
                    feedLimit = 35 - feedLimit
                    Log.d(
                        TAG,
                        "onOptionsItemSelected: ${item.title} setting feedLimit to $feedLimit"
                    )
                } else {
                    Log.d(TAG, "onOptionsItemSelected: ${item.title} setting feedLimit unchanged")
                }

            }
            R.id.mnuRefresh -> feedViewModel.invalidate()   //all that the mnuRefresh has to do is to set the feedCachedUrl to something different from the
            //previous Url. Here it is the initialized Url which is the INVALIDATED.
            else ->
                return super.onOptionsItemSelected(item)  //the else is important because the if not present and a submenu is present the code after the when is
            //executed.
        }
        feedViewModel.downloadUrl(feedUrl.format(feedLimit))
        return true
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STATE_URL, feedUrl)
        outState.putInt(STATE_LIMIT, feedLimit)
    }
}

// Note: First we add a listView widget to the layout, create a textView resource, create an instance of the adapter and then call the set adapter function to link

//the ListView to the adapter 



