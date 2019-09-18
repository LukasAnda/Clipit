package sk.lukasanda.clipit.utils

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

fun <T> Single<T>.with(): Single<T> = subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
fun <T> Flowable<T>.with(): Flowable<T> = subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
fun Completable.with(): Completable = subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

inline fun <reified T> List<Single<T>>.singleZip(): Single<List<T>> =
    when (isEmpty()) {
        true -> Single.just(emptyList())
        else -> Single.zip(this) { it.toList() }
//            .cast() // cast is from RxKotlin, but, you can avoid it like this:
            .map {
                @Suppress("UNCHECKED_CAST")
                it as List<T>
            }
    }