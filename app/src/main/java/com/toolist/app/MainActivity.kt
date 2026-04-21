package com.toolist.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.google.firebase.auth.FirebaseAuth
import com.toolist.app.ui.navigation.AppNavGraph
import com.toolist.app.ui.theme.ToolistTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToolistTheme {
                AppNavGraph(isSessionActive = firebaseAuth.currentUser != null)
            }
        }
    }
}
