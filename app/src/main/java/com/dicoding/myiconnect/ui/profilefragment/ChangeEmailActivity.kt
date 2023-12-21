package com.dicoding.myiconnect.ui.profilefragment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.dicoding.myiconnect.databinding.ActivityChangeEmailBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

import java.util.regex.Pattern

class ChangeEmailActivity : AppCompatActivity() {

    lateinit var binding : ActivityChangeEmailBinding
    lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        binding.cvChangeEmailInputPass.visibility = View.VISIBLE
        binding.cvChangeEmail.visibility = View.GONE

        binding.btnNext.setOnClickListener {
            var pass = binding.edtChangeEmailPassword.text.toString()

            if (pass.isEmpty()) {
                binding.edtChangeEmailPassword.error = "Password Harus Terisi"
                binding.edtChangeEmailPassword.requestFocus()
                return@setOnClickListener
            }

            // cek password
            user.let {
                val userCredential = EmailAuthProvider.getCredential(it?.email!!,pass)
                it.reauthenticate(userCredential).addOnCompleteListener { task ->
                    when {
                        task.isSuccessful -> {
                            binding.cvChangeEmailInputPass.visibility = View.GONE
                            binding.cvChangeEmail.visibility = View.VISIBLE
                        }
                        task.exception is FirebaseAuthInvalidCredentialsException -> {
                            binding.edtChangeEmailPassword.error = "Password Salah"
                            binding.edtChangeEmailPassword.requestFocus()
                        }
                        else -> {
                            Toast.makeText(this, "${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        binding.btnChangeEmail.setOnClickListener newEmail@{
            var newEmail = binding.edtChangeEmail.text.toString()

            if (newEmail.isEmpty()){
                binding.edtChangeEmail.error = "Email Harus Terisi"
                binding.edtChangeEmail.requestFocus()
                return@newEmail
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                binding.edtChangeEmail.error = "Email Tidak Valid"
                binding.edtChangeEmail.requestFocus()
                return@newEmail
            }

            user?.let {
                user.updateEmail(newEmail).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "Email Berhasil Diubah", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, ProfileFragment::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }

    }
}