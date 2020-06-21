package com.example.top10downloader

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*

private const val TAG = "FeedViewModel"

val EMPTY_FEED_LIST: List<FeedEntry> = Collections.emptyList()    //rather than create a new feedEntry object for initialization, we use
// the empty feed list , which is an empty list created by the collections class and it is immutable.

class FeedViewModel : ViewModel(), DownloadData.DownloaderCallBack {
    private var downloadData: DownloadData? =
        null
    private var feedCachedUrl = "INVALIDATED"

    private val feed = MutableLiveData<List<FeedEntry>>()
    val feedEntries: LiveData<List<FeedEntry>>
    get() = feed

    init {
        feed.postValue(EMPTY_FEED_LIST)
    }   //the mutableLiveData and LiveData hold the FeedEntry objects, but also return null, this cannot change unless
    //initialized immediately.

     fun downloadUrl(feedUrl: String) {
         Log.d(TAG,"downloadUrl: called with url $feedUrl")
        if (feedUrl != feedCachedUrl) {
            Log.d(TAG, "downloadUrl starting AsyncTask")
            downloadData = DownloadData(this)
            downloadData?.execute(feedUrl)
            feedCachedUrl =
                feedUrl  //so after downloading the data,we're storing the data that's just been downloaded in the feedCachedUrl field ready to be compared next time.
            Log.d(
                TAG,
                "downloadUrl done"
            ) //once the url is changed, that's what we'll be doing in the onCreate method, so we're calling a new downloadData object ,calling its execute method
            //with a new url.creating the private function is because duplicating is bad, so we're calling the same method in the onCreate:42 and onOptionsItemSelected:77
        } else {
            Log.d(TAG, "downloadUrl - URL not changed")
        }

    }
    fun invalidate(){                             //for the refresh button, downloading the URL again.
        feedCachedUrl = "INVALIDATE"      //indicate that the download should be performed again.
    }

    override fun onDataAvailable(data: List<FeedEntry>) {
        Log.d(TAG, "onDataAvailable called")
        feed.value = data
        Log.d(TAG,"onDataAvailable ends")
    }

    override fun onCleared() {
        Log.d(TAG,"onCleared: cancelling pending downloads")
        downloadData?.cancel(true)
    }
}