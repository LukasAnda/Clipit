package sk.lukasanda.clipit.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import io.reactivex.Single
import sk.lukasanda.clipit.data.db.entity.Category
import sk.lukasanda.clipit.data.db.entity.ClipboardEntry
import sk.lukasanda.clipit.data.db.entity.DBHolder.AssignedCategory
import sk.lukasanda.clipit.data.db.entity.DBHolder.ClipboardCategoryPair

@Dao
abstract class ClipboardDao {

    @Insert(onConflict = OnConflictStrategy.FAIL)
    abstract fun insert(clipboardEntry: ClipboardEntry): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun update(clipboardEntry: ClipboardEntry)

    @Query("DELETE FROM ClipboardEntry WHERE clipboard=:clipboard")
    abstract fun delete(clipboard: String)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertAssignedCategory(assignedCategory: AssignedCategory)

    @Query("DELETE FROM AssignedCategory WHERE clipId = :id")
    abstract fun removeCategoriesFromEntry(id: Long)

    @Query("""SELECT
                        ClipboardEntry.id as clipboard_id,
                        ClipboardEntry.clipboard as clipboard_clipboard,
                        ClipboardEntry.createdAt as clipboard_createdAt,
                        AssignedCategory.n,
                        Category.*
                    FROM ClipboardEntry
                    LEFT OUTER JOIN AssignedCategory on AssignedCategory.clipId = ClipboardEntry.id
                    LEFT OUTER JOIN Category on AssignedCategory.n = Category.name
    """)
    abstract fun getAllWithAssignedCategories():Single<List<ClipboardCategoryPair>>
}