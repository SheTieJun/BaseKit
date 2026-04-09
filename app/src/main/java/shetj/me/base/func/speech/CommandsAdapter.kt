package shetj.me.base.func.speech

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import shetj.me.base.R

class CommandsAdapter(private val commands: List<CommandItem>) :
    RecyclerView.Adapter<CommandsAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val commandTextView: TextView = view.findViewById(R.id.commandTextView)
        val timeTextView: TextView = view.findViewById(R.id.timeTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_command, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = commands[position]
        holder.commandTextView.text = item.command
        holder.timeTextView.text = item.time
    }

    override fun getItemCount() = commands.size
}
