package com.example.messenger.settings.server

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.messenger.R
import com.example.messenger.settings.SettingsItem


class SettingsListAdapter(context: Context, resource: Int, private var list: List<SettingsItem>) :
    ArrayAdapter<SettingsItem>(context, resource, list) {
    private var layout: Int = resource
    private var inflater: LayoutInflater  = LayoutInflater.from(context)


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        lateinit var view: View
        lateinit var viewHolder: ViewHolder
        if (convertView==null){
            view = inflater.inflate(layout, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }
        val item = list[position]

        viewHolder.textView.text = item.name
        viewHolder.imageView.setImageResource(item.icon)

        view.setOnClickListener(item.action)

        return view
    }

    private class ViewHolder(view: View){
        var imageView = view.findViewById<ImageView>(R.id.settingsItemIcon)
        var textView = view.findViewById<TextView>(R.id.settingsItemName)
    }
}