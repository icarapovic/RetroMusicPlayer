package dev.icarapovic.music.domain.repository

import dev.icarapovic.music.domain.model.Genre
import dev.icarapovic.music.domain.model.Song

interface GenreRepository {
    fun genres(): List<Genre>
    fun songs(genreId: Long): List<Song>
}