package com.projects.ShoppingList

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.projects.ShoppingList.Model.ToDoItem
import kotlinx.android.synthetic.main.activity_item.*
import java.util.*

class ItemActivity : AppCompatActivity() {

    lateinit var dbHandler: DBHandler
    private var todoId: Long = -1

    var list: MutableList<ToDoItem>? = null
    var doneList: MutableList<ToDoItem> = ArrayList()
    var adapter : ItemAdapter? = null
    var doneAdapter : DoneItemAdapter? = null
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
            if(boolean) {
                // Change the text to show the user where they are at
                button.text = "Hide List"
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
                button.text = "Crossed Off List"
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
            // Set which button is the affirmation button in the dialog_dashboard
            dialog.setPositiveButton("Add") { _: DialogInterface, _: Int ->
                // If text field is empty, do nothing
                if (toDoName.text.isNotEmpty()) {
                    // Create and add toDoItem
                    val item = ToDoItem()
                    item.itemName = toDoName.text.toString()
                    item.toDoId = todoId
                    item.isCompleted = false
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
        // Set the initial toDoItem text into the text field
        toDoName.setText(item.itemName)
        // Set which button is the affirmation button in the dialog_dashboard
        dialog.setPositiveButton("Update") { _: DialogInterface, _: Int ->
            if (toDoName.text.isNotEmpty()) {
                // Build the toDoItem
                item.itemName = toDoName.text.toString()
                item.toDoId = todoId
                item.isCompleted = false
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
    private fun refreshList() {
        // Keep the done item list off of the screen unless done list is meant to be displayed
        if(boolean) rv_done_item.animate().translationY((rv_done_item.height).toFloat())
        // Get current active list from database
        list = dbHandler.getToDoItems(todoId)

        // Set the adapters
        adapter = ItemAdapter(this, list!!)
        doneAdapter = DoneItemAdapter(this, doneList)
        rv_item.adapter = adapter
        rv_done_item.adapter = doneAdapter
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////         Adapters           //////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    // Handle the active view list
    class ItemAdapter(private val activity: ItemActivity, private val list: MutableList<ToDoItem>) :
        RecyclerView.Adapter<ItemAdapter.ViewHolder>() {
        // Update the view with the current items from the child_item recycler view
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                    return ViewHolder(LayoutInflater.from(activity).inflate(R.layout.rv_child_item, parent, false))
            }
        // Required Override
        override fun getItemCount(): Int { return list.size }
        // Called when the adapter sees an action
        @SuppressLint("ClickableViewAccessibility")
        override fun onBindViewHolder(holder: ViewHolder, index: Int) {
            // Set the displayed text onto the recycler view
            holder.itemName.text = list[index].itemName
            // Remove from active list and add to done list
            holder.itemName.setOnClickListener {
                activity.dbHandler.deleteToDoItem(list[index].id)
                activity.doneList.add(list[index])
                activity.button.visibility = View.VISIBLE
                if(activity.button.text == "Hide List") {

                    // Put the bottom of the done item list back at the bottom of the screen
                    activity.rv_done_item.animate().translationY((0).toFloat())
                    // Move the button to the same height as the done item list
                    activity.button.animate().translationY(-(activity.rv_done_item.height).toFloat())
                }
                activity.refreshList()
            }
            // Remove from active list and don't add to done list
            holder.delete.setOnClickListener {
                // Initialize generic dialog pop-up
                val dialog = AlertDialog.Builder(activity)
                dialog.setTitle("Are you sure")
                dialog.setMessage("Do you want to delete this item ?")
                // Set which button is the affirmation button in the dialog_dashboard
                dialog.setPositiveButton("Continue") { _: DialogInterface, _: Int ->
                    activity.dbHandler.deleteToDoItem(list[index].id)
                    activity.refreshList()
                }
                // Set which button is the cancel button in the dialog_dashboard
                dialog.setNegativeButton("Cancel") { _: DialogInterface, _: Int -> }
                // Show the pop-up
                dialog.show()
            }
            // Edit chosen activity item
            holder.edit.setOnClickListener {
                activity.updateItem(list[index])
            }
        }
        // Setting the template for the recyclerview
        inner class ViewHolder(v: View) :
            RecyclerView.ViewHolder(v) {
            val itemName: TextView = v.findViewById(R.id.cb_item)
            val edit: ImageView = v.findViewById(R.id.iv_edit)
            val delete: ImageView = v.findViewById(R.id.iv_delete)
        }
    }

    class DoneItemAdapter(private val activity: ItemActivity, private val list: MutableList<ToDoItem>) :
        RecyclerView.Adapter<DoneItemAdapter.DoneViewHolder>() {
        // Update the view with the current items from the child_item recycler view
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoneViewHolder {
            val view = LayoutInflater.from(activity).inflate(R.layout.rv_child_item, parent, false)
            return DoneViewHolder(view)
        }
        // Required Override
        override fun getItemCount(): Int { return list.size }
        // Called when the adapter sees an action
        @SuppressLint("ClickableViewAccessibility")
        override fun onBindViewHolder(holder: DoneViewHolder, index: Int) {
            // Set the displayed text onto the recycler view
            holder.itemName.text = list[index].itemName
            // Remove from active list and add to done list
            holder.itemName.setOnClickListener {
                // Change list item to not completed, then add to active list
                list[index].isCompleted = !list[index].isCompleted
                activity.dbHandler.addToDoItem(list[index])
                // Remove from done list
                list.removeAt(index)

                // Put the bottom of the done item list back at the bottom of the screen
                activity.rv_done_item.animate().translationY((0).toFloat())
                // Move the button to the same height as the done item list
                activity.button.animate().translationY(-(activity.rv_done_item.height).toFloat())
                if(list.size == 0) {
                    activity.button.visibility = View.GONE
                    // Change the text to show the user where they are at
                    activity.button.callOnClick()
                }


                activity.refreshList()
            }
            // Remove from list entirely
            holder.delete.setOnClickListener {
                val dialog = AlertDialog.Builder(activity)
                dialog.setTitle("Are you sure")
                dialog.setMessage("Do you want to delete this item ?")
                dialog.setPositiveButton("Continue") { _: DialogInterface, _: Int ->
                    list.removeAt(index)
                    activity.refreshList()
                }
                // Set which button is the cancel button in the dialog_dashboard
                dialog.setNegativeButton("Cancel") { _: DialogInterface, _: Int -> }
                // Show the pop up
                dialog.show()
            }
        }
        // Setting the template for the recyclerview
        inner class DoneViewHolder(v: View) :
            RecyclerView.ViewHolder(v) {
            val itemName: TextView = v.findViewById(R.id.cb_item)
            val delete: ImageView = v.findViewById(R.id.iv_delete)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            finish()
            true
        } else
            super.onOptionsItemSelected(item)
    }
}

