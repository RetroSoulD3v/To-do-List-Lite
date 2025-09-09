package com.retrosouldev.todolite

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.retrosouldev.todolite.databinding.ItemTodoBinding

class TodoAdapter(
    private val items: MutableList<Todo>,
    private val onChanged: (Todo) -> Unit,
    private val onLongPressDelete: (Todo, Int) -> Unit
) : RecyclerView.Adapter<TodoAdapter.VH>() {

    inner class VH(val b: ItemTodoBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: Todo) {
            b.txtTitle.text = item.title
            b.chkDone.isChecked = item.done
            b.txtTitle.paintFlags =
                if (item.done) b.txtTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                else b.txtTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()

            b.chkDone.setOnCheckedChangeListener(null)
            b.chkDone.setOnCheckedChangeListener { _, checked ->
                item.done = checked
                notifyItemChanged(bindingAdapterPosition)
                onChanged(item)
            }

            b.root.setOnLongClickListener {
                onLongPressDelete(item, bindingAdapterPosition)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])
    override fun getItemCount() = items.size

    fun removeAt(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    fun insertTop(item: Todo) {
        items.add(0, item)
        notifyItemInserted(0)
    }
}
