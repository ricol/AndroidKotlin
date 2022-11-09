package com.example.myapplication.activities

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?)
    {
        super.onCreate(savedInstanceState, persistentState)
        println("[$this]...onCreate...")
    }

    override fun onStart()
    {
        super.onStart()
        println("[$this]...onStart...")
    }

    override fun onRestart()
    {
        super.onRestart()
        println("[$this]...onRestart...")
    }

    override fun onResume()
    {
        super.onResume()
        println("[$this]...onResume...")
    }

    override fun onPause()
    {
        super.onPause()
        println("[$this]...onPause...")
    }

    override fun onStop()
    {
        super.onStop()
        println("[$this]...onStop...")
    }

    override fun onDestroy()
    {
        super.onDestroy()
        println("[$this]...onDestroy...")
    }
}