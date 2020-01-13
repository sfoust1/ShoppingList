package com.projects.shoppingList.model

//import io.objectbox.annotation.Id

class ToDo() {
    //@Id
    var id : Long = -1
    var name : String? = null
    var createdAt : String? = null
    var items : MutableList<ToDoItem> = ArrayList()
}