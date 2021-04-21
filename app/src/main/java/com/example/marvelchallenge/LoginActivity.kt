package com.example.marvelchallenge

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {

    val TAG = "TAG"
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val facebookLoginButton = findViewById<LoginButton>(R.id.facebook_login_button)
        val emailLoginButton    = findViewById<Button>(R.id.email_sign_in_button)
        val emailLoginText      = findViewById<EditText>(R.id.email_login)
        val passLoginText       = findViewById<EditText>(R.id.email_pass)
        val signUpButton        = findViewById<TextView>(R.id.sign_up)
        val accessToken         = AccessToken.getCurrentAccessToken()
        val isLoggedIn          = accessToken != null && !accessToken.isExpired
        val callbackManager     = CallbackManager.Factory.create()
        val EMAIL               = "email"

        auth = FirebaseAuth.getInstance()

        val loginOnCompleteListener = OnCompleteListener<AuthResult> { task ->

            if (task.isSuccessful) {

                val intent = Intent(this, MainActivity::class.java)

                startActivity(intent)
                finish()

            } else {

                Toast.makeText(this, "Error: Email and Password do not match.", Toast.LENGTH_SHORT).show()

            }

        }

        emailLoginButton.setOnClickListener {

            var emailValid  = false
            var passValid   = false

            if(emailLoginText.text.toString() != "") {

                emailValid  = true

            } else {

                Toast.makeText(this, "Please, enter a valid email.", Toast.LENGTH_SHORT).show()

            }

            if(passLoginText.text.toString() != "") {

                passValid   = true

            } else {

                Toast.makeText(this, "Please, enter a valid password.", Toast.LENGTH_SHORT).show()

            }

            if(emailValid && passValid) {

                auth.signInWithEmailAndPassword(emailLoginText.text.toString(), passLoginText.text.toString()).addOnCompleteListener(this, loginOnCompleteListener)

            }

        }

        if (isLoggedIn) {

            LoginManager.getInstance().logInWithReadPermissions(
                this, listOf(
                    "email",
                    "public_profile"
                )
            )

        }

        facebookLoginButton.setPermissions(EMAIL, "public_profile")

        facebookLoginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG, "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")
            }

            override fun onError(error: FacebookException) {
                Log.d(TAG, "facebook:onError", error)
            }
        })

        signUpButton.setOnClickListener {

            val intent = Intent(this, SignUpActivity::class.java)

            startActivity(intent)

        }

    }

    private fun intentLogin() {

        val intent = Intent(this, MainActivity::class.java)

        startActivity(intent)
        finish()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val callbackManager = CallbackManager.Factory.create()
        callbackManager.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            intentLogin()
        }

    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {

                    intentLogin()
                    Log.d(TAG, "signInWithCredential:success")

                } else {

                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",Toast.LENGTH_SHORT).show()

                }

            }

    }

}