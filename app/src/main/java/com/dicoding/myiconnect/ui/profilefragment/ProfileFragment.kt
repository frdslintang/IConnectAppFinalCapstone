package com.dicoding.myiconnect.ui.profilefragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.dicoding.myiconnect.R
import com.dicoding.myiconnect.databinding.FragmentProfileBinding
import com.dicoding.myiconnect.ui.login.LoginActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.GoogleAuthProvider

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(requireContext())


        if (currentUser != null) {
            displayUserInfo(currentUser.displayName ?: "Nama Pengguna Tidak Tersedia", currentUser.email ?: "Email Pengguna")
        } else if (googleSignInAccount != null) {
            displayUserInfo(googleSignInAccount.displayName ?: "Nama Pengguna Tidak Tersedia", googleSignInAccount.email ?: "Email Pengguna")
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        binding.btnChangePass.setOnClickListener {
            changePass()
        }
        binding.btnVerify.setOnClickListener {
            emailVerification()
        }

        binding.btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun displayUserInfo(name: String, email: String) {
        binding.edtName.text = name
        binding.edtEmail.text = email
    }


    private fun emailVerification() {
        val user = auth.currentUser

        if (user?.providerData?.any { it.providerId == EmailAuthProvider.PROVIDER_ID } == true) {
            // Jika pengguna login menggunakan email, lakukan verifikasi email
            user.sendEmailVerification().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(activity, "Email Verifikasi Telah Dikirim", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(activity, "${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // Jika pengguna tidak login menggunakan email, beri peringatan atau tindakan yang sesuai
            Toast.makeText(activity, "Anda login menggunakan Google, verifikasi email tidak diperlukan.", Toast.LENGTH_LONG).show()
        }
    }


    private fun changePass() {
        val user = auth.currentUser

        if (user?.providerData?.any { it.providerId == GoogleAuthProvider.PROVIDER_ID } == true) {
            // Jika pengguna login menggunakan Google, beri peringatan untuk mengubah kata sandi di pengaturan akun Google
            Toast.makeText(activity, "Anda login menggunakan Google. Untuk mengubah kata sandi, gunakan pengaturan akun Google.", Toast.LENGTH_LONG).show()
            return
        }

        // Lanjutkan logika untuk pengguna yang login menggunakan email
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        binding.cvCurrentPass.visibility = View.VISIBLE

        binding.btnCancel.setOnClickListener {
            binding.cvCurrentPass.visibility = View.GONE
        }

        binding.btnConfirm.setOnClickListener {
            val pass = binding.edtCurrentPassword.text.toString()

            if (pass.isEmpty()) {
                binding.edtCurrentPassword.error = "Password Tidak Boleh Kosong"
                binding.edtCurrentPassword.requestFocus()
                return@setOnClickListener
            }

            currentUser?.let {
                val userCredential = EmailAuthProvider.getCredential(it.email!!, pass)
                it.reauthenticate(userCredential).addOnCompleteListener { task ->
                    when {
                        task.isSuccessful -> {
                            binding.cvCurrentPass.visibility = View.GONE
                            binding.cvUpdatePass.visibility = View.VISIBLE
                        }
                        task.exception is FirebaseAuthInvalidCredentialsException -> {
                            binding.edtCurrentPassword.error = "Password Salah"
                            binding.edtCurrentPassword.requestFocus()
                        }
                        else -> {
                            Toast.makeText(activity, "${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            binding.btnNewCancel.setOnClickListener {
                binding.cvCurrentPass.visibility = View.GONE
                binding.cvUpdatePass.visibility = View.GONE
            }

            binding.btnNewChange.setOnClickListener {

                val newPass = binding.edtNewPass.text.toString()
                val passConfirm = binding.edtConfirmPass.text.toString()

                if (newPass.isEmpty() || passConfirm.isEmpty()) {
                    Toast.makeText(activity, "Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (newPass.length < 6 || passConfirm.length < 6) {
                    Toast.makeText(activity, "Password harus lebih dari 6 karakter", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (newPass != passConfirm) {
                    Toast.makeText(activity, "Password tidak sama", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                currentUser?.let {
                    it.updatePassword(newPass).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(activity, "Password Berhasil diUpdate", Toast.LENGTH_SHORT).show()
                            auth.signOut() // Logout user
                            val intent = Intent(requireContext(), LoginActivity::class.java)
                            startActivity(intent) // Redirect ke halaman login
                            requireActivity().finish() // Menutup activity saat ini
                        } else {
                            Toast.makeText(activity, "${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }





    private fun logout() {
        auth.signOut()
        googleSignInClient.signOut().addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Berhasil Logout", Toast.LENGTH_SHORT).show()
                val intent = Intent(requireContext(), LoginActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            } else {
                Toast.makeText(requireContext(), "Gagal Logout", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
