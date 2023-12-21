package com.dicoding.myiconnect.ui.articelfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.myiconnect.R

class ArticelFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var articelAdapter: ListArticelAdapter
    private var itemsList: ArrayList<Items> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_articel, container, false)

        recyclerView = view.findViewById(R.id.rv_articel)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        val titles = resources.getStringArray(R.array.data_tittle)
        val descriptions = resources.getStringArray(R.array.data_description)
        val photoUrls = resources.getStringArray(R.array.data_photo_urls)

        for (i in titles.indices) {
            val item = Items(
                titles[i],
                descriptions[i],
                photoUrls[i]
            )
            itemsList.add(item)
        }

        articelAdapter = ListArticelAdapter(requireContext(), itemsList) { selectedItem ->
            val bundle = Bundle().apply {
                putString("title", selectedItem.title)
                putString("description", selectedItem.description)
                putString("photo", selectedItem.photoUrl)
            }

            val detailFragment = DetailArticelFragment()
            detailFragment.arguments = bundle

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit()
        }
        recyclerView.adapter = articelAdapter

        return view
    }


}
