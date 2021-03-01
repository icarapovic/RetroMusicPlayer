package dev.icarapovic.music.data.repository

import android.content.Context
import android.database.Cursor
import android.provider.BaseColumns
import android.provider.MediaStore
import code.name.monkey.retromusic.Constants
import code.name.monkey.retromusic.Constants.NUMBER_OF_TOP_TRACKS
import dev.icarapovic.music.domain.model.Album
import dev.icarapovic.music.domain.model.Artist
import dev.icarapovic.music.domain.model.Song
import dev.icarapovic.music.data.providers.HistoryStore
import dev.icarapovic.music.data.providers.SongPlayCountStore
import code.name.monkey.retromusic.util.PreferenceUtil
import dev.icarapovic.music.data.providers.BlacklistStore
import dev.icarapovic.music.domain.repository.MostPlayedRepository
import dev.icarapovic.music.domain.repository.SongRepository
import dev.icarapovic.music.extensions.getInt
import dev.icarapovic.music.extensions.getLong
import dev.icarapovic.music.extensions.getString
import dev.icarapovic.music.extensions.getStringOrNull
import java.util.ArrayList


class MostPlayedRepositoryImpl(
    private val context: Context,
    private val songRepository: SongRepository,
    private val albumRepository: AlbumRepositoryImpl,
    private val artistRepository: ArtistRepositoryImpl
) : MostPlayedRepository {

    override fun recentlyPlayedTracks(): List<Song> {
        return songs(makeRecentTracksCursorAndClearUpDatabase())
    }

    override fun topTracks(): List<Song> {
        return songs(makeTopTracksCursorAndClearUpDatabase())
    }

    override fun notRecentlyPlayedTracks(): List<Song> {
        val allSongs = mutableListOf<Song>().apply {
            addAll(
                songRepository.getFilteredSongs(
                        null, null,
                        MediaStore.Audio.Media.DATE_ADDED + " ASC"
                    )
                )
        }
        val playedSongs = songs(
            makePlayedTracksCursorAndClearUpDatabase()
        )
        val notRecentlyPlayedSongs = songs(
            makeNotRecentTracksCursorAndClearUpDatabase()
        )
        allSongs.removeAll(playedSongs)
        allSongs.addAll(notRecentlyPlayedSongs)
        return allSongs
    }

    override fun topAlbums(): List<Album> {
        return albumRepository.splitIntoAlbums(topTracks())
    }

    override fun topArtists(): List<Artist> {
        return artistRepository.splitIntoArtists(topAlbums())
    }


    private fun makeTopTracksCursorAndClearUpDatabase(): Cursor? {
        val retCursor = makeTopTracksCursorImpl()
        // clean up the databases with any ids not found
        if (retCursor != null) {
            val missingIds = retCursor.missingIds
            if (missingIds != null && missingIds.size > 0) {
                for (id in missingIds) {
                    SongPlayCountStore.getInstance(context).removeItem(id)
                }
            }
        }
        return retCursor
    }

    private fun makeRecentTracksCursorImpl(): SortedLongCursor? {
        // first get the top results ids from the internal database
        val songs = HistoryStore.getInstance(context).queryRecentIds()
        songs.use {
            return makeSortedCursor(
                it,
                it.getColumnIndex(HistoryStore.RecentStoreColumns.ID)
            )
        }
    }

    private fun makeTopTracksCursorImpl(): SortedLongCursor? {
        // first get the top results ids from the internal database
        val cursor =
            SongPlayCountStore.getInstance(context).getTopPlayedResults(NUMBER_OF_TOP_TRACKS)

        cursor.use { songs ->
            return makeSortedCursor(
                songs,
                songs.getColumnIndex(SongPlayCountStore.SongPlayCountColumns.ID)
            )
        }
    }

    private fun makeSortedCursor(
        cursor: Cursor?, idColumn: Int
    ): SortedLongCursor? {

        if (cursor != null && cursor.moveToFirst()) {
            // create the list of ids to select against
            val selection = StringBuilder()
            selection.append(BaseColumns._ID)
            selection.append(" IN (")

            // this tracks the order of the ids
            val order = LongArray(cursor.count)

            var id = cursor.getLong(idColumn)
            selection.append(id)
            order[cursor.position] = id

            while (cursor.moveToNext()) {
                selection.append(",")

                id = cursor.getLong(idColumn)
                order[cursor.position] = id
                selection.append(id.toString())
            }

            selection.append(")")

            // get a list of songs with the data given the selection statement
            val songCursor = makeSongCursor(selection.toString(), null)
            if (songCursor != null) {
                // now return the wrapped TopTracksCursor to handle sorting given order
                return SortedLongCursor(
                    songCursor,
                    order,
                    BaseColumns._ID
                )
            }
        }

        return null
    }

    private fun makeRecentTracksCursorAndClearUpDatabase(): Cursor? {
        return makeRecentTracksCursorAndClearUpDatabaseImpl(
            ignoreCutoffTime = false,
            reverseOrder = false
        )
    }

    private fun makePlayedTracksCursorAndClearUpDatabase(): Cursor? {
        return makeRecentTracksCursorAndClearUpDatabaseImpl(
            ignoreCutoffTime = true,
            reverseOrder = false
        )
    }

    private fun makeNotRecentTracksCursorAndClearUpDatabase(): Cursor? {
        return makeRecentTracksCursorAndClearUpDatabaseImpl(
            ignoreCutoffTime = false,
            reverseOrder = true
        )
    }

    private fun makeRecentTracksCursorAndClearUpDatabaseImpl(
        ignoreCutoffTime: Boolean,
        reverseOrder: Boolean
    ): SortedLongCursor? {
        val retCursor = makeRecentTracksCursorImpl(ignoreCutoffTime, reverseOrder)
        // clean up the databases with any ids not found
        // clean up the databases with any ids not found
        if (retCursor != null) {
            val missingIds = retCursor.missingIds
            if (missingIds != null && missingIds.size > 0) {
                for (id in missingIds) {
                    HistoryStore.getInstance(context).removeSongId(id)
                }
            }
        }
        return retCursor
    }

    private fun makeRecentTracksCursorImpl(
        ignoreCutoffTime: Boolean,
        reverseOrder: Boolean
    ): SortedLongCursor? {
        val cutoff =
            (if (ignoreCutoffTime) 0 else PreferenceUtil.getRecentlyPlayedCutoffTimeMillis()).toLong()
        val songs =
            HistoryStore.getInstance(context).queryRecentIds(cutoff * if (reverseOrder) -1 else 1)
        return songs.use {
            makeSortedCursor(
                it,
                it.getColumnIndex(HistoryStore.RecentStoreColumns.ID)
            )
        }
    }

    // TODO duplicate from SongRepositoryImpl
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

    @JvmOverloads
    fun makeSongCursor(

        selection: String?,
        selectionValues: Array<String>?,
        sortOrder: String = PreferenceUtil.songSortOrder
    ): Cursor? {
        var selectionFinal = selection
        var selectionValuesFinal = selectionValues
        selectionFinal = if (selection != null && selection.trim { it <= ' ' } != "") {
            "${Constants.IS_MUSIC} AND $selectionFinal"
        } else {
            Constants.IS_MUSIC
        }

        // Blacklist
        val paths = BlacklistStore.getInstance(context).paths
        if (paths.isNotEmpty()) {
            selectionFinal = generateBlacklistSelection(selectionFinal, paths.size)
            selectionValuesFinal = addBlacklistSelectionValues(selectionValuesFinal, paths)
        }
        selectionFinal =
            selectionFinal + " AND " + MediaStore.Audio.Media.DURATION + ">= " + (PreferenceUtil.filterLength * 1000)

        val uri = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }
        return try {
            context.contentResolver.query(
                uri,
                Constants.baseProjection,
                selectionFinal,
                selectionValuesFinal,
                sortOrder
            )
        } catch (ex: SecurityException) {
            return null
        }
    }

    private fun generateBlacklistSelection(
        selection: String?,
        pathCount: Int
    ): String {
        val newSelection = StringBuilder(
            if (selection != null && selection.trim { it <= ' ' } != "") "$selection AND " else "")
        newSelection.append(MediaStore.Audio.AudioColumns.DATA + " NOT LIKE ?")
        for (i in 0 until pathCount - 1) {
            newSelection.append(" AND " + MediaStore.Audio.AudioColumns.DATA + " NOT LIKE ?")
        }
        return newSelection.toString()
    }

    private fun addBlacklistSelectionValues(
        selectionValues: Array<String>?,
        paths: ArrayList<String>
    ): Array<String>? {
        var selectionValuesFinal = selectionValues
        if (selectionValuesFinal == null) {
            selectionValuesFinal = emptyArray()
        }
        val newSelectionValues = Array(selectionValuesFinal.size + paths.size) {
            "n = $it"
        }
        System.arraycopy(selectionValuesFinal, 0, newSelectionValues, 0, selectionValuesFinal.size)
        for (i in selectionValuesFinal.size until newSelectionValues.size) {
            newSelectionValues[i] = paths[i - selectionValuesFinal.size] + "%"
        }
        return newSelectionValues
    }
}
