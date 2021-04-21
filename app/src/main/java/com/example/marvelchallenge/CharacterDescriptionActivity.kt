package com.example.marvelchallenge

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONObject
import java.util.regex.Matcher
import java.util.regex.Pattern


class CharacterDescriptionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_description)

        val comicsList          = findViewById<ListView>(R.id.comics_list)
        val descriptionImage    = findViewById<ImageView>(R.id.description_image)
        val descriptionText     = findViewById<TextView>(R.id.description_text)
        val titleBarCloseButton = findViewById<ImageButton>(R.id.titleBar_close)
        val titleBarText        = findViewById<TextView>(R.id.titleBar_text)

        val getCharacterInfo    = JSONObject(intent.getStringExtra("characterInfo"))
        val getPath             = getCharacterInfo["thumbnail"] as JSONObject
        val getComics           = getCharacterInfo["comics"] as JSONObject
        val getItems            = getComics["items"] as JSONArray
        val comicTitle          = arrayListOf<String>()

        titleBarText.text               = getCharacterInfo["name"] as String
        titleBarCloseButton.background  = null

        if (getCharacterInfo["description"] as String != "") {

            descriptionText.text    = getCharacterInfo["description"] as String

        }

        Picasso.get().load(
                getPath["path"].toString() + "." + getPath["extension"].toString()).into(
                descriptionImage
        )

        val makeArray = Array<String>(getItems.length()) { i -> getItems.getString(i) }

        for (element in makeArray) {

            val createMap = JSONObject(element)
            comicTitle.add(createMap["name"] as String)

        }

        val data: MutableList<Map<String, String?>> = ArrayList()

        for (i in 0 until comicTitle.size) {

            val itemString                          = comicTitle[i]
            val datum: MutableMap<String, String?>  = HashMap(2)
            val getYear: Matcher                    = Pattern.compile("\\(([^)]+)\\)").matcher(itemString)

            datum["title"] = itemString

            while (getYear.find()) {

                val checkedYear = java.lang.String(getYear.group(1)).toString()

                if (isNumber(checkedYear)) {

                    datum["subtitle"] = checkedYear

                } else {

                    datum["subtitle"] = "Year not found."

                }

                break

            }

            data.add(datum)

        }

        val adapter = SimpleAdapter(
                this, data,
                R.layout.character_list_item, arrayOf("title", "subtitle"),
                intArrayOf(R.id.character_text_1, R.id.character_text_2)
        )

        comicsList.adapter  = adapter
        setListViewHeightBasedOnChildren(comicsList)

        titleBarCloseButton.setOnClickListener {

            this.finish()

        }

    }

    fun setListViewHeightBasedOnChildren(listView: ListView) {

        val listAdapter = listView.adapter ?: return
        var totalHeight = 0

        for (i in 0 until listAdapter.count) {

            val listItem: View = listAdapter.getView(i, null, listView)

            listItem.measure(0, 0)
            totalHeight = (listItem.measuredHeight + 8) * listAdapter.count


        }

        val params = listView.layoutParams

        params.height           = totalHeight + listView.dividerHeight * listAdapter.count
        listView.layoutParams   = params
        listView.requestLayout()

    }

    private fun isNumber(checkInt: String): Boolean {

        return when(checkInt.toIntOrNull()) {

            null -> false
            else -> true

        }

    }

}