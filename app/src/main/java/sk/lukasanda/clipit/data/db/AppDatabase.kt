package sk.lukasanda.clipit.data.db

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import io.reactivex.Completable
import sk.lukasanda.clipit.data.db.AppDatabase.Companion.DB_VERSION
import sk.lukasanda.clipit.data.db.converter.TimeConverter
import sk.lukasanda.clipit.data.db.dao.CategoryDao
import sk.lukasanda.clipit.data.db.dao.ClipboardDao
import sk.lukasanda.clipit.data.db.entity.Category
import sk.lukasanda.clipit.data.db.entity.ClipboardEntry
import sk.lukasanda.clipit.data.db.entity.DBHolder.AssignedCategory
import sk.lukasanda.clipit.utils.Category.GENERAL
import sk.lukasanda.clipit.utils.getColor
import sk.lukasanda.clipit.utils.with

@Database(
    entities = [ClipboardEntry::class, Category::class, AssignedCategory::class],
    version = DB_VERSION,
    exportSchema = true
)
@TypeConverters(TimeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getClipboardDao(): ClipboardDao
    abstract fun getCategoryDao(): CategoryDao

    companion object {
        const val DB_VERSION = 2
        private const val DB_NAME = "clipboard.db"
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: build(context).also { INSTANCE = it }
            }

        private fun build(context: Context) =
            Room
                .databaseBuilder(context.applicationContext, AppDatabase::class.java, DB_NAME)
                .addCallback(object : RoomDatabase.Callback() {
                    @SuppressLint("CheckResult")
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        Completable.fromCallable {
                            AppDatabase.getInstance(context).getCategoryDao().insertCategory(
                                Category("Unfiled", getColor(GENERAL))
                            )
                        }
                            .with()
                            .subscribe({}, {})
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
    }
}