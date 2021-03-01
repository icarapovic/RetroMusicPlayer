package dev.icarapovic.music.domain.repository

import dev.icarapovic.music.domain.model.Album

interface AlbumRepository {
    fun getAllAlbums(): List<Album>
    fun getAlbumsByName(query: String): List<Album>
    fun getAlbumById(albumId: Long): Album
}