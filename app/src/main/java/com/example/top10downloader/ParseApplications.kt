package com.example.top10downloader

import android.util.Log
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory


class ParseApplications {
    private val TAG = "ParseApplications"
    val applications = ArrayList<FeedEntry>()


    fun parse(xmlData: String): Boolean {
        Log.d(TAG, "parse called with $xmlData")
        var status = true
        var inEntry = false
        var textValue =
            ""   //the textValue is used to store the value of each tag so that we can assign it to its appropriate feed entry object


        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val xpp = factory.newPullParser()   //making the XML into something that makes sense.
            xpp.setInput(xmlData.reader())    //the xmlPullParser needs a stringReader to read from and the xmlReader needs a stream/string to read which we have done here
            //when the pullParser processes its input various events happen like END_DOCUMENT and START_TAG
            var eventType = xpp.eventType
            var currentRecord = FeedEntry()
            while (eventType != XmlPullParser.END_DOCUMENT) {  //first we need to do is check the event and ensure that we have not reached the end of the Document i.e the XML
                val tagName = xpp.name?.toLowerCase()

                when (eventType) {

                    XmlPullParser.START_TAG -> {
                        Log.d(TAG, "parse:Starting tag for $tagName")
//                        if (tagName == "entry") {
//                            inEntry = true
//                        }
//                        else if(tagName == "title"){
//                            outEntry = true
//                        }
                        when(tagName){
                            "entry"-> inEntry=true
                            "title"-> inEntry=true
                        }
                       }//after the START_TAG, the next method is called ,we don't know what the next event is but,  as far as it is not the end of the document we go round the loop again

                    XmlPullParser.TEXT -> textValue =
                        xpp.text     //if the event type is TEXT that's the pullParser telling us that data is available, therefore the pullParser stores the data
                    //in a variable textValue and not directly in the applications object as the XML does not work that way. Stores a new text when a new text is available and doesn't do anything
                    //with it until there is an END_TAG event.


                    //Now the only way to know the text available is for a particular tag is to wait until there is an END_TAG event and store the data at that point

                    XmlPullParser.END_TAG -> {
//                        Log.d(TAG, "parse:Ending tag for $tagName")
                        if (inEntry) {
                            when (tagName) {
                                "entry" -> {
                                    applications.add(currentRecord)
                                    inEntry = false
                                    currentRecord = FeedEntry()  //create a new object
                                }//else method
                                "name" -> currentRecord.name = textValue
                                "artist" -> currentRecord.artist = textValue
                                "releasedate" -> currentRecord.releaseDate = textValue
                                "summary" -> currentRecord.summary = textValue
                                "image" -> currentRecord.imageURL =
                                    textValue  //checking if the tagName has been found in the XML and assign the value it to the appropriate feedEntry object
                            }
                        }
                    }

                }
                //Nothing else to do
                eventType = xpp.next()
            }
//            for (app in applications) {
//                Log.d(TAG, "*****************")
//                Log.d(TAG, app.toString())
//            }

        } catch (e: Exception) {
            e.printStackTrace()
            status = false
        }
        return status
    }
}



