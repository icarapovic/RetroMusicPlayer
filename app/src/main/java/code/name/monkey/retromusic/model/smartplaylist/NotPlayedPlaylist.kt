package code.name.monkey.retromusic.model.smartplaylist

import dev.icarapovic.music.App
import code.name.monkey.retromusic.R
import dev.icarapovic.music.domain.model.Song
import kotlinx.android.parcel.Parcelize

@Parcelize
class NotPlayedPlaylist : AbsSmartPlaylist(
    name = App.getContext().getString(R.string.not_recently_played),
    iconRes = R.drawable.ic_watch_later
) {
    override fun songs(): List<Song> {
        return topPlayedRepository.notRecentlyPlayedTracks()
    }
}