package dev.icarapovic.music.data.repository

import android.content.Context
import code.name.monkey.retromusic.model.Contributor
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dev.icarapovic.music.domain.repository.LocalDataRepository

class LocalDataRepositoryImpl(
    private val context: Context
) : LocalDataRepository {

    override fun contributors(): List<Contributor> {
        val jsonString = context.assets.open("contributors.json")
            .bufferedReader().use { it.readText() }

        val gsonBuilder = GsonBuilder()
        val gson = gsonBuilder.create()
        val listContributorType = object : TypeToken<List<Contributor>>() {}.type
        return gson.fromJson(jsonString, listContributorType)
    }
}