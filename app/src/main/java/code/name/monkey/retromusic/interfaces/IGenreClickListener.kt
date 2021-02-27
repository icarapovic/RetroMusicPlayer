package code.name.monkey.retromusic.interfaces

import android.view.View
import dev.icarapovic.music.domain.model.Genre

interface IGenreClickListener {
    fun onClickGenre(genre: Genre, view: View)
}