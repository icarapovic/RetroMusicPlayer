package code.name.monkey.retromusic.model.smartplaylist

import dev.icarapovic.music.App
import code.name.monkey.retromusic.R
import dev.icarapovic.music.domain.model.Song
import kotlinx.android.parcel.Parcelize

@Parcelize
class TopTracksPlaylist : AbsSmartPlaylist(
    name = App.getContext().getString(R.string.my_top_tracks),
    iconRes = R.drawable.ic_trending_up
) {
    override fun songs(): List<Song> {
        return topPlayedRepository.topTracks()
    }
}