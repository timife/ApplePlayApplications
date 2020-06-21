package com.example.top10downloader

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import kotlin.properties.Delegates

private const val TAG = "DownloadData"


 class DownloadData(private val callBack: DownloaderCallBack) :
    AsyncTask<String, Void, String>() {    //made a companion object to avoid memory leaks so we no longer have access to our main activity class therefore we needed to create new instances for our main activity from the companion object.

   interface DownloaderCallBack{
       fun onDataAvailable(data: List<FeedEntry>)
   }
    override fun onPostExecute(result: String) {
        val parseApplications = ParseApplications()
        if(result.isNotEmpty()){
            parseApplications.parse(result)
        }
        callBack.onDataAvailable(parseApplications.applications)
        //to run the app, we are to call the parse method in our activity
    }

    //when creating the array adapter, android studio suggests that we need to provide the context and this asks for the reference to the activity instance
    //which is "this", now our code is not running inside the main activity because it is in a companion object it has no access to the main activity. But we can create
    //an instance of the main activity class when we create the downloadData object.

    override fun doInBackground(vararg url: String
    ): String {
        Log.d(TAG, "doInBackground: starts with ${url[0]}")
        val rssfeed = downloadXML(url[0])
        if (rssfeed.isEmpty()) {
            Log.e(TAG, "doInBackground:Error downloading")
        }
        return rssfeed
    }

    private fun downloadXML(urlPath: String): String {
        try{
        return URL(urlPath).readText()
    }catch (e: MalformedURLException){
            Log.d(TAG, "downloadXML: Invalid URL" + e.message)
        }catch (e: IOException){
            Log.d(TAG, "downloadXML: IO Exception reading data" + e.message)
        }catch (e: SecurityException){
            Log.d(TAG, "downloadXML: Security Exception. Needs permission?" + e.message)
//            e.printStackTrace()
        }
        return ""   //return an empty string if there was an exception.
    }
}