package dev.icarapovic.music.data.repository

import android.provider.MediaStore.Audio.AudioColumns
import code.name.monkey.retromusic.util.PreferenceUtil
import dev.icarapovic.music.domain.model.Album
import dev.icarapovic.music.domain.model.Artist
import dev.icarapovic.music.domain.repository.ArtistRepository
import dev.icarapovic.music.domain.repository.SongRepository

class ArtistRepositoryImpl(
    private val songRepository: SongRepository,
    private val albumRepository: AlbumRepositoryImpl
) : ArtistRepository {

    private fun getSongLoaderSortOrder(): String {
        return PreferenceUtil.artistSortOrder + ", " +
                PreferenceUtil.artistAlbumSortOrder + ", " +
                PreferenceUtil.artistSongSortOrder
    }

    override fun artist(artistId: Long): Artist {
        if (artistId == Artist.VARIOUS_ARTISTS_ID) {
            // Get Various Artists
            val songs = songRepository.getFilteredSongs(
                null,
                null,
                getSongLoaderSortOrder()
            )
            val albums = albumRepository.splitIntoAlbums(songs)
                .filter { it.albumArtist == Artist.VARIOUS_ARTISTS_DISPLAY_NAME }
            return Artist(Artist.VARIOUS_ARTISTS_ID, albums)
        }

        val songs = songRepository.getFilteredSongs(
            AudioColumns.ARTIST_ID + "=?",
            arrayOf(artistId.toString()),
            getSongLoaderSortOrder()
        )
        return Artist(artistId, albumRepository.splitIntoAlbums(songs))
    }

    override fun artists(): List<Artist> {
        val songs = songRepository.getFilteredSongs(
            null, null,
            getSongLoaderSortOrder()

        )
        return splitIntoArtists(albumRepository.splitIntoAlbums(songs))
    }

    override fun albumArtists(): List<Artist> {
        val songs = songRepository.getFilteredSongs(
            null,
            null,
            getSongLoaderSortOrder()
        )

        return splitIntoAlbumArtists(albumRepository.splitIntoAlbums(songs))
    }

    override fun artists(query: String): List<Artist> {
        val songs = songRepository.getFilteredSongs(
                AudioColumns.ARTIST + " LIKE ?",
                arrayOf("%$query%"),
                getSongLoaderSortOrder()
        )
        return splitIntoArtists(albumRepository.splitIntoAlbums(songs))
    }


    private fun splitIntoAlbumArtists(albums: List<Album>): List<Artist> {
        return albums.groupBy { it.albumArtist }
            .map {
                val currentAlbums = it.value
                if (currentAlbums.isNotEmpty()) {
                    if (currentAlbums[0].albumArtist == Artist.VARIOUS_ARTISTS_DISPLAY_NAME) {
                        Artist(Artist.VARIOUS_ARTISTS_ID, currentAlbums)
                    } else {
                        Artist(currentAlbums[0].artistId, currentAlbums)
                    }
                } else {
                    Artist.empty
                }
            }
    }


    fun splitIntoArtists(albums: List<Album>): List<Artist> {
        return albums.groupBy { it.artistId }
            .map { Artist(it.key, it.value) }
    }
}