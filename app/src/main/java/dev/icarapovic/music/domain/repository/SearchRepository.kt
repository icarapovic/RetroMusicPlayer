package dev.icarapovic.music.domain.repository

import android.content.Context

interface SearchRepository {
    fun searchAll(context: Context, query: String?): MutableList<Any>
}