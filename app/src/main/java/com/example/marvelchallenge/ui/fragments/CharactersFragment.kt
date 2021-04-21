package com.example.marvelchallenge.ui.fragments

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.marvelchallenge.CharacterAdapter
import com.example.marvelchallenge.R
import com.facebook.FacebookSdk.getApplicationContext
import org.json.JSONObject


class CharactersFragment : Fragment() {

    private var getMarvelJson: JSONObject?  = null
    private var getResults                  = arrayListOf<JSONObject>()
    lateinit var recyclerView: RecyclerView
    val pref: SharedPreferences = getApplicationContext().getSharedPreferences("character_info", MODE_PRIVATE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {

            val editor: SharedPreferences.Editor = pref.edit()
            editor.putString("json_characters", requireArguments().getString(MARVEL_JSON))
            editor.apply()

        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(
                R.layout.fragment_characters,
                container,
                false)

        getMarvelJson       = JSONObject(pref.getString("json_characters", ""))
        getResults          = arrayListOf()

        val getRequestData  = getMarvelJson!!["data"] as JSONObject
        val getArray        = getRequestData.getJSONArray("results")
        val makeArray       = Array<String>(getArray.length()) { i -> getArray.getString(i) }

        for (element in makeArray) {

            val createMap = JSONObject(element)
            getResults.add(createMap)

        }

        recyclerView = view.findViewById(R.id.recycler_view_characters)

        recyclerView.layoutManager = LinearLayoutManager(this.context)
        val adapter = CharacterAdapter(getResults)
        recyclerView.adapter = adapter

        return view

    }

    companion object {

        private val MARVEL_JSON = "marvelJson"

        fun newInstance(jsonString: String): CharactersFragment {

            val fragment = CharactersFragment()
            val args = Bundle()
            args.putString(MARVEL_JSON, jsonString)

            fragment.arguments = args

            return fragment

        }

    }

}