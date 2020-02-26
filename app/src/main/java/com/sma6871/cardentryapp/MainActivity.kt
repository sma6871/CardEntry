package com.sma6871.cardentryapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnEnableDisable.setOnClickListener {
            card.isEnabled = !card.isEnabled
            card.hasLine = !card.hasLine
            card.setTextColor(ContextCompat.getColor(this,R.color.green))
        }
        card.onPinChange { isComplete, length ->
//            Toast.makeText(this,"is Completed=$isComplete, length=$length",Toast.LENGTH_SHORT).show()
        }
    }
}
