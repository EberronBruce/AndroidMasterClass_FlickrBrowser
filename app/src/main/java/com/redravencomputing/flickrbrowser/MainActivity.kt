package com.redravencomputing.flickrbrowser

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.redravencomputing.flickrbrowser.databinding.ActivityMainBinding

private const val TAG = "FlickrMainActivity"

class MainActivity : BaseActivity(), GetRawData.OnDownloadComplete,
    GetFlickrJsonData.OnDataAvailable,
    RecyclerItemClickListener.OnRecyclerClickListener {

    private lateinit var binding: ActivityMainBinding
    private val flickrRecyclerViewAdapter = FlickrRecyclerViewAdapter(ArrayList())

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate called")
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activateToolbar(false)

        val recycler_view = findViewById<RecyclerView>(R.id.recycler_view)
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.addOnItemTouchListener(RecyclerItemClickListener(this, recycler_view, this))
        recycler_view.adapter = flickrRecyclerViewAdapter

        val url = createUri("https://www.flickr.com/services/feeds/photos_public.gne", "android, oreo", "en-us", true)
        Log.d(TAG, "This is the url: $url")
        val getRawData = GetRawData(this)
        // https://www.flickr.com/services/feeds/photos_public.gne?tags=android,oreo&format=json&nojsoncallback=1
        getRawData.execute(url)

        Log.d(TAG, "onCreate ends")
    }

    override fun onItemClick(view: View, position: Int) {
        Log.d(TAG, ".onItemClick: starts")
        Toast.makeText(this, "Normal tap at position: $position", Toast.LENGTH_SHORT).show()
    }

    override fun onItemLongClick(view: View, position: Int) {
        Log.d(TAG, ".onItemLongClick: starts")
//        Toast.makeText(this, "Long tap at position $position", Toast.LENGTH_SHORT).show()
        val photo = flickrRecyclerViewAdapter.getPhoto(position)
        if(photo != null) {
            val intent = Intent(this, PhotoDetailsActivity::class.java)
            intent.putExtra(PHOTO_TRANSFER, photo)
            startActivity(intent)
        }
    }

    private fun createUri(baseUrl: String, searchCriteria: String, lang: String, matchAll: Boolean) : String {
        Log.d(TAG, ".createUri starts")

        val uri = Uri.parse(baseUrl).
            buildUpon().
            appendQueryParameter("tags", searchCriteria).
            appendQueryParameter("tagmode", if (matchAll) "ALL" else "ANY").
            appendQueryParameter("lang", lang).
            appendQueryParameter("format", "json").
            appendQueryParameter("nojsoncallback", "1").
            build()

        return uri.toString()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        Log.d(TAG, "onCreateOptionsMenu called")
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Log.d(TAG, "onOptionsItemSelected called")
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

//    companion object {
//        private const val TAG = "MainActivity"
//    }

    override fun onDownloadComplete(data: String, status: DownloadStatus) {
        if (status == DownloadStatus.OK) {
            Log.d(TAG, "onDownloadComplete called")

            val getFlickrJsonData = GetFlickrJsonData(this)
            getFlickrJsonData.execute(data)
        } else {
            //download failed
            Log.d(TAG, "onDownloadComplete failed with status $status. Error message is: $data")
        }
    }

    override fun onDataAvailable(data: List<Photo>) {
        Log.d(TAG, ".OnDataAvailable called")
        flickrRecyclerViewAdapter.loadNewData(data)
        Log.d(TAG, ".onDataAvailable ends")
    }

    override fun onError(exception: Exception) {
        Log.e(TAG, "onError called with ${exception.message}")
    }
}