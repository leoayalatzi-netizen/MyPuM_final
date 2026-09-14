package com.mypum.pos
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import com.mypum.pos.designsystem.theme.MyPuMTheme
import com.mypum.pos.navigation.MyPuMNavHost
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); enableEdgeToEdge(); setContent { MyPuMTheme { Surface(Modifier.fillMaxSize()) { MyPuMNavHost() } } } }
}
