package com.example.messenger.chat;

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView;
import com.example.messenger.R


class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>(){

    var data = listOf<Message>()
        @SuppressLint("NotifyDataSetChanged")
        set(value){
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder =
        ChatViewHolder.inflateFrom(parent)

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    class ChatViewHolder(val rootview: LinearLayout): RecyclerView.ViewHolder(rootview){
        val sender = rootview.findViewById<TextView>(R.id.senderTextView)
        val text = rootview.findViewById<TextView>(R.id.messageTextView)

        fun bind(item: Message){
            sender.text = item.senderName
            text.text = item.text
        }

        companion object{
            fun inflateFrom(parent: ViewGroup): ChatViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.message_item, parent, false) as LinearLayout
                return ChatViewHolder(view)
            }
        }
    }
}
