package sk.lukasanda.clipit.view.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_detail.view.clipboard
import kotlinx.android.synthetic.main.fragment_detail.view.group
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import sk.lukasanda.clipit.R
import sk.lukasanda.clipit.data.db.entity.Category
import sk.lukasanda.clipit.data.db.entity.ClipboardEntry
import sk.lukasanda.clipit.utils.Category.GENERAL
import sk.lukasanda.clipit.utils.createChipFromCategory
import sk.lukasanda.clipit.utils.getColor

class DetailFragment : Fragment() {
    private var clipboardEntry: ClipboardEntry? = null
    private val viewModel: MainViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        val v = inflater.inflate(R.layout.fragment_detail, container, false)
        arguments?.let {
            it.getParcelable<ClipboardEntry>(ENTRY)?.let {
                clipboardEntry = it
                v.clipboard.setText(it.clipboard)
                drawChips(v)
            }
        }
        return v
    }

    fun addCategory(category: Category) {
        if (clipboardEntry?.categories?.contains(category) == false) {
            clipboardEntry?.categories?.add(category)
            val item = clipboardEntry?.categories?.find { it?.name == "Unfiled" }
            clipboardEntry?.categories?.remove(item)
        }
        drawChips(view)
    }

    private fun drawChips(view: View?) {
        view?.group?.removeAllViews()
        clipboardEntry?.categories?.filterNotNull()?.forEach {
            if (it.name != "Unfiled") {
                view?.group?.addView(createChipFromCategory(view.context, it, false, true, onCloseListener = {
                    clipboardEntry?.categories?.remove(it)
                    if (clipboardEntry?.categories?.isEmpty() == true) {
                        clipboardEntry?.categories?.add(Category("Unfiled", getColor(GENERAL)))
                    }
                    drawChips(view)
                }

                ))
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.save) {
            clipboardEntry?.clipboard = view?.clipboard?.text.toString()

            val cleared = clipboardEntry?.categories?.filterNotNull()?.distinctBy { it.name }
            clipboardEntry?.categories?.clear()
            cleared?.let { clipboardEntry?.categories?.addAll(it) }
            clipboardEntry?.let { viewModel.updateClipboard(it) }
            requireActivity().onBackPressed()
            return true
        }
        return false
    }

    companion object {
        private val ENTRY = "ENTRY"
        fun newInstance(clipboardEntry: ClipboardEntry): DetailFragment {
            return DetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ENTRY, clipboardEntry)
                }
            }
        }
    }
}