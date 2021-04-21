package com.example.marvelchallenge

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val signUpEmail             = findViewById<EditText>(R.id.sign_up_email)
        val signUpPassword          = findViewById<EditText>(R.id.sign_up_password)
        val signUpRepeatPassword    = findViewById<EditText>(R.id.sign_up_repeat_password)
        val signUpButton            = findViewById<TextView>(R.id.sign_up_button)

        mAuth       =   FirebaseAuth.getInstance()

        val filter  =   InputFilter { source, start, end, _, _, _ ->

            for (i in start until end) {
                if (Character.isWhitespace(source[i])) {
                    return@InputFilter ""
                }

            }

            null

        }

        signUpEmail.filters             = arrayOf(filter)
        signUpPassword.filters          = arrayOf(filter)
        signUpRepeatPassword.filters    = arrayOf(filter)

        signUpButton.setOnClickListener {

            val emailValid         =    validations(signUpEmail.text.toString(), 1)
            val passValid          =    validations(signUpPassword.text.toString(), 2)
            var repeatPassValid    =    false

            if(signUpRepeatPassword.text.toString() == signUpPassword.text.toString()) {

                repeatPassValid    =    validations(signUpRepeatPassword.text.toString(), 3)

            } else {

                Toast.makeText(this, "Tienes que repetir la contraseña.", Toast.LENGTH_SHORT).show()

            }

            if(emailValid && passValid && repeatPassValid) {

                mAuth!!.createUserWithEmailAndPassword(signUpEmail.text.toString(), signUpPassword.text.toString()).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {

                        Toast.makeText(this, "¡Bienvenido a Marvel Challenge!", Toast.LENGTH_SHORT).show()

                        val request     =   db.collection("users")
                        val setValues   =   HashMap<String, Any>()

                        if (!(signUpEmail.text.toString().isBlank())) {

                            setValues["username"]   =   signUpEmail.text.toString()

                        }

                        setValues["uid"]            =   FirebaseAuth.getInstance().uid.toString()
                        setValues["email"]          =   signUpEmail.text.toString()

                        request.add(setValues)
                            .addOnSuccessListener { documentReference ->
                                Log.d("Success", "DocumentSnapshot added with ID: ${documentReference.id}")
                            }
                            .addOnFailureListener { e ->
                                Log.w("Failure", "Error adding document", e)
                            }

                        val intent  =   Intent(this, MainActivity::class.java)

                        startActivity(intent)

                        finish()

                    } else {

                        Toast.makeText(this, "Hubo un error, por favor inténtalo nuevamente más tarde.", Toast.LENGTH_SHORT).show()

                        Log.w("What Happened:", "createUserWithEmail:failure", task.exception)


                    }

                }

            } else {

                Toast.makeText(this, "Error, usuario o contraseña inconrrectos.", Toast.LENGTH_SHORT).show()

            }

        }

    }

    private fun validations(validation: String, validationType: Int) : Boolean {

        var validityValue   =   false

        if(validation != "") {

            validityValue   =   true

        } else {

            when(validationType) {

                1 ->    Toast.makeText(this, "Tienes que ingresar un email válido.", Toast.LENGTH_SHORT).show()
                2 ->    Toast.makeText(this, "Tienes que ingresar una contraseña.", Toast.LENGTH_SHORT).show()
                3 ->    Toast.makeText(this, "Tienes que repetir la contraseña.", Toast.LENGTH_SHORT).show()

            }

        }

        return validityValue

    }

}