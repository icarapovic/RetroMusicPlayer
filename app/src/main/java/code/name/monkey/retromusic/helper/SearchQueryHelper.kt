/*
 * Copyright (c) 2020 Hemanth Savarla.
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 */
package code.name.monkey.retromusic.helper

import android.app.SearchManager
import android.os.Bundle
import android.provider.MediaStore
import dev.icarapovic.music.domain.model.Song
import dev.icarapovic.music.domain.repository.SongRepository
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

@KoinApiExtension
object SearchQueryHelper : KoinComponent {
    private const val TITLE_SELECTION = "lower(" + MediaStore.Audio.AudioColumns.TITLE + ") = ?"
    private const val ALBUM_SELECTION = "lower(" + MediaStore.Audio.AudioColumns.ALBUM + ") = ?"
    private const val ARTIST_SELECTION = "lower(" + MediaStore.Audio.AudioColumns.ARTIST + ") = ?"
    private const val AND = " AND "
    private val songRepository by inject<SongRepository>()
    var songs = ArrayList<Song>()

    @JvmStatic
    fun getSongs(extras: Bundle): List<Song> {
        val query = extras.getString(SearchManager.QUERY, null)
        val artistName = extras.getString(MediaStore.EXTRA_MEDIA_ARTIST, null)
        val albumName = extras.getString(MediaStore.EXTRA_MEDIA_ALBUM, null)
        val titleName = extras.getString(MediaStore.EXTRA_MEDIA_TITLE, null)

        var songs = listOf<Song>()
        if (artistName != null && albumName != null && titleName != null) {
            songs = songRepository.getFilteredSongs(
                ARTIST_SELECTION + AND + ALBUM_SELECTION + AND + TITLE_SELECTION,
                arrayOf(
                    artistName.toLowerCase(Locale.getDefault()),
                    albumName.toLowerCase(Locale.getDefault()),
                    titleName.toLowerCase(Locale.getDefault())
                )
            )
        }
        if (songs.isNotEmpty()) {
            return songs
        }
        if (artistName != null && titleName != null) {
            songs = songRepository.getFilteredSongs(
                ARTIST_SELECTION + AND + TITLE_SELECTION,
                arrayOf(
                    artistName.toLowerCase(Locale.getDefault()),
                    titleName.toLowerCase(Locale.getDefault())
                )
            )
        }
        if (songs.isNotEmpty()) {
            return songs
        }
        if (albumName != null && titleName != null) {
            songs = songRepository.getFilteredSongs(
                ALBUM_SELECTION + AND + TITLE_SELECTION,
                arrayOf(
                    albumName.toLowerCase(Locale.getDefault()),
                    titleName.toLowerCase(Locale.getDefault())
                )
            )
        }
        if (songs.isNotEmpty()) {
            return songs
        }
        if (artistName != null) {
            songs = songRepository.getFilteredSongs(
                ARTIST_SELECTION,
                arrayOf(artistName.toLowerCase(Locale.getDefault()))
            )
        }
        if (songs.isNotEmpty()) {
            return songs
        }
        if (albumName != null) {
            songs = songRepository.getFilteredSongs(
                ALBUM_SELECTION,
                arrayOf(albumName.toLowerCase(Locale.getDefault()))
            )
        }
        if (songs.isNotEmpty()) {
            return songs
        }
        if (titleName != null) {
            songs = songRepository.getFilteredSongs(
                TITLE_SELECTION,
                arrayOf(titleName.toLowerCase(Locale.getDefault()))
            )
        }
        if (songs.isNotEmpty()) {
            return songs
        }
        songs = songRepository.getFilteredSongs(
            ARTIST_SELECTION,
            arrayOf(query.toLowerCase(Locale.getDefault()))
        )

        if (songs.isNotEmpty()) {
            return songs
        }
        songs = songRepository.getFilteredSongs(
            ALBUM_SELECTION,
            arrayOf(query.toLowerCase(Locale.getDefault()))
        )
        if (songs.isNotEmpty()) {
            return songs
        }
        songs = songRepository.getFilteredSongs(
            TITLE_SELECTION,
            arrayOf(query.toLowerCase(Locale.getDefault()))

        )
        return if (songs.isNotEmpty()) {
            songs
        } else ArrayList()
    }
}
