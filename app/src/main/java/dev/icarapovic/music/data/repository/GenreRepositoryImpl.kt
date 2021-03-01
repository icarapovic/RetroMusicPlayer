package dev.icarapovic.music.data.repository

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.BaseColumns
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Genres
import code.name.monkey.retromusic.Constants.IS_MUSIC
import code.name.monkey.retromusic.Constants.baseProjection
import dev.icarapovic.music.extensions.getLong
import dev.icarapovic.music.extensions.getString
import dev.icarapovic.music.extensions.getStringOrNull
import dev.icarapovic.music.domain.model.Genre
import dev.icarapovic.music.domain.model.Song
import code.name.monkey.retromusic.util.PreferenceUtil
import dev.icarapovic.music.domain.repository.GenreRepository
import dev.icarapovic.music.domain.repository.SongRepository
import dev.icarapovic.music.extensions.getInt

class GenreRepositoryImpl(
    private val contentResolver: ContentResolver,
    private val songRepository: SongRepository
) : GenreRepository {

    override fun genres(): List<Genre> {
        return getGenresFromCursor(makeGenreCursor())
    }

    override fun songs(genreId: Long): List<Song> {
        // The genres table only stores songs that have a genre specified,
        // so we need to get songs without a genre a different way.
        return if (genreId == -1L) {
            getSongsWithNoGenre()
        } else {
            val cursor = makeGenreSongCursor(genreId)
            return songs(cursor)
        }
    }

    private fun getGenreFromCursor(cursor: Cursor): Genre {
        val id = cursor.getLong(Genres._ID)
        val name = cursor.getStringOrNull(Genres.NAME)
        val songCount = songs(id).size
        return Genre(id, name ?: "", songCount)

    }

    private fun getGenreFromCursorWithOutSongs(cursor: Cursor): Genre {
        val id = cursor.getLong(Genres._ID)
        val name = cursor.getString(Genres.NAME)
        return Genre(id, name, -1)
    }

    private fun getSongsWithNoGenre(): List<Song> {
        val selection =
            BaseColumns._ID + " NOT IN " + "(SELECT " + Genres.Members.AUDIO_ID + " FROM audio_genres_map)"
        return songRepository.getFilteredSongs(selection)
    }

    private fun hasSongsWithNoGenre(): Boolean {
        val allSongsCursor = songRepository.getAllSongs()
        val allSongsWithGenreCursor = makeAllSongsWithGenreCursor() ?: return false

        val hasSongsWithNoGenre = allSongsCursor.size > allSongsWithGenreCursor.count
        allSongsWithGenreCursor.close()
        return hasSongsWithNoGenre
    }

    private fun makeAllSongsWithGenreCursor(): Cursor? {
        println(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI.toString())
        return contentResolver.query(
            Uri.parse("content://media/external/audio/genres/all/members"),
            arrayOf(Genres.Members.AUDIO_ID), null, null, null
        )
    }

    private fun makeGenreSongCursor(genreId: Long): Cursor? {
        return try {
            contentResolver.query(
                Genres.Members.getContentUri("external", genreId),
                baseProjection,
                IS_MUSIC,
                null,
                PreferenceUtil.songSortOrder
            )
        } catch (e: SecurityException) {
            return null
        }
    }

    private fun getGenresFromCursor(cursor: Cursor?): ArrayList<Genre> {
        val genres = arrayListOf<Genre>()
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val genre = getGenreFromCursor(cursor)
                    if (genre.songCount > 0) {
                        genres.add(genre)
                    } else {
                        // try to remove the empty genre from the media store
                        try {
                            contentResolver.delete(
                                Genres.EXTERNAL_CONTENT_URI,
                                Genres._ID + " == " + genre.id,
                                null
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
        return genres
    }

    private fun getGenresFromCursorForSearch(cursor: Cursor?): List<Genre> {
        val genres = mutableListOf<Genre>()
        if (cursor != null && cursor.moveToFirst()) {
            do {
                genres.add(getGenreFromCursorWithOutSongs(cursor))
            } while (cursor.moveToNext())
        }
        cursor?.close()
        return genres
    }

    private fun makeGenreCursor(): Cursor? {
        val projection = arrayOf(Genres._ID, Genres.NAME)
        return try {
            contentResolver.query(
                Genres.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                PreferenceUtil.genreSortOrder
            )
        } catch (e: SecurityException) {
            return null
        }
    }

    // TODO duplicate SongRepositoryImpl
    private fun getSongFromCursorImpl(
        cursor: Cursor
    ): Song {
        val id = cursor.getLong(MediaStore.Audio.AudioColumns._ID)
        val title = cursor.getString(MediaStore.Audio.AudioColumns.TITLE)
        val trackNumber = cursor.getInt(MediaStore.Audio.AudioColumns.TRACK)
        val year = cursor.getInt(MediaStore.Audio.AudioColumns.YEAR)
        val duration = cursor.getLong(MediaStore.Audio.AudioColumns.DURATION)
        val data = cursor.getString(MediaStore.Audio.AudioColumns.DATA)
        val dateModified = cursor.getLong(MediaStore.Audio.AudioColumns.DATE_MODIFIED)
        val albumId = cursor.getLong(MediaStore.Audio.AudioColumns.ALBUM_ID)
        val albumName = cursor.getStringOrNull(MediaStore.Audio.AudioColumns.ALBUM)
        val artistId = cursor.getLong(MediaStore.Audio.AudioColumns.ARTIST_ID)
        val artistName = cursor.getStringOrNull(MediaStore.Audio.AudioColumns.ARTIST)
        val composer = cursor.getStringOrNull(MediaStore.Audio.AudioColumns.COMPOSER)
        val albumArtist = cursor.getStringOrNull("album_artist")
        return Song(
            id,
            title,
            trackNumber,
            year,
            duration,
            data,
            dateModified,
            albumId,
            albumName ?: "",
            artistId,
            artistName ?: "",
            composer ?: "",
            albumArtist ?: ""
        )
    }

    // TODO duplicate SongRepositoryImpl
    private fun songs(cursor: Cursor?): List<Song> {
        val songs = arrayListOf<Song>()
        if (cursor != null && cursor.moveToFirst()) {
            do {
                songs.add(getSongFromCursorImpl(cursor))
            } while (cursor.moveToNext())
        }
        cursor?.close()
        return songs
    }
}
