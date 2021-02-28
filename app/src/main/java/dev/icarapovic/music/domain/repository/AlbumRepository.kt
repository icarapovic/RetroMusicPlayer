package dev.icarapovic.music.domain.repository

import dev.icarapovic.music.domain.model.Album

interface AlbumRepository {
    fun albums(): List<Album>
    fun albums(query: String): List<Album>
    fun album(albumId: Long): Album
}