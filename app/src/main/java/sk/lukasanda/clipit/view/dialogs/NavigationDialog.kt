package sk.lukasanda.clipit.view.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_categories.view.group
import sk.lukasanda.clipit.R
import sk.lukasanda.clipit.data.db.entity.Category
import sk.lukasanda.clipit.utils.createChipFromCategory

class NavigationDialog : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val v = inflater.inflate(R.layout.dialog_navigation, container, false)
        return v
    }


}