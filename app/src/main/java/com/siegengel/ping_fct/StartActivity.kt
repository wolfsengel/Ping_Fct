package com.siegengel.ping_fct

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*

class StartActivity : AppCompatActivity() {
    private lateinit var slideToUnlock:SeekBar
    private lateinit var slideText:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        slideToUnlock = findViewById(R.id.slider_start)
        slideText = findViewById(R.id.sliderText)

        slideToUnlock.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (slideToUnlock.progress > 95){
                    val intent = intent.setClass(this@StartActivity, LogInActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    slideText.alpha = 1 - slideToUnlock.progress.toFloat()/100
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }
}