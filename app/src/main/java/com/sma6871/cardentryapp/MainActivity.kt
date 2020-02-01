package com.sma6871.cardentryapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnEnableDisable.setOnClickListener {
            card.isEnabled = !card.isEnabled
            card.hasLine = !card.hasLine
        }
    }
}
