package com.mypum.pos.di
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.mypum.pos.data.remote.firebase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
@Module @InstallIn(SingletonComponent::class) object FirebaseModule {
 @Provides fun auth()=FirebaseAuth.getInstance()
 @Provides fun firestore()=FirebaseFirestore.getInstance()
 @Provides fun storage()=FirebaseStorage.getInstance()
 @Provides fun authService(a:FirebaseAuth)=FirebaseAuthService(a)
 @Provides fun firestoreService(f:FirebaseFirestore)=FirestoreSyncService(f)
 @Provides fun storageService(s:FirebaseStorage)=FirebaseStorageService(s)
}
