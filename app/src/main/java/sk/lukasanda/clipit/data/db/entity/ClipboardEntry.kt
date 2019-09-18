package sk.lukasanda.clipit.data.db.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import org.joda.time.DateTime

@Entity(tableName = "ClipboardEntry", indices = [Index(value = ["clipboard"], unique = true)])
@Parcelize
data class ClipboardEntry(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,
    var clipboard: String = "",
    var createdAt: DateTime = DateTime.now(),
    @Ignore
    var categories: MutableList<Category?> = mutableListOf()
): Parcelable