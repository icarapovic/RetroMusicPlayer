package dev.icarapovic.music.domain.repository

import dev.icarapovic.music.domain.model.Album
import dev.icarapovic.music.domain.model.Artist
import dev.icarapovic.music.domain.model.Song

interface LastAddedRepository {
    fun recentSongs(): List<Song>
    fun recentAlbums(): List<Album>
    fun recentArtists(): List<Artist>
}