package com.dicoding.myiconnect.ui.articelfragment


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.dicoding.myiconnect.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class DetailArticelFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail_articel, container, false)

        val ivDetailPhoto = view.findViewById<ImageView>(R.id.iv_detail_photo)
        val tvDetailTitle = view.findViewById<TextView>(R.id.tv_detail_title)
        val tvDetailDescription = view.findViewById<TextView>(R.id.tv_detail_description)
        val btnShare = view.findViewById<Button>(R.id.action_share)

        // Mendapatkan data dari bundle yang dikirim dari ArticelFragment
        val title = arguments?.getString("title")
        val description = arguments?.getString("description")
        val photoUrl = arguments?.getString("photo")

        // Menggunakan library Glide untuk menampilkan gambar dari URL
        Glide.with(this)
            .load(photoUrl)
            .into(ivDetailPhoto)

        // Menampilkan judul dan deskripsi pada TextView
        tvDetailTitle.text = title
        tvDetailDescription.text = description

        // Menambahkan fungsi bagi tombol Share
        btnShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Bagikan artikel ini: $title - $description")
            startActivity(Intent.createChooser(shareIntent, "Bagikan artikel via"))
        }

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).findViewById<BottomNavigationView>(R.id.nav_view).visibility = View.GONE
        val backButton = view.findViewById<ImageView>(R.id.backButton)
        // Implementasi kembali ke halaman sebelumnya (HomeFragment) saat tombol kembali ditekan
        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Mengembalikan BottomNavigationView ke Visible saat keluar dari DetailArticelFragment
        (requireActivity() as AppCompatActivity).findViewById<BottomNavigationView>(R.id.nav_view).visibility = View.VISIBLE
    }

}
