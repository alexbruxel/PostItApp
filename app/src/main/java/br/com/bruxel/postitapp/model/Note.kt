package br.com.bruxel.postitapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val color: String,
    val category: String,
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val timestamp: Long
)