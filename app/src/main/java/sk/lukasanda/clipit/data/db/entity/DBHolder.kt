package sk.lukasanda.clipit.data.db.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey

class DBHolder{
    @Entity(
        primaryKeys = ["clipId", "n"], foreignKeys = [
            ForeignKey(
                entity = Category::class,
                parentColumns = ["name"],
                childColumns = ["n"],
                onDelete = ForeignKey.CASCADE
            ),
            ForeignKey(
                entity = ClipboardEntry::class,
                parentColumns = ["id"],
                childColumns = ["clipId"],
                onDelete = ForeignKey.CASCADE
            )]
    )
    data class AssignedCategory(
        var n: String = "",
        var clipId: Long = 0L
    )

    class ClipboardCategoryPair {
        @Embedded(prefix = "clipboard_")
        lateinit var clipboardEntry: ClipboardEntry

        @Embedded
        var category : Category? = null
    }

    data class ClipboardAndCategories(val clipboardEntry: ClipboardEntry, val categories: List<Category?>)

    companion object {
        fun group(clipboardAndCategories: List<ClipboardCategoryPair>): List<ClipboardEntry>{
            return mutableListOf<ClipboardEntry>().also {items->
                clipboardAndCategories
                    .groupBy(keySelector = {it.clipboardEntry}, valueTransform = {it.category})
                    .forEach {
                        items.add(ClipboardEntry(it.key.id, it.key.clipboard, it.key.createdAt, it.value.toMutableList()))
                    }
            }
        }
    }
}