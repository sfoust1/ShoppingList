package com.projects.shoppingList

const val DB_NAME = "TodoList"
const val DB_VERSION = 1

const val TABLE_TODO = "ToDo"
const val COL_ID = "id"
const val COL_CREATED_AT = "createdAt"
const val COL_NAME = "name"

const val TABLE_TODO_ITEM = "ToDoItem"
const val COL_TODO_ID = "todoId"
const val COL_ITEM_AISLE = "aisle"
const val COL_ITEM_NAME = "itemName"

const val INTENT_TODO_ID = "ToDoID"
const val INTENT_TODO_NAME = "ToDoName"

const val HIDDEN_DONE_LIST = "Crossed Off List"
const val SHOWN_DONE_LIST = "Hide List"