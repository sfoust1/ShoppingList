package com.projects.shoppingList

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.projects.shoppingList.Adapters.DoneItemAdapter
import com.projects.shoppingList.Adapters.ItemAdapter
import com.projects.shoppingList.Model.ToDoItem
import kotlinx.android.synthetic.main.activity_item.*
import java.util.*

class ItemActivity : AppCompatActivity() {

    lateinit var dbHandler: DBHandler
    private var todoId: Long = -1

    var list: MutableList<ToDoItem>? = null
    var doneList: MutableList<ToDoItem> = ArrayList()
    var adapter: ItemAdapter? = null
    var doneAdapter: DoneItemAdapter? = null
    var boolean = true

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set View
        setContentView(R.layout.activity_item)
        // Set Action Bar
        setSupportActionBar(item_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Set DB Depth Handler
        todoId = intent.getLongExtra(INTENT_TODO_ID, -1)
        dbHandler = DBHandler(this)

        // Set layouts manager to allow handling here
        rv_item.layoutManager = LinearLayoutManager(this)
        rv_done_item.layoutManager = LinearLayoutManager(this)

        // Handle Deleted List Slide View
        button.setOnClickListener {
            // Slide the deleted list into view
            if (boolean) {
                // Change the text to show the user where they are at
                button.text = SHOWN_DONE_LIST
                // Move the button to the same height as the done item list
                button.animate().translationY(-(rv_done_item.height).toFloat())
                // Put the bottom of the done item list back at the bottom of the screen
                rv_done_item.animate().translationY((0).toFloat())
                // Remove Fab
                fab_item.visibility = View.GONE
                // Direct Next Interaction with button click listener
                boolean = false
            }
            // Remove the list from view
            else {
                // Change the text to show the user where they are at
                button.text = HIDDEN_DONE_LIST
                // Set button back to bottom of view
                button.animate().translationY((0).toFloat())
                // Place top of done item list onto the bottom of the view
                rv_done_item.animate().translationY((rv_done_item.height).toFloat())
                // Bring back fab
                fab_item.visibility = View.VISIBLE
                // Direct Next Interaction with button click listener
                boolean = true
            }
        }
        // Button made for adding items
        fab_item.setOnClickListener {
            // Initialize pop up dialog
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Add ToDo Item")
            // Set dialog view
            val view = layoutInflater.inflate(R.layout.dialog_dashboard, null)
            dialog.setView(view)
            // Get the text in the text field
            val toDoName = view.findViewById<EditText>(R.id.ev_todo)
            val aisleSpinner: Spinner = view.findViewById(R.id.spinner)
            // Set which button is the affirmation button in the dialog_dashboard
            dialog.setPositiveButton("Add") { _: DialogInterface, _: Int ->
                // If text field is empty, do nothing
                if (toDoName.text.isNotEmpty()) {
                    // Create and add toDoItem
                    val item = ToDoItem()
                    item.itemName = toDoName.text.toString()
                    item.toDoId = todoId
                    item.aisle = aisleSpinner.selectedItemId.toInt()
                    dbHandler.addToDoItem(item)
                    refreshList()
                }
            }
            // Set which button is the cancel button in the dialog_dashboard
            dialog.setNegativeButton("Cancel") { _: DialogInterface, _: Int -> }
            // Show the pop-up
            dialog.show()
        }
    }

    // Update toDoItem in view
    @SuppressLint("InflateParams")
    fun updateItem(item: ToDoItem) {
        // Initialize pop up dialog
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Update ToDo Item")
        // Set dialog view
        val view = layoutInflater.inflate(R.layout.dialog_dashboard, null)
        dialog.setView(view)
        // Get the text from the dialog view text field
        val toDoName = view.findViewById<EditText>(R.id.ev_todo)
        val aisleSpinner: Spinner = view.findViewById(R.id.spinner)
        // Set the initial toDoItem text into the text field
        toDoName.setText(item.itemName)
        // Set which button is the affirmation button in the dialog_dashboard
        dialog.setPositiveButton("Update") { _: DialogInterface, _: Int ->
            if (toDoName.text.isNotEmpty()) {
                // Build the toDoItem
                item.itemName = toDoName.text.toString()
                item.toDoId = todoId
                item.aisle = aisleSpinner.selectedItemId.toInt()
                dbHandler.updateToDoItem(item)
                refreshList()
            }
        }
        // Set which button is the cancel button in the dialog_dashboard
        dialog.setNegativeButton("Cancel") { _: DialogInterface, _: Int -> }
        // Show the pop-up
        dialog.show()
    }

    // Start the View
    override fun onResume() {
        refreshList()
        super.onResume()
    }

    /**
     * Keep the done list down while needed.
     * Update the active list.
     * Set the adapters.
     */
    fun refreshList() {
        // Keep the done item list off of the screen unless done list is meant to be displayed
        if (boolean) rv_done_item.animate().translationY((rv_done_item.height).toFloat())
        // Get current active list from database
        list = dbHandler.getToDoItems(todoId)

        // Set the adapters
        adapter = ItemAdapter(this, list!!)
        doneAdapter = DoneItemAdapter(this, doneList)
        rv_item.adapter = adapter
        rv_done_item.adapter = doneAdapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            finish()
            true
        } else
            super.onOptionsItemSelected(item)
    }
}





