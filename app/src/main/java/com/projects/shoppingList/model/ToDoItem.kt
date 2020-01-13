package com.projects.shoppingList.model

//import io.objectbox.annotation.Id

/**
 * Handles Store Items
 */
class ToDoItem {
    //@Id
    var id : Long = -1
    var toDoId : Long = -1
    var itemName : String? = null
    var aisle : Int = -1
}