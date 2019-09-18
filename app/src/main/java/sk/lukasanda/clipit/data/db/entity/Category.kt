package sk.lukasanda.clipit.data.db.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "Category")
@Parcelize
data class Category(
    @PrimaryKey(autoGenerate = false)
    var name: String = "",
    var color: Int = 0,
    var selected: Boolean = false
): Parcelable{

    override fun toString(): String {
        return "$name-$color"
    }
}