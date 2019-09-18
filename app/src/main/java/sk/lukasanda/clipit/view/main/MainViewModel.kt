package sk.lukasanda.clipit.view.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import sk.lukasanda.clipit.data.db.dao.CategoryDao
import sk.lukasanda.clipit.data.db.dao.ClipboardDao
import sk.lukasanda.clipit.data.db.entity.Category
import sk.lukasanda.clipit.data.db.entity.ClipboardEntry
import sk.lukasanda.clipit.data.db.entity.DBHolder
import sk.lukasanda.clipit.data.db.entity.DBHolder.AssignedCategory
import sk.lukasanda.clipit.data.db.entity.DBHolder.ClipboardCategoryPair
import sk.lukasanda.clipit.utils.BaseViewModel
import sk.lukasanda.clipit.utils.with

class MainViewModel(private val clipboardDao: ClipboardDao, private val categoryDao: CategoryDao) : BaseViewModel() {

    private val mClips: MutableLiveData<List<ClipboardEntry>> = MutableLiveData()
    private val mCategories: MutableLiveData<List<Category>> = MutableLiveData()

    val clips: LiveData<List<ClipboardEntry>>
        get() = mClips

    val categories: LiveData<List<Category>>
        get() = mCategories

    fun getAllCategories() {
        launch {
            categoryDao.findAllCategories().with().subscribe({
                mCategories.postValue(it)
            }, {
            })
        }
    }

    fun addCategory(category: Category) {
        launch {
            Completable.fromAction {
                categoryDao.insertCategory(category)
            }.with().subscribe({}, {})
        }
    }

    fun updateAllCategories(categories: List<Category>) {
        launch {
            Observable.fromIterable(categories).concatMapCompletable {
                Completable.fromCallable { categoryDao.update(it) }
            }.with().subscribe({
                getAllClipsNew()
            }, {
                getAllClipsNew()
            })
        }
    }

    fun getAllClipsNew() {
        launch {
            clipboardDao.getAllWithAssignedCategories()
                .zipWith(
                    categoryDao.findAllCategories(),
                    BiFunction<List<ClipboardCategoryPair>, List<Category>, Pair<List<Category>, List<ClipboardCategoryPair>>> { values, categories ->
                        return@BiFunction Pair(categories, values)
                    })
                .with().subscribe({
                    val newList = mutableListOf<ClipboardEntry>()
                    newList.addAll(DBHolder.group(it.second))
                    if (it.first.any { it.selected }) {
                        val returnlist = newList.filter { it.categories.any { it?.selected ?: false } }
                        newList.clear()
                        newList.addAll(returnlist)
                    }
                    mClips.postValue(newList)
                }, {
                    mClips.postValue(mutableListOf())
                })
        }
        getAllCategories()
    }

    fun removeClip(clipboardEntry: ClipboardEntry) {
        launch {
            Completable.fromCallable { clipboardDao.delete(clipboardEntry.clipboard) }.with().subscribe({
                Log.d("REMOVE", "Success")
            }, {
                Log.e("REMOVE", "Message", it)
            })
        }
    }

    fun updateClipboard(clipboardEntry: ClipboardEntry) {
        launch {
            Single.fromCallable {
                clipboardDao.removeCategoriesFromEntry(clipboardEntry.id)
            }.flatMap {
                Single.fromCallable {
                    clipboardDao.update(clipboardEntry)
                }
            }
                .flatMapObservable {
                    Observable.fromIterable(clipboardEntry.categories.filterNotNull())
                }.concatMapCompletable {
                    Completable.fromAction {
                        clipboardDao.insertAssignedCategory(
                            AssignedCategory(
                                it.name,
                                clipboardEntry.id
                            )
                        )
                    }
                }.with().subscribe({}, {})
        }
    }
}