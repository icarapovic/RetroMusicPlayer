package code.name.monkey.retromusic.model.smartplaylist

import dev.icarapovic.music.App
import code.name.monkey.retromusic.R
import dev.icarapovic.music.domain.model.Song
import kotlinx.android.parcel.Parcelize

@Parcelize
class ShuffleAllPlaylist : AbsSmartPlaylist(
    name = App.getContext().getString(R.string.action_shuffle_all),
    iconRes = R.drawable.ic_shuffle
) {
    override fun songs(): List<Song> {
        return songRepository.songs()
    }
}