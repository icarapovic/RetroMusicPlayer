package code.name.monkey.retromusic.model.smartplaylist

import dev.icarapovic.music.App
import code.name.monkey.retromusic.R
import dev.icarapovic.music.domain.model.Song
import kotlinx.android.parcel.Parcelize

@Parcelize
class LastAddedPlaylist : AbsSmartPlaylist(
    name = App.getContext().getString(R.string.last_added),
    iconRes = R.drawable.ic_library_add
) {
    override fun songs(): List<Song> {
        return lastAddedRepository.recentSongs()
    }
}