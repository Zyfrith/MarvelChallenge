package com.example.marvelchallenge

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.marvelchallenge.ui.fragments.CalendarFragment
import com.example.marvelchallenge.ui.fragments.CharactersFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity(), Communicator {

    private val charactersFragment  = CharactersFragment()
    private val calendarFragment    = CalendarFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestMarvelCharacters()
        requestMarvelEvents()

        val bottomNav = findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNav.itemIconTintList = null

        bottomNav.setOnNavigationItemSelectedListener {

            val superheroIcon   : MenuItem = bottomNav.menu.findItem(2131231071)
            val calendarIcon    : MenuItem = bottomNav.menu.findItem(2131231070)

            when(it.itemId) {

                R.id.navigation_characters -> {
                    replaceFragment(charactersFragment)
                    it.setIcon(R.drawable.ic_icon_superhero)
                    closefragment(calendarFragment)
                    calendarIcon.setIcon(R.drawable.ic_icon_calendar_disabled)
                }

                R.id.navigation_calendar -> {
                    replaceFragment(calendarFragment)
                    it.setIcon(R.drawable.ic_icon_calendar)
                    closefragment(charactersFragment)
                    superheroIcon.setIcon(R.drawable.ic_icon_superhero_disabled)
                }

            }

            true

        }

    }

    private fun requestMarvelCharacters() {

        val url     = "http://gateway.marvel.com/v1/public/characters?apikey=4de4b10f9bb3670bf9e118f890133488&hash=3a28dc75900a42d49990c1017358a57e&ts=1"
        val queue   = Volley.newRequestQueue(this)

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
                { response ->
                    if (response != null) {

                        passCharacterDataCommunicator(response.toString())

                        replaceFragment(charactersFragment)

                    } else {

                        Toast.makeText(
                                this,
                                "Something went wrong. Please try again later.",
                                Toast.LENGTH_SHORT
                        ).show()

                    }

                }, { error -> error.printStackTrace() }

        )

        queue.add(jsonObjectRequest)

    }

    private fun requestMarvelEvents() {

        val url     = "http://gateway.marvel.com/v1/public/events?apikey=4de4b10f9bb3670bf9e118f890133488&hash=3a28dc75900a42d49990c1017358a57e&ts=1"
        val queue   = Volley.newRequestQueue(this)

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
                { response ->
                    if (response != null) {

                        passCalendarDataCommunicator(response.toString())

                    } else {

                        Toast.makeText(
                                this,
                                "Something went wrong. Please try again later.",
                                Toast.LENGTH_SHORT
                        ).show()

                    }

                }, { error -> error.printStackTrace() }

        )

        queue.add(jsonObjectRequest)

    }

    private fun replaceFragment(fragment: Fragment) {

                if(fragment != null) {

                    val transaction = supportFragmentManager.beginTransaction()

                    transaction.replace(R.id.fragment_container, fragment)
                    transaction.commit()

                }

    }

    override fun passCharacterDataCommunicator(passData: String) {

        if (passData != null) {

            val charactersFragment = CharactersFragment.newInstance(passData)
            replaceFragment(charactersFragment)

        }

    }

    override fun passCalendarDataCommunicator(passData: String) {

        if (passData != null) {

            CalendarFragment.newInstance(passData)

        }

    }

    private fun closefragment(fragment: Fragment) {
        this.supportFragmentManager.beginTransaction().remove(fragment).commit()
    }

}