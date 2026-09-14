package com.mypum.pos.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName="sync_queue")
data class SyncQueueEntity(@PrimaryKey(autoGenerate=true) val id:Long=0,val entityType:String,val entityId:Long,val operation:String,val payload:String,val attempts:Int=0,val createdAt:Long=System.currentTimeMillis())
