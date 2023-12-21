package com.dicoding.myiconnect.ui.homefragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.myiconnect.R
import com.dicoding.myiconnect.databinding.FragmentHomeBinding
import com.dicoding.myiconnect.ui.adapter.ProdukAdapter
import com.dicoding.myiconnect.ui.dictionaryfragment.DictionaryFragment
import com.dicoding.myiconnect.ui.home.MainActivity
import com.dicoding.myiconnect.ui.login.LoginActivity
import com.dicoding.myiconnect.ui.model.ModelProduk
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var rvBaju: RecyclerView
    private lateinit var dictionaryFragment: DictionaryFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkUserLoginStatus()
        dictionaryFragment = DictionaryFragment()

        binding.checkDictionaryButton.setOnClickListener {
            switchToDictionaryFragment()
        }

        binding.checkTranslateButton.setOnClickListener {
            switchToTranslateFragment()
        }

        initRecyclerView()
        showUserInfo()
    }

    private fun checkUserLoginStatus() {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser == null) {
            // Jika pengguna belum login, arahkan kembali ke layar login
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish() // Tutup activity saat ini
        }
    }
    private fun initRecyclerView() {
        rvBaju = binding.rvBaju
        rvBaju.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        val adapterBaju = ProdukAdapter(arrayBaju, requireActivity())
        rvBaju.adapter = adapterBaju
    }

    private val arrayBaju: ArrayList<ModelProduk>
        get() {
            val arraybaju = ArrayList<ModelProduk>()
            arraybaju.add(ModelProduk("Mohon/Tolong", "Ekspresi Permintaan", R.drawable.please))
            arraybaju.add(ModelProduk("Tidak", "Ekspresi Penolakan", R.drawable.no))
            arraybaju.add(ModelProduk("Ya", "Ekspresi Persetujuan", R.drawable.yes))
            arraybaju.add(ModelProduk("Sampai Jumpa", "Ekspresi Perpisahan", R.drawable.goodbye))
            arraybaju.add(ModelProduk("Terima Kasih", "Apresiasi", R.drawable.thankyou))
            arraybaju.add(ModelProduk("Halo", "Kata Salam", R.drawable.hello))
            return arraybaju
        }

    private fun showUserInfo() {
        val auth = FirebaseAuth.getInstance()
        val currentUser: FirebaseUser? = auth.currentUser
        val username = currentUser?.displayName ?: "Pengguna Tidak Tersedia"
        binding.edtName.text = username
    }

    private fun switchToDictionaryFragment() {
        val mainActivity = activity as? MainActivity
        mainActivity?.switchToDictionaryFragment()
    }

    private fun switchToTranslateFragment() {
        val mainActivity = activity as? MainActivity
        mainActivity?.switchToTranslateFragment()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
