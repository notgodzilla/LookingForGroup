package com.notgodzilla.lookingforgroup

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.notgodzilla.lookingforgroup.preferences.PreferencesRepository

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PreferencesRepository.initialize(this)
        setContentView(R.layout.activity_main)
    }

    //TODO Prepare for tracking
    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }

}