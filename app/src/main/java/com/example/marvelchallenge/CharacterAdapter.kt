package com.example.marvelchallenge

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.json.JSONObject

class CharacterAdapter(val character: ArrayList<JSONObject>): RecyclerView.Adapter<CharacterAdapter.CharacterHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterHolder {

        val layoutInflater = LayoutInflater.from(parent.context)

        return CharacterHolder(layoutInflater.inflate(R.layout.item_character, parent, false))

    }

    override fun getItemCount(): Int {
        return character.size
    }

    override fun onBindViewHolder(holder: CharacterHolder, position: Int) {
        holder.render(character[position])

    }

    class CharacterHolder(val view: View):RecyclerView.ViewHolder(view) {

        fun render(character: JSONObject) {

            val characterImage  = view.findViewById<ImageView>(R.id.character_image)
            val getPath         = character["thumbnail"] as JSONObject

            view.findViewById<TextView>(R.id.character_name).text = character["name"] as String
            view.findViewById<TextView>(R.id.character_text).text = character["description"] as String
            Picasso.get().load(getPath["path"].toString()  + "." + getPath["extension"].toString()).into(characterImage)

            view.setOnClickListener {

                val intent = Intent(view.context, CharacterDescriptionActivity::class.java)
                intent.putExtra("characterInfo", character.toString())

                startActivity(view.context, intent, null)

            }
        }

    }

}