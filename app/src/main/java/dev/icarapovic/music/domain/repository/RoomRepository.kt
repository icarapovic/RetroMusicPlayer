package dev.icarapovic.music.domain.repository

import androidx.lifecycle.LiveData
import dev.icarapovic.music.data.db.*
import dev.icarapovic.music.domain.model.Song

interface RoomRepository {
    fun historySongs(): List<HistoryEntity>
    fun favoritePlaylistLiveData(favorite: String): LiveData<List<SongEntity>>
    fun insertBlacklistPath(blackListStoreEntity: BlackListStoreEntity)
    fun observableHistorySongs(): LiveData<List<HistoryEntity>>
    fun getSongs(playListId: Long): LiveData<List<SongEntity>>
    suspend fun createPlaylist(playlistEntity: PlaylistEntity): Long
    suspend fun checkPlaylistExists(playlistName: String): List<PlaylistEntity>
    suspend fun playlists(): List<PlaylistEntity>
    suspend fun playlistWithSongs(): List<PlaylistWithSongs>
    suspend fun insertSongs(songs: List<SongEntity>)
    suspend fun deletePlaylistEntities(playlistEntities: List<PlaylistEntity>)
    suspend fun renamePlaylistEntity(playlistId: Long, name: String)
    suspend fun deleteSongsInPlaylist(songs: List<SongEntity>)
    suspend fun deletePlaylistSongs(playlists: List<PlaylistEntity>)
    suspend fun favoritePlaylist(favorite: String): PlaylistEntity
    suspend fun isFavoriteSong(songEntity: SongEntity): List<SongEntity>
    suspend fun removeSongFromPlaylist(songEntity: SongEntity)
    suspend fun addSongToHistory(currentSong: Song)
    suspend fun songPresentInHistory(song: Song): HistoryEntity?
    suspend fun updateHistorySong(song: Song)
    suspend fun favoritePlaylistSongs(favorite: String): List<SongEntity>
    suspend fun insertSongInPlayCount(playCountEntity: PlayCountEntity)
    suspend fun updateSongInPlayCount(playCountEntity: PlayCountEntity)
    suspend fun deleteSongInPlayCount(playCountEntity: PlayCountEntity)
    suspend fun checkSongExistInPlayCount(songId: Long): List<PlayCountEntity>
    suspend fun playCountSongs(): List<PlayCountEntity>
    suspend fun insertBlacklistPath(blackListStoreEntities: List<BlackListStoreEntity>)
    suspend fun deleteBlacklistPath(blackListStoreEntity: BlackListStoreEntity)
    suspend fun clearBlacklist()
    suspend fun insertBlacklistPathAsync(blackListStoreEntity: BlackListStoreEntity)
    suspend fun blackListPaths(): List<BlackListStoreEntity>
    suspend fun deleteSongs(songs: List<Song>)
}