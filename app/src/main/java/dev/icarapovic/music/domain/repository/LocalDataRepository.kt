package dev.icarapovic.music.domain.repository

import code.name.monkey.retromusic.model.Contributor

interface LocalDataRepository {
    fun contributors(): List<Contributor>
}