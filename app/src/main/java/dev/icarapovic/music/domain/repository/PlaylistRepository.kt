package dev.icarapovic.music.domain.repository

import android.database.Cursor
import dev.icarapovic.music.domain.model.Playlist
import dev.icarapovic.music.domain.model.Song

interface PlaylistRepository {
    fun playlist(cursor: Cursor?): Playlist
    fun searchPlaylist(query: String): List<Playlist>
    fun playlist(playlistName: String): Playlist
    fun playlists(): List<Playlist>
    fun playlists(cursor: Cursor?): List<Playlist>
    fun favoritePlaylist(playlistName: String): List<Playlist>
    fun deletePlaylist(playlistId: Long)
    fun playlist(playlistId: Long): Playlist
    fun playlistSongs(playlistId: Long): List<Song>
}