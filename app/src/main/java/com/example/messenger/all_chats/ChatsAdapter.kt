package com.example.messenger.all_chats;

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View;
import android.view.ViewGroup
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView;
import com.example.messenger.R
import com.example.messenger.all_chats.chats_database.Chat


class ChatsAdapter(val onClick: ((Chat) -> Unit)) : RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder>(){

    var data = listOf<Chat>()
        @SuppressLint("NotifyDataSetChanged")
        set(value){
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsViewHolder =
        ChatsViewHolder.inflateFrom(parent, onClick)

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ChatsViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }



    class ChatsViewHolder(val rootview: View, val onClick: ((Chat) -> Unit)): RecyclerView.ViewHolder(rootview){
        val chatName: TextView = rootview.findViewById<TextView>(R.id.chat_name)

        fun bind(item: Chat){
            chatName.text = item.chatName
            Log.d("Adapter", "Binding chat: ${item.chatName}")
            rootview.setOnClickListener{
                onClick(item)
            }
        }

        companion object{
            fun inflateFrom(parent: ViewGroup, onClick: ((Chat) -> Unit)): ChatsViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.chat_item, parent, false)
                return ChatsViewHolder(view, onClick)
            }
        }
    }
}
