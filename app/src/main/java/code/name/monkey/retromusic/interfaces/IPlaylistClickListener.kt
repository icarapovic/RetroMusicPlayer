package code.name.monkey.retromusic.interfaces

import android.view.View
import dev.icarapovic.music.data.db.PlaylistWithSongs

interface IPlaylistClickListener {
    fun onPlaylistClick(playlistWithSongs: PlaylistWithSongs, view: View)
}