package dev.icarapovic.music.domain.repository

import dev.icarapovic.music.domain.model.Album
import dev.icarapovic.music.domain.model.Artist
import dev.icarapovic.music.domain.model.Song

/**
 * Created by hemanths on 16/08/17.
 */

interface MostPlayedRepository {
    fun recentlyPlayedTracks(): List<Song>

    fun topTracks(): List<Song>

    fun notRecentlyPlayedTracks(): List<Song>

    fun topAlbums(): List<Album>

    fun topArtists(): List<Artist>
}