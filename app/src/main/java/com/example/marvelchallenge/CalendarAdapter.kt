package com.example.marvelchallenge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SimpleAdapter
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import android.widget.ListView

class CalendarAdapter(val calendar: ArrayList<JSONObject>): RecyclerView.Adapter<CalendarAdapter.CalendarHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarHolder {

        val layoutInflater = LayoutInflater.from(parent.context)

        return CalendarHolder(layoutInflater.inflate(R.layout.item_calendar, parent, false))

    }

    override fun getItemCount(): Int {
        return calendar.size
    }

    override fun onBindViewHolder(holder: CalendarHolder, position: Int) {
        holder.render(calendar[position])

        holder.dropdownMenu.setOnClickListener {

            if (holder.expandableLayout.visibility == View.GONE) {

                holder.expandableLayout.visibility = View.VISIBLE

            } else {

                holder.expandableLayout.visibility = View.GONE

            }

        }
    }

    class CalendarHolder(val view: View): RecyclerView.ViewHolder(view) {

        val expandableLayout    = view.findViewById<ConstraintLayout>(R.id.expandable_layout)
        val dropdownMenu        = view.findViewById<ImageButton>(R.id.dropdown_menu)

        fun render(calendar: JSONObject) {

            val eventImage      = view.findViewById<ImageView>(R.id.event_image)
            val discussList     = view.findViewById<ListView>(R.id.comics_discuss_list)
            val getPath         = calendar["thumbnail"] as JSONObject
            val getCreators     = calendar["creators"] as JSONObject
            val creatorsArray   = getCreators["items"] as JSONArray
            var firstDate       = "1900-01-01 00:00:00"
            var secondDate      = "1900-01-01 00:00:00"

            if (!calendar["start"].equals(null)) {

                firstDate       = calendar["start"] as String

            }

            if (!calendar["end"].equals(null)) {

                secondDate      = calendar["end"] as String

            }

            dropdownMenu.background = null
            view.findViewById<TextView>(R.id.event_name).text   = calendar["title"] as String
            view.findViewById<TextView>(R.id.first_date).text   = formatDates(firstDate)
            view.findViewById<TextView>(R.id.second_date).text  = formatDates(secondDate)

            val data: MutableList<Map<String, String?>> = ArrayList()

            for (i in 0 until creatorsArray.length()) {

                val datum: MutableMap<String, String?> = HashMap(2)

                val itemString = creatorsArray[i] as JSONObject

                datum["title"]      = itemString["name"] as String
                datum["subtitle"]   = itemString["role"] as String

                data.add(datum)

            }

            val adapter = SimpleAdapter(
                    view.context, data,
                    R.layout.character_list_item, arrayOf("title", "subtitle"), intArrayOf(
                    R.id.character_text_1,
                    R.id.character_text_2
            )
            )

            discussList.adapter  = adapter
            setListViewHeightBasedOnChildren(discussList)

            Picasso.get().load(getPath["path"].toString() + "." + getPath["extension"].toString()).into(eventImage)

        }

        private fun formatDates(date: String): String {

            val formatter = SimpleDateFormat("yyyy-MM-dd").parse(date)

            return SimpleDateFormat("dd, MMMM yyyy").format(formatter)

        }

        fun setListViewHeightBasedOnChildren(listView: ListView) {

            val listAdapter = listView.adapter ?: return
            var totalHeight = 0

            for (i in 0 until listAdapter.count) {

                val listItem: View = listAdapter.getView(i, null, listView)

                listItem.measure(0, 0)
                totalHeight += listItem.measuredHeight

            }

            val params = listView.layoutParams

            params.height           = totalHeight + listView.dividerHeight * (listAdapter.count - 1)
            listView.layoutParams   = params
            listView.requestLayout()

        }

    }

}