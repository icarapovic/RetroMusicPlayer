package code.name.monkey.retromusic

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.icarapovic.music.data.db.BlackListStoreDao
import dev.icarapovic.music.data.db.BlackListStoreEntity
import dev.icarapovic.music.data.db.PlaylistWithSongs
import dev.icarapovic.music.data.db.RetroDatabase
import dev.icarapovic.music.ui.fragments.LibraryViewModel
import dev.icarapovic.music.ui.fragments.albums.AlbumDetailsViewModel
import dev.icarapovic.music.ui.fragments.artists.ArtistDetailsViewModel
import dev.icarapovic.music.ui.fragments.genres.GenreDetailsViewModel
import dev.icarapovic.music.ui.fragments.playlists.PlaylistDetailsViewModel
import dev.icarapovic.music.domain.model.Genre
import dev.icarapovic.music.data.network.provideDefaultCache
import dev.icarapovic.music.data.network.provideLastFmRest
import dev.icarapovic.music.data.network.provideLastFmRetrofit
import dev.icarapovic.music.data.network.provideOkHttp
import dev.icarapovic.music.data.repository.*
import code.name.monkey.retromusic.util.FilePathUtil
import dev.icarapovic.music.domain.repository.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val networkModule = module {

    factory {
        provideDefaultCache()
    }
    factory {
        provideOkHttp(get(), get())
    }
    single {
        provideLastFmRetrofit(get())
    }
    single {
        provideLastFmRest(get())
    }
}

private val roomModule = module {

    single {
        Room.databaseBuilder(androidContext(), RetroDatabase::class.java, "playlist.db")
            .allowMainThreadQueries()
            .addCallback(object : RoomDatabase.Callback() {
                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)
                    GlobalScope.launch(IO) {
                        FilePathUtil.blacklistFilePaths().map {
                            get<BlackListStoreDao>().insertBlacklistPath(BlackListStoreEntity(it))
                        }
                    }
                }
            })
            .fallbackToDestructiveMigration()
            .build()
    }
    factory {
        get<RetroDatabase>().lyricsDao()
    }

    factory {
        get<RetroDatabase>().playlistDao()
    }

    factory {
        get<RetroDatabase>().blackListStore()
    }

    factory {
        get<RetroDatabase>().playCountDao()
    }

    factory {
        get<RetroDatabase>().historyDao()
    }

    single {
        RoomRepositoryImpl(get(), get(), get(), get(), get())
    } bind RoomRepository::class
}
private val mainModule = module {
    single {
        androidContext().contentResolver
    }
}
private val dataModule = module {
    single {
        RepositoryImpl(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
        )
    } bind Repository::class

    single {
        SongRepositoryImpl(get())
    } bind SongRepository::class

    single {
        GenreRepositoryImpl(get(), get())
    } bind GenreRepository::class

    single {
        AlbumRepositoryImpl(get())
    } bind AlbumRepository::class

    single {
        ArtistRepositoryImpl(get(), get())
    } bind ArtistRepository::class

    single {
        PlaylistRepositoryImpl(get())
    } bind PlaylistRepository::class

    single {
        MostPlayedRepositoryImpl(get(), get(), get(), get())
    } bind MostPlayedRepository::class

    single {
        LastAddedRepositoryImpl(
            get(),
            get(),
            get()
        )
    } bind LastAddedRepository::class

    single {
        SearchRepositoryImpl(
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
    single {
        LocalDataRepositoryImpl(get())
    } bind LocalDataRepository::class
}

private val viewModules = module {

    viewModel {
        LibraryViewModel(get())
    }

    viewModel { (albumId: Long) ->
        AlbumDetailsViewModel(
            get(),
            albumId
        )
    }

    viewModel { (artistId: Long) ->
        ArtistDetailsViewModel(
            get(),
            artistId
        )
    }

    viewModel { (playlist: PlaylistWithSongs) ->
        PlaylistDetailsViewModel(
            get(),
            playlist
        )
    }

    viewModel { (genre: Genre) ->
        GenreDetailsViewModel(
            get(),
            genre
        )
    }
}

val appModules = listOf(mainModule, dataModule, viewModules, networkModule, roomModule)