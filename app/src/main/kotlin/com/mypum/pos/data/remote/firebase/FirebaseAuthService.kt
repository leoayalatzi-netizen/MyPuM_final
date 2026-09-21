package com.mypum.pos.data.remote.firebase
import com.google.firebase.auth.FirebaseAuth
class FirebaseAuthService(private val auth:FirebaseAuth){ fun isSignedIn()=auth.currentUser!=null }
