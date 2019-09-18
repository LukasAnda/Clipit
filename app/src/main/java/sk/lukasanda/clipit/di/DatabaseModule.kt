package sk.lukasanda.clipit.di

import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import sk.lukasanda.clipit.data.db.AppDatabase

val roomModule = module {
    single { AppDatabase.getInstance(androidApplication()) }
    single(createdAtStart = false) {  get<AppDatabase>().getClipboardDao()}
    single(createdAtStart = false) {  get<AppDatabase>().getCategoryDao()}
}