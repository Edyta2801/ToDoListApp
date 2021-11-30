package com.example.todolist

class Todo {

    companion object Factory{
        fun createList():Todo=Todo()
    }

    var UID:String?=null
    var title:String?=null
    var isChecked:Boolean=false
}
