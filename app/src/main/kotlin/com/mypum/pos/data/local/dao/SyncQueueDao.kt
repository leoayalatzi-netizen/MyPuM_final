package com.mypum.pos.data.local.dao
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.mypum.pos.data.local.entity.*
@Dao interface SyncQueueDao { @Insert suspend fun insert(e:SyncQueueEntity):Long; @Query("SELECT * FROM sync_queue ORDER BY id LIMIT 100") suspend fun pending():List<SyncQueueEntity>; @Delete suspend fun delete(e:SyncQueueEntity) }
