package sk.lukasanda.clipit.view.dialogs

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.CompoundButton.OnCheckedChangeListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_categories.view.group
import kotlinx.android.synthetic.main.dialog_new_category.view.new_category
import kotlinx.android.synthetic.main.dialog_new_category.view.save
import sk.lukasanda.clipit.R
import sk.lukasanda.clipit.data.db.entity.Category
import sk.lukasanda.clipit.utils.Category.MISC
import sk.lukasanda.clipit.utils.createChipFromCategory
import sk.lukasanda.clipit.utils.getColor

class NewCategoryDialog : BottomSheetDialogFragment() {

    var listener: ((Category) -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.dialog_new_category, container, false)
        arguments?.let {
            it.getParcelableArrayList<Category>(CATEGORIES)?.let {
                it.forEach {
                    if (it.name != "Unfiled") {
                        v.group.addView(
                            createChipFromCategory(
                                requireContext(),
                                it,
                                true,
                                true,
                                onCheckedChangeListener = OnCheckedChangeListener { buttonView, isChecked ->
                                    listener?.invoke(it)
                                    this.dismiss()
                                })
                        )
                    }
                }
            }
        }
        v.save.setOnClickListener {
            listener?.invoke(Category(v.new_category.text.toString(), getColor(MISC)))
            this.dismiss()
        }

        v.new_category.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    if (s.length < 3) {
                        updateCategories()
                    } else {
                        updateCategories(s.toString())
                    }
                }
            }
        })

        v.new_category.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                listener?.invoke(Category(v.new_category.text.toString(), getColor(MISC)))
                this.dismiss()
                true
            } else {
                false
            }
        }
        return v
    }

    fun updateCategories() {
        view?.group?.removeAllViews()
        arguments?.getParcelableArrayList<Category>(CATEGORIES)?.forEach {
            view?.group?.addView(
                createChipFromCategory(
                    requireContext(),
                    it,
                    true,
                    true,
                    onCheckedChangeListener = OnCheckedChangeListener { buttonView, isChecked ->
                        listener?.invoke(it)
                        this.dismiss()
                    })
            )
        }
    }

    fun updateCategories(filter: String) {
        view?.group?.removeAllViews()
        arguments?.getParcelableArrayList<Category>(CATEGORIES)
            ?.filter { it.name.toLowerCase().contains(filter.toLowerCase()) }?.forEach {
                view?.group?.addView(
                    createChipFromCategory(
                        requireContext(),
                        it,
                        true,
                        true,
                        onCheckedChangeListener = OnCheckedChangeListener { buttonView, isChecked ->
                            listener?.invoke(it)
                            this.dismiss()
                        })
                )
            }
    }

    companion object {
        const val CATEGORIES = "Categories"
        fun newInstance(categories: ArrayList<Category>): NewCategoryDialog = NewCategoryDialog().apply {
            arguments = Bundle().apply {
                putParcelableArrayList(CATEGORIES, categories)
            }
        }
    }
}