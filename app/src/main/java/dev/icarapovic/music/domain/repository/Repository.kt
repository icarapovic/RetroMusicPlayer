package dev.icarapovic.music.domain.repository

import androidx.lifecycle.LiveData
import code.name.monkey.retromusic.model.Contributor
import code.name.monkey.retromusic.model.Home
import dev.icarapovic.music.data.db.*
import dev.icarapovic.music.data.network.Result
import dev.icarapovic.music.data.network.model.LastFmAlbum
import dev.icarapovic.music.data.network.model.LastFmArtist
import dev.icarapovic.music.domain.model.*
import kotlinx.coroutines.flow.Flow

interface Repository {
    fun songsFlow(): Flow<Result<List<Song>>>
    fun albumsFlow(): Flow<Result<List<Album>>>
    fun artistsFlow(): Flow<Result<List<Artist>>>
    fun playlistsFlow(): Flow<Result<List<Playlist>>>
    fun genresFlow(): Flow<Result<List<Genre>>>
    fun historySong(): List<HistoryEntity>
    fun favorites(): LiveData<List<SongEntity>>
    fun observableHistorySongs(): LiveData<List<Song>>
    fun albumById(albumId: Long): Album
    fun playlistSongs(playListId: Long): LiveData<List<SongEntity>>
    suspend fun fetchAlbums(): List<Album>
    suspend fun albumByIdAsync(albumId: Long): Album
    suspend fun allSongs(): List<Song>
    suspend fun fetchArtists(): List<Artist>
    suspend fun albumArtists(): List<Artist>
    suspend fun fetchLegacyPlaylist(): List<Playlist>
    suspend fun fetchGenres(): List<Genre>
    suspend fun search(query: String?): MutableList<Any>
    suspend fun getPlaylistSongs(playlist: Playlist): List<Song>
    suspend fun getGenre(genreId: Long): List<Song>
    suspend fun artistInfo(name: String, lang: String?, cache: String?): Result<LastFmArtist>
    suspend fun albumInfo(artist: String, album: String): Result<LastFmAlbum>
    suspend fun artistById(artistId: Long): Artist
    suspend fun recentArtists(): List<Artist>
    suspend fun topArtists(): List<Artist>
    suspend fun topAlbums(): List<Album>
    suspend fun recentAlbums(): List<Album>
    suspend fun recentArtistsHome(): Home
    suspend fun topArtistsHome(): Home
    suspend fun topAlbumsHome(): Home
    suspend fun recentAlbumsHome(): Home
    suspend fun favoritePlaylistHome(): Home
    suspend fun suggestionsHome(): Home
    suspend fun genresHome(): Home
    suspend fun playlists(): Home
    suspend fun homeSections(): List<Home>
    suspend fun homeSectionsFlow(): Flow<Result<List<Home>>>
    suspend fun playlist(playlistId: Long): Playlist
    suspend fun fetchPlaylistWithSongs(): List<PlaylistWithSongs>
    suspend fun playlistSongs(playlistWithSongs: PlaylistWithSongs): List<Song>
    suspend fun insertSongs(songs: List<SongEntity>)
    suspend fun checkPlaylistExists(playlistName: String): List<PlaylistEntity>
    suspend fun createPlaylist(playlistEntity: PlaylistEntity): Long
    suspend fun fetchPlaylists(): List<PlaylistEntity>
    suspend fun deleteRoomPlaylist(playlists: List<PlaylistEntity>)
    suspend fun renameRoomPlaylist(playlistId: Long, name: String)
    suspend fun deleteSongsInPlaylist(songs: List<SongEntity>)
    suspend fun removeSongFromPlaylist(songEntity: SongEntity)
    suspend fun deletePlaylistSongs(playlists: List<PlaylistEntity>)
    suspend fun favoritePlaylist(): PlaylistEntity
    suspend fun isFavoriteSong(songEntity: SongEntity): List<SongEntity>
    suspend fun addSongToHistory(currentSong: Song)
    suspend fun songPresentInHistory(currentSong: Song): HistoryEntity?
    suspend fun updateHistorySong(currentSong: Song)
    suspend fun favoritePlaylistSongs(): List<SongEntity>
    suspend fun recentSongs(): List<Song>
    suspend fun topPlayedSongs(): List<Song>
    suspend fun insertSongInPlayCount(playCountEntity: PlayCountEntity)
    suspend fun updateSongInPlayCount(playCountEntity: PlayCountEntity)
    suspend fun deleteSongInPlayCount(playCountEntity: PlayCountEntity)
    suspend fun checkSongExistInPlayCount(songId: Long): List<PlayCountEntity>
    suspend fun playCountSongs(): List<PlayCountEntity>
    suspend fun blackListPaths(): List<BlackListStoreEntity>
    suspend fun deleteSongs(songs: List<Song>)
    suspend fun contributor(): List<Contributor>
    suspend fun searchArtists(query: String): List<Artist>
    suspend fun searchSongs(query: String): List<Song>
    suspend fun searchAlbums(query: String): List<Album>
}