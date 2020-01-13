package com.projects.shoppingList

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import com.projects.shoppingList.view.DashboardActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Animate List Logo
        ic_logo.startAnimation(AnimationUtils.loadAnimation(this,R.anim.splash_in))
        // Allow image to display for half a second without animation
        Handler().postDelayed({
            // Then start animation
            ic_logo.startAnimation(AnimationUtils.loadAnimation(this,R.anim.splash_out))
            Handler().postDelayed({
                ic_logo.visibility = View.GONE
                // Begin main activity
                startActivity(Intent(this, DashboardActivity::class.java))
                finish()
            }, 500)
        }, 1500)
    }
}
