package dev.icarapovic.music.data.repository

import android.provider.MediaStore.Audio.AudioColumns
import code.name.monkey.retromusic.helper.SortOrder
import code.name.monkey.retromusic.util.PreferenceUtil
import dev.icarapovic.music.domain.model.Album
import dev.icarapovic.music.domain.model.Song
import dev.icarapovic.music.domain.repository.AlbumRepository
import dev.icarapovic.music.domain.repository.SongRepository

class AlbumRepositoryImpl(
    private val songRepository: SongRepository
) : AlbumRepository {

    override fun getAllAlbums(): List<Album> {
        val songs = songRepository.getFilteredSongs(
                null,
                null,
                getSongLoaderSortOrder()
        )
        return splitIntoAlbums(songs)
    }

    override fun getAlbumsByName(query: String): List<Album> {
        val songs = songRepository.getFilteredSongs(
                AudioColumns.ALBUM + " LIKE ?",
                arrayOf("%$query%"),
                getSongLoaderSortOrder()
        )
        return splitIntoAlbums(songs)
    }

    override fun getAlbumById(albumId: Long): Album {
        val songs = songRepository.getFilteredSongs(
            AudioColumns.ALBUM_ID + "=?",
            arrayOf(albumId.toString()),
            getSongLoaderSortOrder()
        )
        val album = Album(albumId, songs)
        sortAlbumSongs(album)
        return album
    }

    fun splitIntoAlbums(
        songs: List<Song>
    ): List<Album> {
        return songs.groupBy { it.albumId }
            .map { sortAlbumSongs(Album(it.key, it.value)) }
    }

    private fun sortAlbumSongs(album: Album): Album {
        val songs = when (PreferenceUtil.albumDetailSongSortOrder) {
            SortOrder.AlbumSongSortOrder.SONG_TRACK_LIST -> album.songs.sortedWith { o1, o2 ->
                o1.trackNumber.compareTo(o2.trackNumber)
            }
            SortOrder.AlbumSongSortOrder.SONG_A_Z -> album.songs.sortedWith { o1, o2 ->
                o1.title.compareTo(o2.title)
            }
            SortOrder.AlbumSongSortOrder.SONG_Z_A -> album.songs.sortedWith { o1, o2 ->
                o2.title.compareTo(o1.title)
            }
            SortOrder.AlbumSongSortOrder.SONG_DURATION -> album.songs.sortedWith { o1, o2 ->
                o1.duration.compareTo(o2.duration)
            }
            else -> throw IllegalArgumentException("invalid ${PreferenceUtil.albumDetailSongSortOrder}")
        }
        return album.copy(songs = songs)
    }

    private fun getSongLoaderSortOrder(): String {
        return PreferenceUtil.albumSortOrder + ", " +
                PreferenceUtil.albumSongSortOrder
    }


}
