package com.example.marvelchallenge.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.marvelchallenge.CalendarAdapter
import com.example.marvelchallenge.R
import com.facebook.FacebookSdk.getApplicationContext
import org.json.JSONObject

class CalendarFragment : Fragment() {

    private var getMarvelJson: JSONObject?  = null
    private var getResults                  = arrayListOf<JSONObject>()
    lateinit var recyclerView: RecyclerView
    val pref: SharedPreferences = getApplicationContext().getSharedPreferences("calendar_info", Context.MODE_PRIVATE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {

            val editor: SharedPreferences.Editor = pref.edit()
            editor.putString("json_calendar", requireArguments().getString(MARVEL_CALENDAR))
            editor.apply()

        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(
            R.layout.fragment_calendar,
            container,
            false)

        getMarvelJson       = JSONObject(pref.getString("json_calendar", ""))
        getResults          = arrayListOf()

        val getRequestData  = getMarvelJson!!["data"] as JSONObject
        val getArray        = getRequestData.getJSONArray("results")
        val makeArray       = Array<String>(getArray.length()) { i -> getArray.getString(i) }

        for (element in makeArray) {

            val createMap = JSONObject(element)
            getResults.add(createMap)

        }

        recyclerView = view.findViewById(R.id.recycler_view_calendar)

        recyclerView.layoutManager = LinearLayoutManager(this.context)
        val adapter = CalendarAdapter(getResults)
        recyclerView.adapter = adapter

        return view

    }

    companion object {

        private val MARVEL_CALENDAR = "marvelCalendar"

        fun newInstance(jsonString: String): CalendarFragment {

            val fragment = CalendarFragment()
            val args = Bundle()
            args.putString(MARVEL_CALENDAR, jsonString)

            fragment.arguments = args

            return fragment

        }

    }
}