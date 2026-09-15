package com.mypum.pos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import com.mypum.pos.navigation.MyPuMNavHost
import com.mypum.pos.ui.theme.MyPuMTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyPuMTheme {
                val nav = rememberNavController()
                MyPuMNavHost(nav = nav)
            }
        }
    }
}
