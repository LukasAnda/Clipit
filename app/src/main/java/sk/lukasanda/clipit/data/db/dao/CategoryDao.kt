package sk.lukasanda.clipit.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.reactivex.Single
import sk.lukasanda.clipit.data.db.entity.Category

@Dao
interface CategoryDao{
    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun update(category: Category)

    @Query("SELECT * FROM Category")
    fun findAllCategories(): Single<List<Category>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCategory(category: Category)
}