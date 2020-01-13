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


