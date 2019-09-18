package sk.lukasanda.clipit.view.dialogs

import android.content.Context
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.dialog_categories.view.group
import sk.lukasanda.clipit.R
import sk.lukasanda.clipit.data.db.entity.Category
import sk.lukasanda.clipit.utils.createChipFromCategory

class AllCategoriesDialog : BottomSheetDialogFragment() {

    private val selectedCategories = mutableListOf<Category>()

    var listener: ((List<Category>) -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val categories = arrayListOf<Category>()
        arguments?.let {
            it.getParcelableArrayList<Category>(CATEGORIES)?.let {
                categories.addAll(it)
            }
        }
        val v = inflater.inflate(R.layout.dialog_categories, container, false)
        categories.forEach {
            v.group.addView(
                createChipFromCategory(
                    requireContext(),
                    it,
                    true,
                    false,
                    onCheckedChangeListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                        it.selected = isChecked
                        if (selectedCategories.contains(it)) {
                            selectedCategories.remove(it)
                        } else {
                            selectedCategories.add(it)
                        }
                    })
            )
        }
        return v
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        listener?.invoke(selectedCategories)
    }

    companion object {
        const val CATEGORIES = "Categories"
        fun newInstance(categories: ArrayList<Category>): AllCategoriesDialog = AllCategoriesDialog().apply {
            arguments = Bundle().apply {
                putParcelableArrayList(CATEGORIES, categories)
            }
        }
    }
}