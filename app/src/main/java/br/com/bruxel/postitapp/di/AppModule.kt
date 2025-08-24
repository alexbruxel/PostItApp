package br.com.bruxel.postitapp.di

import android.content.Context
import androidx.room.Room
import br.com.bruxel.postitapp.data.NoteDao
import br.com.bruxel.postitapp.data.NoteDatabase
import br.com.bruxel.postitapp.repository.NoteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): NoteDatabase =
        Room.databaseBuilder(context, NoteDatabase::class.java, "note_database").build()

    @Provides
    fun provideNoteDao(db: NoteDatabase): NoteDao = db.noteDao()

    @Provides
    @Singleton
    fun provideNoteRepository(dao: NoteDao): NoteRepository = NoteRepository(dao)
}

