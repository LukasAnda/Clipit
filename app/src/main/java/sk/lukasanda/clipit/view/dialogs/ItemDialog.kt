package sk.lukasanda.clipit.view.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import android.view.WindowManager
import android.view.Gravity
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager.LayoutParams
import androidx.annotation.NonNull
import sk.lukasanda.clipit.R

class ItemDialog : DialogFragment(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.TransparentDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.dialog_item, container, false)
        return v
    }

    @NonNull
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawable(
            ColorDrawable(
                android.graphics.Color
                    .TRANSPARENT
            )
        )
        dialog.window?.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        return dialog
    }
}