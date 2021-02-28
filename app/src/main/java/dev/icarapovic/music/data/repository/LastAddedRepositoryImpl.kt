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

import android.database.Cursor
import android.provider.MediaStore
import code.name.monkey.retromusic.util.PreferenceUtil
import dev.icarapovic.music.domain.model.Album
import dev.icarapovic.music.domain.model.Artist
import dev.icarapovic.music.domain.model.Song
import dev.icarapovic.music.domain.repository.LastAddedRepository

class LastAddedRepositoryImpl(
    private val songRepository: SongRepositoryImpl,
    private val albumRepository: AlbumRepositoryImpl,
    private val artistRepository: ArtistRepositoryImpl
) : LastAddedRepository {
    override fun recentSongs(): List<Song> {
        return songRepository.songs(makeLastAddedCursor())
    }

    override fun recentAlbums(): List<Album> {
        return albumRepository.splitIntoAlbums(recentSongs())
    }

    override fun recentArtists(): List<Artist> {
        return artistRepository.splitIntoArtists(recentAlbums())
    }

    private fun makeLastAddedCursor(): Cursor? {
        val cutoff = PreferenceUtil.lastAddedCutoff
        return songRepository.makeSongCursor(
            MediaStore.Audio.Media.DATE_ADDED + ">?",
            arrayOf(cutoff.toString()),
            MediaStore.Audio.Media.DATE_ADDED + " DESC"
        )
    }
}
