package com.mypum.pos.data.local.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
            ALTER TABLE turnos
            ADD COLUMN efectivoContado TEXT
            """.trimIndent()
        )

        database.execSQL(
            """
            ALTER TABLE turnos
            ADD COLUMN diferencia TEXT
            """.trimIndent()
        )
    }
}
