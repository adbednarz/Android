package com.example.l4z1

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.preference.EditTextPreference
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import androidx.appcompat.app.AppCompatActivity

class ShowPhotoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
            setContentView(R.layout.activity_show_photo)
        else {
            finish()
        }
    }
}