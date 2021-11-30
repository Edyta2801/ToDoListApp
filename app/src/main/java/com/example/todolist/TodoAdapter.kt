package com.example.todolist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.content.Context
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.ImageButton

class TodoAdapter(context: Context, toDoList:MutableList<Todo>):BaseAdapter(){

    private val inflater:LayoutInflater= LayoutInflater.from(context)
    private var itemList=toDoList
    private var updateAndDelete:UpdateAndDelete=context as UpdateAndDelete


    override fun getView(p0:Int,p1:View?,p2: ViewGroup?): View {
        val UID:String= itemList[p0].UID as String
        val title= itemList[p0].title as String
        val isChecked: Boolean= itemList[p0].isChecked

        val view:View
        val viewHolder:ListViewHolder

    if (p1==null){
    view=inflater.inflate(R.layout.item_todo,p2, false )
    viewHolder=ListViewHolder(view)
   view.tag=viewHolder

    }else{
        view=p1
        viewHolder=view.tag as ListViewHolder
    }
    viewHolder.textLabel.text=title
    viewHolder.isDone.isChecked=isChecked

    viewHolder.isDone.setOnClickListener{
    updateAndDelete.modifyItem(UID, !isChecked)

 }
        viewHolder.isDeleted.setOnClickListener{
        updateAndDelete.onItemDelete(UID)}

    return view
    }

   private class ListViewHolder(row:View?){
        val textLabel:TextView= row!!.findViewById(R.id.tvToDoTitle) as TextView
        val isDone:CheckBox= row!!.findViewById(R.id.cbDone) as CheckBox
        val isDeleted:ImageButton= row!!.findViewById(R.id.ibDelete) as ImageButton


    }

    override fun getItem(p0:Int): Any {
       return itemList[p0]
    }

    override fun getItemId(p0:Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return itemList.size
    }
}

