package com.example.muzik.ui.main_activity

 import android.content.Intent
 import android.os.Bundle
 import android.util.Log
 import android.widget.Button
 import android.widget.EditText
 import android.widget.Toast
 import androidx.appcompat.app.AppCompatActivity
// import com.auth0.android.jwt.JWT
 import com.example.muzik.R
 import com.example.muzik.api_controller.MuzikAPI
 import com.example.muzik.api_controller.RetrofitHelper
 import com.example.muzik.data_model.retrofit_model.request.LoginRequest
 import com.example.muzik.data_model.retrofit_model.response.LoginResponse
 // import com.example.muzik.storage.SharedPrefManager
 import retrofit2.Call
 import retrofit2.Callback
 import retrofit2.Response


class LoginActivity: AppCompatActivity() {


    val tag: String = "Tagne"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val et_username: EditText = findViewById(R.id.username)
        val et_password: EditText = findViewById(R.id.password)
        val loginBtn: Button = findViewById(R.id.loginbtn)
        val signUpBtn: Button = findViewById(R.id.signupbtn1)

        Log.i(tag, "oncreate")
        loginBtn.setOnClickListener {
            val username = et_username.text.toString()
            val password = et_password.text.toString()

            if (username.trim().isEmpty() || password.trim().isEmpty()) {
                Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Log.i(tag, "onclickLoginBtn")
            val loginRequest = LoginRequest(username = username, password = password)
            performLogin(loginRequest)
//            Toast.makeText(this@LoginActivity, username, Toast.LENGTH_LONG).show()
        }

        signUpBtn.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

    }
    override fun onStart() {
        super.onStart()

        Log.i(tag, "onstart")
        if(SharedPrefManager.getInstance(this).isLoggedIn
                && SharedPrefManager.getInstance(this).isAccessTokenExpired){

            val intent = Intent(applicationContext, ProfileActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            Log.i(tag, "isLogin")
            startActivity(intent)
        }
    }

    private fun performLogin(loginRequest: LoginRequest) {
        RetrofitHelper.getInstance().create(MuzikAPI::class.java).userLogin(loginRequest = loginRequest)
            .enqueue(object: Callback<LoginResponse>{

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.i(tag, "performonFailue")
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    Log.i(tag, "onResponse")
                    println(response.body())
//                    val accessToken = response.body()?.accessToken
//                    Log.i(tag, "val accessToken")
//
//                    if (accessToken != null) {
//                        SharedPrefManager.getInstance(this@LoginActivity).saveAccessToken(accessToken)
//                    }
//                    val intent = Intent(applicationContext, MainActivity::class.java)
//                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//
//                    startActivity(intent)

                    if(!response.body()?.error!!){

                        Log.i(tag, "!response.body()?.error!!")
                        val accessToken = response.body()?.accessToken
                        if (accessToken != null) {
                            SharedPrefManager.getInstance(this@LoginActivity).saveAccessToken(accessToken)
                        }
//                        SharedPrefManager.getInstance(applicationContext).saveUser(response.body()?.user!!)

                        val intent = Intent(applicationContext, ProfileActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                        startActivity(intent)


                    }else{
                        val intent = Intent(applicationContext, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                        startActivity(intent)
                        Toast.makeText(applicationContext, response.body()?.message, Toast.LENGTH_LONG).show()

                    }
                }
            })
    }

}

