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
import code.name.monkey.retromusic.R
import dev.icarapovic.music.domain.model.Genre
import dev.icarapovic.music.domain.repository.*
import java.util.*

class SearchRepositoryImpl(
    private val songRepository: SongRepository,
    private val albumRepository: AlbumRepository,
    private val artistRepository: ArtistRepository,
    private val roomRepository: RoomRepository,
    private val genreRepository: GenreRepository,
) : SearchRepository {
    override fun searchAll(context: Context, query: String?): MutableList<Any> {
        val results = mutableListOf<Any>()
        query?.let { searchString ->
            val songs = songRepository.songs(searchString)
            if (songs.isNotEmpty()) {
                results.add(context.resources.getString(R.string.songs))
                results.addAll(songs)
            }
            val artists = artistRepository.artists(searchString)
            if (artists.isNotEmpty()) {
                results.add(context.resources.getString(R.string.artists))
                results.addAll(artists)
            }

            val albums = albumRepository.getAlbumsByName(searchString)
            if (albums.isNotEmpty()) {
                results.add(context.resources.getString(R.string.albums))
                results.addAll(albums)
            }
            val genres: List<Genre> = genreRepository.genres().filter { genre ->
                genre.name.toLowerCase(Locale.getDefault())
                    .contains(searchString.toLowerCase(Locale.getDefault()))
            }
            if (genres.isNotEmpty()) {
                results.add(context.resources.getString(R.string.genres))
                results.addAll(genres)
            }
            /* val playlist = roomRepository.playlists().filter { playlist ->
                 playlist.playlistName.toLowerCase(Locale.getDefault())
                     .contains(searchString.toLowerCase(Locale.getDefault()))
             }
             if (playlist.isNotEmpty()) {
                 results.add(context.getString(R.string.playlists))
                 results.addAll(playlist)
             }*/
        }
        return results
    }
}
