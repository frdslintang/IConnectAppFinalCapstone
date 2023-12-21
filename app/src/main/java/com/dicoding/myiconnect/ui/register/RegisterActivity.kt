package com.dicoding.myiconnect.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.dicoding.myiconnect.databinding.ActivityRegisterBinding
import com.dicoding.myiconnect.ui.home.MainActivity
import com.dicoding.myiconnect.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRegisterBinding
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()

        binding.tvToLogin.setOnClickListener {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                // Jika pengguna sedang login, arahkan ke MainActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // Jika pengguna tidak login (logout), arahkan ke LoginActivity
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        binding.btnRegister.setOnClickListener {
            val fullName = binding.edtFullnameRegister.text.toString().trim()
            val email = binding.edtEmailRegister.text.toString().trim()
            val password = binding.edtPasswordRegister.text.toString().trim()
            val confirmPassword = binding.edtConfirmPasswordRegister.text.toString().trim()

            // Validasi input

            if (fullName.isEmpty()){
                // Validasi untuk nama
                binding.edtFullnameRegister.error = "Nama Tidak Boleh Kosong"
                binding.edtFullnameRegister.requestFocus()
                return@setOnClickListener
            }

            if (email.isEmpty()){
                // Validasi untuk email
                binding.edtEmailRegister.error = "Email Tidak Boleh Kosong"
                binding.edtEmailRegister.requestFocus()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                // Validasi untuk format email
                binding.edtEmailRegister.error = "Email Tidak Valid"
                binding.edtEmailRegister.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty() || password.length < 8){
                // Validasi untuk password
                binding.edtPasswordRegister.error = "Password Tidak Boleh Kosong dan Lebih dari 8 Karakter"
                binding.edtPasswordRegister.requestFocus()
                return@setOnClickListener
            }

            if (password != confirmPassword){
                // Validasi untuk konfirmasi password
                binding.edtConfirmPasswordRegister.error = "Password Tidak Sama"
                binding.edtConfirmPasswordRegister.requestFocus()
                return@setOnClickListener
            }

            registerUser(email, password, fullName)
        }
    }

    private fun registerUser(email: String, password: String, fullName: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val profileUpdates = userProfileChangeRequest {
                        displayName = fullName
                    }

                    user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { updateProfileTask ->
                            if (updateProfileTask.isSuccessful) {
                                Toast.makeText(this, "Registrasi Berhasil", Toast.LENGTH_SHORT).show()
                                // Arahkan ke halaman login setelah registrasi berhasil
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                                finish() // Tutup halaman registrasi
                            } else {
                                Toast.makeText(this, "Gagal memperbarui profil", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Registrasi Gagal", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
