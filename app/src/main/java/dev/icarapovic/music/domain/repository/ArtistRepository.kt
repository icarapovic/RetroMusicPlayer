package dev.icarapovic.music.domain.repository

import dev.icarapovic.music.domain.model.Artist

interface ArtistRepository {
    fun artists(): List<Artist>
    fun albumArtists(): List<Artist>
    fun artists(query: String): List<Artist>
    fun artist(artistId: Long): Artist
}