package com.example.myapplication.activities

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.example.myapplication.R
import com.example.myapplication.fragments.SecondFragment
import com.example.myapplication.fragments.ThirdFragment

class SecondActivity : BaseActivity()
{
    val vm: MyViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        findViewById<TextView>(R.id.tvText).text = intent.getStringExtra("name")
        findViewById<Button>(R.id.button).setOnClickListener {
            supportFragmentManager.setFragmentResult("data", bundleOf("name" to "ricolwang"))
        }

        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add<ThirdFragment>(R.id.fragmentContainerView2)
            add<SecondFragment>(R.id.fragmentContainerView1)
        }
    }
}