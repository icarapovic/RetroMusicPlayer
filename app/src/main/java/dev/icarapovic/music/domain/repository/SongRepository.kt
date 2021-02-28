package dev.icarapovic.music.domain.repository

import android.database.Cursor
import dev.icarapovic.music.domain.model.Song

interface SongRepository {
    fun songs(): List<Song>
    fun songs(cursor: Cursor?): List<Song>
    fun songs(query: String): List<Song>
    fun songsByFilePath(filePath: String): List<Song>
    fun song(cursor: Cursor?): Song
    fun song(songId: Long): Song
}