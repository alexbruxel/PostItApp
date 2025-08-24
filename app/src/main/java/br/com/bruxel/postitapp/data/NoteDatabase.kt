package br.com.bruxel.postitapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import br.com.bruxel.postitapp.model.Note

@Database(entities = [Note::class], version = 2, exportSchema = false)
@TypeConverters(br.com.bruxel.postitapp.data.RoomConverters::class)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var INSTANCE: NoteDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `Note_new` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `title` TEXT NOT NULL,
                        `content` TEXT NOT NULL,
                        `color` TEXT NOT NULL,
                        `categories` TEXT NOT NULL,
                        `isPinned` INTEGER NOT NULL,
                        `isArchived` INTEGER NOT NULL,
                        `isDeleted` INTEGER NOT NULL,
                        `timestamp` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    INSERT INTO `Note_new` (id, title, content, color, categories, isPinned, isArchived, isDeleted, timestamp)
                    SELECT id, title, content, color,
                           CASE WHEN category IS NULL OR category = ''
                                THEN '[]'
                                ELSE '[' || '"' || category || '"' || ']'
                           END,
                           isPinned, isArchived, isDeleted, timestamp
                    FROM `Note`
                    """.trimIndent()
                )
                db.execSQL("DROP TABLE `Note`")
                db.execSQL("ALTER TABLE `Note_new` RENAME TO `Note`")
            }
        }

        fun getDatabase(context: Context): NoteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    "note_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}