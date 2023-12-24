package com.example.muzik.ui.main_activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.muzik.R
import com.example.muzik.api_controller.MuzikAPI
import com.example.muzik.api_controller.RetrofitHelper
import com.example.muzik.data_model.retrofit_model.response.SignUpResponse
import com.example.muzik.databinding.ActivityLoginBinding
import com.example.muzik.databinding.ActivitySignupBinding
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val et_fullname: EditText = binding.fullname
        val et_email: EditText = binding.email
        val et_phone: EditText = binding.phone
        val et_date_of_birth: EditText = binding.birthdate
        val et_password: EditText = binding.password
        val et_confirmPassword: EditText = binding.confirmPass
        val signUpBtn: Button = binding.signupbtn

        binding.backButton.setOnClickListener {
            finish()
        }

        signUpBtn.setOnClickListener {
            val fullName = et_fullname.text.toString()
            val email = et_email.text.toString()
            val phoneNumberText = et_phone.text.toString()
            val phoneNumber: Int = phoneNumberText.toInt()
            val dateOfBirthText = et_date_of_birth.text.toString()
            val dateOfBirth: Date? = SimpleDateFormat("dd/MM/yyyy").parse(dateOfBirthText)
            val password = et_password.text.toString()
            val confirmPassword = et_confirmPassword.text.toString()

            if (fullName.trim().isEmpty() || email.trim().isEmpty() || phoneNumberText.trim()
                    .isEmpty()
                || dateOfBirthText.trim().isEmpty() || password.trim()
                    .isEmpty() || confirmPassword.trim().isEmpty()
            ) {
                Toast.makeText(this@SignUpActivity, "Please fill all fields", Toast.LENGTH_SHORT)
                    .show()

            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(
                    this@SignUpActivity,
                    "Passwords are not matching",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (dateOfBirth != null) {
                    lifecycleScope.launch {
                        performSignUp(fullName, email, phoneNumber, dateOfBirth, password)
                    }
                }

            }
        }
    }

    private suspend fun performSignUp(
        fullName: String,
        email: String,
        phoneNumber: Int,
        dateOfBirth: Date,
        password: String
    ) {
        val signUpApiService = RetrofitHelper.getInstance().create(MuzikAPI::class.java)

        signUpApiService.signUp(fullName, email, phoneNumber, dateOfBirth, password)
            .enqueue(object : Callback<SignUpResponse> {
                override fun onResponse(
                    call: Call<SignUpResponse>,
                    response: Response<SignUpResponse>
                ) {
                    if (response.isSuccessful) {
                        val signUpResponse: SignUpResponse? = response.body()
                        if (signUpResponse != null) {
                            if (signUpResponse.success) {
                                Toast.makeText(
                                    this@SignUpActivity,
                                    "Register successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()

                                val handler = Handler()
                                handler.postDelayed({
                                    val intent =
                                        Intent(applicationContext, LoginActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                }, 5000)
                            }
                        }

                    } else {
                        Toast.makeText(this@SignUpActivity, "Error in sign up", Toast.LENGTH_LONG)
                            .show()
                    }
                }

                override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                    Toast.makeText(this@SignUpActivity, t.message, Toast.LENGTH_LONG).show()
                }
            })
    }
}