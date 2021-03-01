package dev.icarapovic.music.domain.repository

import android.database.Cursor
import code.name.monkey.retromusic.util.PreferenceUtil
import dev.icarapovic.music.domain.model.Song

interface SongRepository {
    fun getAllSongs(): List<Song>
    fun getFilteredSongs(
        selection: String? = null,
        selectionArgs: Array<String>? = null,
        sortOrder: String = PreferenceUtil.songSortOrder
    ): List<Song>
    fun songs(query: String): List<Song>
    fun songsByFilePath(filePath: String): List<Song>
    fun song(cursor: Cursor?): Song
    fun song(songId: Long): Song
}