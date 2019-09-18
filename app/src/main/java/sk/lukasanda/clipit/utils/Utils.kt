package sk.lukasanda.clipit.utils

import android.app.ActivityManager
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Color.parseColor
import android.view.LayoutInflater
import android.view.View
import android.widget.CompoundButton.OnCheckedChangeListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.chip.Chip
import sk.lukasanda.clipit.R
import java.util.regex.Pattern

enum class Category(val color: Int) {
    GENERAL(Color.parseColor("#FFCC33")),
    TYPE(Color.parseColor("#FF355E")),
    APP(Color.parseColor("#50BFE6")),
    MISC(Color.parseColor("#FF6EFF")),
}

fun getColor(category: Category): Int {
    return category.color
}

fun View.hide() {
    this.visibility = View.GONE
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun FragmentManager.inTransaction(where: Int, fragment: Fragment, backstack: Boolean = false) {
    if (backstack) {
        this.beginTransaction().replace(where, fragment).addToBackStack(null).commit()
    } else {
        this.beginTransaction().replace(where, fragment).commit()
    }
}

fun createChipFromCategory(
    context: Context,
    category: sk.lukasanda.clipit.data.db.entity.Category,
    clickable: Boolean,
    shouldCheck: Boolean,
    onCloseListener: (() -> Unit)? = null,
    onCheckedChangeListener: OnCheckedChangeListener? = null
): Chip {

    val states = arrayOf(
        intArrayOf(-android.R.attr.state_checked), // unchecked
        intArrayOf(android.R.attr.state_checked) // checked
    )
    val chip = LayoutInflater.from(context).inflate(R.layout.item_category, null) as Chip
    chip.text = category.name
    chip.chipBackgroundColor = ColorStateList(states, intArrayOf(parseColor("#f0f0f0"), category.color))
    chip.rippleColor = ColorStateList.valueOf(Color.TRANSPARENT)
    chip.isCheckable = true
    if (shouldCheck) {
        chip.isChecked = true
    } else {
        chip.isChecked = category.selected
    }
    chip.isClickable = clickable
    onCloseListener?.let { it1 ->
        chip.isCloseIconVisible = true
        chip.setOnCloseIconClickListener {
            it1.invoke()
        }
    }
    onCheckedChangeListener?.let {
        chip.setOnCheckedChangeListener(onCheckedChangeListener)
    }

    return chip
}

fun isMyServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
    val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
        if (serviceClass.name == service.service.className) {
            return true
        }
    }
    return false
}

fun shouldGiveCategory(clipboard: String, regexes: List<String>): Boolean {
    return regexes.none { Pattern.compile(it).matcher(clipboard).find() }
}
