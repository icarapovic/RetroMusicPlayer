package dev.icarapovic.music.data.repository

import android.provider.MediaStore
import code.name.monkey.retromusic.util.PreferenceUtil
import dev.icarapovic.music.domain.model.Album
import dev.icarapovic.music.domain.model.Artist
import dev.icarapovic.music.domain.model.Song
import dev.icarapovic.music.domain.repository.LastAddedRepository
import dev.icarapovic.music.domain.repository.SongRepository

class LastAddedRepositoryImpl(
    private val songRepository: SongRepository,
    private val albumRepository: AlbumRepositoryImpl,
    private val artistRepository: ArtistRepositoryImpl
) : LastAddedRepository {
    override fun recentSongs(): List<Song> {
        val cutoff = PreferenceUtil.lastAddedCutoff
        return songRepository.getFilteredSongs(
            MediaStore.Audio.Media.DATE_ADDED + ">?",
            arrayOf(cutoff.toString()),
            MediaStore.Audio.Media.DATE_ADDED + " DESC"
        )
    }

    override fun recentAlbums(): List<Album> {
        return albumRepository.splitIntoAlbums(recentSongs())
    }

    override fun recentArtists(): List<Artist> {
        return artistRepository.splitIntoArtists(recentAlbums())
    }
}
