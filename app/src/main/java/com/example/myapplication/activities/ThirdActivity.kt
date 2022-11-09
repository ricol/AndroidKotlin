package com.example.myapplication.activities

import android.os.Bundle
import android.os.PersistableBundle

class ThirdActivity: BaseActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        val name = intent.getStringExtra("name") ?: "not found"
        println("get string: $name")
    }
}