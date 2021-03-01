/*
 * Copyright (c) 2019 Hemanth Savarala.
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by
 *  the Free Software Foundation either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package dev.icarapovic.music.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import code.name.monkey.retromusic.*
import dev.icarapovic.music.data.db.*
import code.name.monkey.retromusic.model.*
import code.name.monkey.retromusic.model.smartplaylist.NotPlayedPlaylist
import dev.icarapovic.music.data.network.LastFMService
import dev.icarapovic.music.data.network.Result
import dev.icarapovic.music.data.network.Result.*
import dev.icarapovic.music.data.network.model.LastFmAlbum
import dev.icarapovic.music.data.network.model.LastFmArtist
import dev.icarapovic.music.domain.model.*
import dev.icarapovic.music.domain.repository.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

class RepositoryImpl(
    private val context: Context,
    private val lastFMService: LastFMService,
    private val songRepository: SongRepository,
    private val albumRepository: AlbumRepository,
    private val artistRepository: ArtistRepository,
    private val genreRepository: GenreRepository,
    private val lastAddedRepository: LastAddedRepository,
    private val playlistRepository: PlaylistRepository,
    private val searchRepository: SearchRepositoryImpl,
    private val mostPlayedRepository: MostPlayedRepository,
    private val roomRepository: RoomRepository,
    private val localDataRepository: LocalDataRepository
) : Repository {


    override suspend fun deleteSongs(songs: List<Song>) = roomRepository.deleteSongs(songs)

    override suspend fun contributor(): List<Contributor> = localDataRepository.contributors()

    override suspend fun searchSongs(query: String): List<Song> = songRepository.songs(query)

    override suspend fun searchAlbums(query: String): List<Album> = albumRepository.getAlbumsByName(query)

    override suspend fun searchArtists(query: String): List<Artist> =
        artistRepository.artists(query)

    override suspend fun fetchAlbums(): List<Album> = albumRepository.getAllAlbums()

    override suspend fun albumByIdAsync(albumId: Long): Album = albumRepository.getAlbumById(albumId)

    override fun albumById(albumId: Long): Album = albumRepository.getAlbumById(albumId)

    override suspend fun fetchArtists(): List<Artist> = artistRepository.artists()

    override suspend fun albumArtists(): List<Artist> = artistRepository.albumArtists()

    override suspend fun artistById(artistId: Long): Artist = artistRepository.artist(artistId)

    override suspend fun recentArtists(): List<Artist> = lastAddedRepository.recentArtists()

    override suspend fun recentAlbums(): List<Album> = lastAddedRepository.recentAlbums()

    override suspend fun topArtists(): List<Artist> = mostPlayedRepository.topArtists()

    override suspend fun topAlbums(): List<Album> = mostPlayedRepository.topAlbums()

    override suspend fun fetchLegacyPlaylist(): List<Playlist> = playlistRepository.playlists()

    override suspend fun fetchGenres(): List<Genre> = genreRepository.genres()

    override suspend fun allSongs(): List<Song> = songRepository.getAllSongs()

    override suspend fun search(query: String?): MutableList<Any> =
        searchRepository.searchAll(context, query)

    override suspend fun getPlaylistSongs(playlist: Playlist): List<Song> =
        if (playlist is AbsCustomPlaylist) {
            playlist.songs()
        } else {
            PlaylistSongsLoader.getPlaylistSongList(context, playlist.id)
        }

    override suspend fun getGenre(genreId: Long): List<Song> = genreRepository.songs(genreId)

    override suspend fun artistInfo(
        name: String,
        lang: String?,
        cache: String?
    ): Result<LastFmArtist> {
        return try {
            Success(lastFMService.artistInfo(name, lang, cache))
        } catch (e: Exception) {
            println(e)
            Error(e)
        }
    }

    override suspend fun albumInfo(
        artist: String,
        album: String
    ): Result<LastFmAlbum> {
        return try {
            val lastFmAlbum = lastFMService.albumInfo(artist, album)
            Success(lastFmAlbum)
        } catch (e: Exception) {
            println(e)
            Error(e)
        }
    }

    @ExperimentalCoroutinesApi
    override suspend fun homeSectionsFlow(): Flow<Result<List<Home>>> {
        val homes = MutableStateFlow<Result<List<Home>>>(value = Loading)
        val homeSections = mutableListOf<Home>()
        val sections = listOf(
            topArtistsHome(),
            topAlbumsHome(),
            recentArtistsHome(),
            recentAlbumsHome(),
            suggestionsHome(),
            favoritePlaylistHome(),
            genresHome()
        )
        for (section in sections) {
            if (section.arrayList.isNotEmpty()) {
                println("${section.homeSection} -> ${section.arrayList.size}")
                homeSections.add(section)
            }
        }
        if (homeSections.isEmpty()) {
            homes.value = Error(Exception(Throwable("No items")))
        } else {
            homes.value = Success(homeSections)
        }
        return homes
    }

    override suspend fun homeSections(): List<Home> {
        val homeSections = mutableListOf<Home>()
        val sections: List<Home> = listOf(
            suggestionsHome(),
            topArtistsHome(),
            topAlbumsHome(),
            recentArtistsHome(),
            recentAlbumsHome(),
            favoritePlaylistHome()
            // genresHome()
        )
        for (section in sections) {
            if (section.arrayList.isNotEmpty()) {
                homeSections.add(section)
            }
        }
        return homeSections
    }


    override suspend fun playlist(playlistId: Long) =
        playlistRepository.playlist(playlistId)

    override suspend fun fetchPlaylistWithSongs(): List<PlaylistWithSongs> =
        roomRepository.playlistWithSongs()

    override suspend fun playlistSongs(playlistWithSongs: PlaylistWithSongs): List<Song> =
        playlistWithSongs.songs.map {
            it.toSong()
        }

    override fun playlistSongs(playListId: Long): LiveData<List<SongEntity>> =
        roomRepository.getSongs(playListId)

    override suspend fun insertSongs(songs: List<SongEntity>) =
        roomRepository.insertSongs(songs)

    override suspend fun checkPlaylistExists(playlistName: String): List<PlaylistEntity> =
        roomRepository.checkPlaylistExists(playlistName)

    override suspend fun createPlaylist(playlistEntity: PlaylistEntity): Long =
        roomRepository.createPlaylist(playlistEntity)

    override suspend fun fetchPlaylists(): List<PlaylistEntity> = roomRepository.playlists()

    override suspend fun deleteRoomPlaylist(playlists: List<PlaylistEntity>) =
        roomRepository.deletePlaylistEntities(playlists)

    override suspend fun renameRoomPlaylist(playlistId: Long, name: String) =
        roomRepository.renamePlaylistEntity(playlistId, name)

    override suspend fun deleteSongsInPlaylist(songs: List<SongEntity>) =
        roomRepository.deleteSongsInPlaylist(songs)

    override suspend fun removeSongFromPlaylist(songEntity: SongEntity) =
        roomRepository.removeSongFromPlaylist(songEntity)

    override suspend fun deletePlaylistSongs(playlists: List<PlaylistEntity>) =
        roomRepository.deletePlaylistSongs(playlists)

    override suspend fun favoritePlaylist(): PlaylistEntity =
        roomRepository.favoritePlaylist(context.getString(R.string.favorites))

    override suspend fun isFavoriteSong(songEntity: SongEntity): List<SongEntity> =
        roomRepository.isFavoriteSong(songEntity)

    override suspend fun addSongToHistory(currentSong: Song) =
        roomRepository.addSongToHistory(currentSong)

    override suspend fun songPresentInHistory(currentSong: Song): HistoryEntity? =
        roomRepository.songPresentInHistory(currentSong)

    override suspend fun updateHistorySong(currentSong: Song) =
        roomRepository.updateHistorySong(currentSong)

    override suspend fun favoritePlaylistSongs(): List<SongEntity> =
        roomRepository.favoritePlaylistSongs(context.getString(R.string.favorites))

    override suspend fun recentSongs(): List<Song> = lastAddedRepository.recentSongs()

    override suspend fun topPlayedSongs(): List<Song> = mostPlayedRepository.topTracks()

    override suspend fun insertSongInPlayCount(playCountEntity: PlayCountEntity) =
        roomRepository.insertSongInPlayCount(playCountEntity)

    override suspend fun updateSongInPlayCount(playCountEntity: PlayCountEntity) =
        roomRepository.updateSongInPlayCount(playCountEntity)

    override suspend fun deleteSongInPlayCount(playCountEntity: PlayCountEntity) =
        roomRepository.deleteSongInPlayCount(playCountEntity)

    override suspend fun checkSongExistInPlayCount(songId: Long): List<PlayCountEntity> =
        roomRepository.checkSongExistInPlayCount(songId)

    override suspend fun playCountSongs(): List<PlayCountEntity> =
        roomRepository.playCountSongs()

    override suspend fun blackListPaths(): List<BlackListStoreEntity> =
        roomRepository.blackListPaths()

    override fun observableHistorySongs(): LiveData<List<Song>> =
        Transformations.map(roomRepository.observableHistorySongs()) {
            it.fromHistoryToSongs()
        }

    override fun historySong(): List<HistoryEntity> =
        roomRepository.historySongs()

    override fun favorites(): LiveData<List<SongEntity>> =
        roomRepository.favoritePlaylistLiveData(context.getString(R.string.favorites))

    override suspend fun suggestionsHome(): Home {
        val songs =
            NotPlayedPlaylist().songs().shuffled().takeIf {
                it.size > 9
            } ?: emptyList()
        return Home(songs, SUGGESTIONS, R.string.suggestion_songs)
    }

    override suspend fun genresHome(): Home {
        val genres = genreRepository.genres().shuffled()
        return Home(genres, GENRES, R.string.genres)
    }

    override suspend fun playlists(): Home {
        val playlist = playlistRepository.playlists()
        return Home(playlist, PLAYLISTS, R.string.playlists)
    }

    override suspend fun recentArtistsHome(): Home {
        val artists = lastAddedRepository.recentArtists().take(5)
        return Home(artists, RECENT_ARTISTS, R.string.recent_artists)
    }

    override suspend fun recentAlbumsHome(): Home {
        val albums = lastAddedRepository.recentAlbums().take(5)
        return Home(albums, RECENT_ALBUMS, R.string.recent_albums)
    }

    override suspend fun topAlbumsHome(): Home {
        val albums = mostPlayedRepository.topAlbums().take(5)
        return Home(albums, TOP_ALBUMS, R.string.top_albums)
    }

    override suspend fun topArtistsHome(): Home {
        val artists = mostPlayedRepository.topArtists().take(5)
        return Home(artists, TOP_ARTISTS, R.string.top_artists)
    }

    override suspend fun favoritePlaylistHome(): Home {
        val songs = favoritePlaylistSongs().map {
            it.toSong()
        }
        return Home(songs, FAVOURITES, R.string.favorites)
    }

    override fun songsFlow(): Flow<Result<List<Song>>> = flow {
        emit(Loading)
        val data = songRepository.getAllSongs()
        if (data.isEmpty()) {
            emit(Error(Exception(Throwable("No items"))))
        } else {
            emit(Success(data))
        }
    }

    override fun albumsFlow(): Flow<Result<List<Album>>> = flow {
        emit(Loading)
        val data = albumRepository.getAllAlbums()
        if (data.isEmpty()) {
            emit(Error(Exception(Throwable("No items"))))
        } else {
            emit(Success(data))
        }
    }

    override fun artistsFlow(): Flow<Result<List<Artist>>> = flow {
        emit(Loading)
        val data = artistRepository.artists()
        if (data.isEmpty()) {
            emit(Error(Exception(Throwable("No items"))))
        } else {
            emit(Success(data))
        }
    }

    override fun playlistsFlow(): Flow<Result<List<Playlist>>> = flow {
        emit(Loading)
        val data = playlistRepository.playlists()
        if (data.isEmpty()) {
            emit(Error(Exception(Throwable("No items"))))
        } else {
            emit(Success(data))
        }
    }

    override fun genresFlow(): Flow<Result<List<Genre>>> = flow {
        emit(Loading)
        val data = genreRepository.genres()
        if (data.isEmpty()) {
            emit(Error(Exception(Throwable("No items"))))
        } else {
            emit(Success(data))
        }
    }
}