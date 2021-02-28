package code.name.monkey.retromusic.model

import dev.icarapovic.music.domain.repository.LastAddedRepository
import dev.icarapovic.music.domain.repository.SongRepository
import dev.icarapovic.music.domain.repository.MostPlayedRepository
import dev.icarapovic.music.domain.model.Playlist
import dev.icarapovic.music.domain.model.Song
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@KoinApiExtension
abstract class AbsCustomPlaylist(
    id: Long,
    name: String
) : Playlist(id, name), KoinComponent {

    abstract fun songs(): List<Song>

    protected val songRepository by inject<SongRepository>()

    protected val topPlayedRepository by inject<MostPlayedRepository>()

    protected val lastAddedRepository by inject<LastAddedRepository>()
}