package sk.lukasanda.clipit.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import sk.lukasanda.clipit.view.main.MainViewModel

val viewModelModule = module {
    viewModel<MainViewModel> { MainViewModel(get(), get()) }
}