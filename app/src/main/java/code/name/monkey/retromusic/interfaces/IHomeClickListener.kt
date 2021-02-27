package code.name.monkey.retromusic.interfaces

import dev.icarapovic.music.domain.model.Album
import dev.icarapovic.music.domain.model.Artist
import dev.icarapovic.music.domain.model.Genre

interface IHomeClickListener {
    fun onAlbumClick(album: Album)

    fun onArtistClick(artist: Artist)

    fun onGenreClick(genre: Genre)
}