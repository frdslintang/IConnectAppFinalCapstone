package com.dicoding.myiconnect.ui.dictionaryfragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.myiconnect.databinding.FragmentDictionaryBinding
import com.dicoding.myiconnect.ui.videoplayer.api.ApiConfig
import com.dicoding.myiconnect.ui.videoplayer.dictionary.DataItem
import com.dicoding.myiconnect.ui.videoplayer.dictionary.ResponseDictionary
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DictionaryFragment : Fragment() {

    private var _binding: FragmentDictionaryBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: DictionaryAdapter
    private var dataList: List<DataItem?> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDictionaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews()
        setupRecyclerView()
        fetchDataFromAPI()
    }

    private fun initializeViews() {
        val searchEditText: EditText = binding.searchEditText

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

    }

    private fun setupRecyclerView() {
        val recyclerView: RecyclerView = binding.videoRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = DictionaryAdapter(dataList, this)
        adapter.loadAllData()
        recyclerView.adapter = adapter
    }

    private fun fetchDataFromAPI() {
        val apiService = ApiConfig.getService()
        val call: Call<ResponseDictionary> = apiService.getLibrary()

        call.enqueue(object : Callback<ResponseDictionary> {
            override fun onResponse(
                call: Call<ResponseDictionary>,
                response: Response<ResponseDictionary>
            ) {
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        dataList =
                            data.data ?: emptyList() // Update dataList with the received data
                        adapter.setData(dataList) // Set data to adapter
                        adapter.loadAllData() // Load all data to adapter
                    }
                } else {
                    // Handle when response code is not 200
                }
            }

            override fun onFailure(call: Call<ResponseDictionary>, t: Throwable) {
                // Handle error when failing to connect to the API server
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        adapter.releasePlayers()
    }
}