package com.projects.shoppingList.adapters

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.projects.shoppingList.view.ItemActivity
import com.projects.shoppingList.model.ToDoItem
import com.projects.shoppingList.R
import kotlinx.android.synthetic.main.activity_item.*

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