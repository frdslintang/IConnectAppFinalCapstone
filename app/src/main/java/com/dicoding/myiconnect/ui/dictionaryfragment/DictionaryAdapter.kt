package com.dicoding.myiconnect.ui.dictionaryfragment

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.myiconnect.R
import com.dicoding.myiconnect.ui.videoplayer.dictionary.DataItem
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

class DictionaryAdapter(
    private var dataDictionary: List<DataItem?>,
    private val fragment: Fragment
) : RecyclerView.Adapter<DictionaryAdapter.MyViewHolder>() {

    private var filteredData: MutableList<DataItem?> = mutableListOf()
    private var playerList: MutableList<SimpleExoPlayer> = mutableListOf()

    init {
        filteredData.addAll(dataDictionary)
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val wordDictionary: TextView = view.findViewById(R.id.tvTitle)
        val playerView = view.findViewById<StyledPlayerView>(R.id.playerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_video, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.wordDictionary.text = filteredData[position]?.word
        val videoUrl = filteredData[position]?.sourceLink

        val player = SimpleExoPlayer.Builder(holder.playerView.context).build()
        playerList.add(player)

        holder.playerView.player = player
        val dataSourceFactory = DefaultDataSourceFactory(
            fragment.requireContext(),
            Util.getUserAgent(fragment.requireContext(), "YourApplicationName")
        )

        if (videoUrl != null) {
            val mediaItem = MediaItem.fromUri(Uri.parse(videoUrl))
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mediaItem)
            player.setMediaSource(mediaSource)
            player.prepare()
        }

        holder.itemView.setOnClickListener {
            val word = filteredData[position]?.word
            Toast.makeText(holder.itemView.context, "$word", Toast.LENGTH_SHORT).show()
        }
    }

    fun filter(keyword: String) {
        filteredData.clear()
        if (keyword.isEmpty()) {
            filteredData.addAll(dataDictionary)
        } else {
            for (item in dataDictionary) {
                if (item?.word?.contains(keyword, ignoreCase = true) == true) {
                    filteredData.add(item)
                }
            }
        }
        notifyDataSetChanged()
        releasePlayers() // Melepaskan pemutar saat data berubah
    }

    fun loadAllData() {
        filteredData.clear()
        filteredData.addAll(dataDictionary)
        notifyDataSetChanged()
    }

    fun setData(newData: List<DataItem?>) {
        dataDictionary = newData
        filteredData.clear()
        filteredData.addAll(dataDictionary)
        notifyDataSetChanged()
        releasePlayers() // Melepaskan pemutar saat data berubah
    }

    fun releasePlayers() {
        for (player in playerList) {
            player.release()
        }
        playerList.clear()
    }

    override fun getItemCount(): Int {
        return filteredData.size
    }

    override fun onViewRecycled(holder:MyViewHolder) {
        super.onViewRecycled(holder)
        holder.playerView.player = null
    }

}