package com.example.top10downloader

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class ViewHolder(v: View) {
    val tvName: TextView = v.findViewById(R.id.tvName)
    val tvArtist: TextView = v.findViewById(R.id.tvArtist)
    val tvSummary: TextView = v.findViewById(R.id.tvSummary)
    val tvImage: TextView = v.findViewById(R.id.tvImage)
}


class FeedAdapter(
    context: Context,
    private val resource: Int,
    private var applications: List<FeedEntry>
) : ArrayAdapter<FeedEntry>(context, resource) {
    private val inflater =                  //layout inflater is used to bring the xml to display the widgets onto the screen.creates all the view objects described in the xml
        LayoutInflater.from(context) //rather than retrieving the inflater to use it anytime
    //we need it,we rather store it and call it when and where is needed.
    //we need a context which we can request the layout inflater from

    fun setFeedList(feedList: List<FeedEntry>){
        this.applications = feedList
        notifyDataSetChanged()
    }

    override fun getView(  //the getView method is called by the listView anytime it wants another item to be displayed.
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {  //if a list view has a view that it can reuse, it passes a
        //reference to it in the convertView. However, a view can only be reused if the current view is scrolled off, so we have to check if the convertView is null
        //meaning it only creates a new view if convertView is null. Log.d(TAG, "getView() called")
        val view: View
        val viewHolder: ViewHolder
        if (convertView == null) {
            view = inflater.inflate(
                resource, parent, false
            )
            viewHolder = ViewHolder(view)
            view.tag =
                viewHolder//(create a new viewHolder object and store it in the view's tag using the setTag method)
            //inflates a new view anytime the item view is called which is changed by the if statement rather we want the listView to be reused whenever it is scrolled off.
        } else {
            view = convertView
            viewHolder =
                view.tag as ViewHolder    //if we have been given back an existing view by the listView ,then we're retrieving viewHolder from its tag
            //using the getTag method.Here we're just accessing the property in view.tag as ViewHolder, placing that in the viewHolder.The tag as an object needs
            //to be casted to the viewHolder and since the android framework doesn't use the tag field we can be sure that whatever stored in it won't be touched by android.
        }
//        val tvName: TextView = view.findViewById(R.id.tvName)
//        val tvArtist: TextView = view.findViewById(R.id.tvArtist)
//        val tvSummary: TextView = view.findViewById(R.id.tvSummary)

        //rather than findViewById every time to search for the widgets every time they are needed, the widgets haven't changed
        // since last time,so it makes sense to store the references somewhere and use it to store references to the text view widgets
        //rather than searching for them every time.That's what the viewHolder pattern does, a small class to store all the views that we
        //found the last time.

        val currentApp = applications[position]


        viewHolder.tvName.text = currentApp.name
        viewHolder.tvArtist.text = currentApp.artist
        viewHolder.tvSummary.text = currentApp.summary
        viewHolder.tvImage.text = currentApp.imageURL



        return view

    }

    override fun getCount(): Int {
        return applications.size
    }
}