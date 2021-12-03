package com.example.todolist
import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.*

import com.google.android.material.floatingactionbutton.FloatingActionButton as FloatingActionButton
import kotlin.Boolean as Boolean


class MainActivity : AppCompatActivity(), UpdateAndDelete {

   lateinit var database:DatabaseReference
    var toDoList:MutableList<Todo>?=null
   lateinit var adapter:TodoAdapter
   private var listViewItem : ListView?=null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)


        val fab = findViewById<View>(R.id.fab) as FloatingActionButton

        listViewItem = findViewById(R.id.lvTodoItems) as ListView

        database = FirebaseDatabase.getInstance().reference

        fab.setOnClickListener { view ->
            val alertDialog = AlertDialog.Builder(this)
            val textEditText = EditText(this)
//            alertDialog.setMessage("Add TODO item")
            alertDialog.setTitle("Add Todo item")
            alertDialog.setView(textEditText)


            alertDialog.setPositiveButton("Add") { dialog, i ->
                if (textEditText.text.isNotEmpty()) {
                    val todoItemData = Todo.createList()
                    todoItemData.title = textEditText.text.toString()
                    todoItemData.isChecked = false

                    val newItemData = database.child("todo").push()
                    todoItemData.UID = newItemData.key
                    newItemData.setValue(todoItemData)

                    dialog.dismiss()
                    Toast.makeText(this, "Item saved", Toast.LENGTH_LONG).show()

                }
            }
            alertDialog.setNegativeButton("Cancel") { dialog, i ->
                dialog.dismiss()
                Toast.makeText(applicationContext, "No item added", Toast.LENGTH_LONG).show()


            }
            alertDialog.show()


        }

        toDoList = mutableListOf<Todo>()
        adapter = TodoAdapter(this, toDoList!!)
        listViewItem!!.adapter=adapter

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                toDoList!!.clear()
                addItemToList(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {

                Toast.makeText(applicationContext, "No item Added", Toast.LENGTH_LONG).show()

            }
        })
    }
        private fun addItemToList(snapshot: DataSnapshot) {
            val items=snapshot.children.iterator()

            if(items.hasNext()){

                val toDoIndexedValue=items.next()
                val itemsIterator=toDoIndexedValue.children.iterator()

                while(itemsIterator.hasNext()) {

                    val currentItem = itemsIterator.next()
                    val toDoItemData = Todo.createList()
                    val map = currentItem.getValue() as HashMap<*, *>


                    toDoItemData.UID=currentItem.key
                    toDoItemData.isChecked = map.get("isChecked")as Boolean? == true
                    toDoItemData.title= map.get("title") as String?
                    toDoList!!.add(toDoItemData)

                }
            }
            adapter.notifyDataSetChanged()

        }



//    override fun modifyItem(itemUID: String, isDone:Boolean ) {
//    val itemReference=database.child("todo").child(itemUID)
//        itemReference.child("isChecked").setValue(isDone)
//    }

    override fun modifyItem(itemUID: String, isDone:Boolean ) {
        val itemReference=database.child("todo").child(itemUID)


        val title2=  itemReference.child("isChecked").setValue(isDone)


        val title = findViewById<View>(R.id.tvToDoTitle) as TextView
        val check=findViewById<View>(R.id.cbDone) as CheckBox

       fun toggleStrikeThrough(title2: TextView, isDone: Boolean) {
       if(isDone) {
            title2.paintFlags = title2.paintFlags or STRIKE_THRU_TEXT_FLAG
       } else {
            title2.paintFlags = title2.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
        }
    }

        toggleStrikeThrough(title2 , isDone)



    }




    override fun onItemDelete(itemUID: String) {
          val alertDialog = AlertDialog.Builder(this)
            alertDialog.setMessage("Are you sure you want to Delete?")

            alertDialog.setPositiveButton("Yes") { dialog, i ->

                val itemReference = database.child("todo").child(itemUID)
                itemReference.removeValue()
                adapter.notifyDataSetChanged()

                dialog.dismiss()
                Toast.makeText(this, "Item deleted", Toast.LENGTH_LONG).show()


            }
            alertDialog.setNegativeButton("No") { dialog, i ->
                dialog.dismiss()


            }
            alertDialog.show()


        }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean{
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

}







