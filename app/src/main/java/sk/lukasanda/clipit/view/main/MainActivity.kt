package sk.lukasanda.clipit.view.main

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.bottom_bar
import kotlinx.android.synthetic.main.activity_main.fab
import org.koin.androidx.viewmodel.ext.android.viewModel
import sk.lukasanda.clipit.R
import sk.lukasanda.clipit.data.db.entity.ClipboardEntry
import sk.lukasanda.clipit.service.ClipboardService
import sk.lukasanda.clipit.utils.inTransaction
import sk.lukasanda.clipit.utils.isMyServiceRunning
import sk.lukasanda.clipit.view.dialogs.AllCategoriesDialog
import sk.lukasanda.clipit.view.dialogs.NavigationDialog
import sk.lukasanda.clipit.view.dialogs.NewCategoryDialog
import sk.lukasanda.clipit.view.main.MainFragment.FragmentInteraction

class MainActivity : AppCompatActivity(), FragmentInteraction {
    override fun onScrollUp() {
//        fab.show()
    }

    override fun onScrollDown() {
//        fab.hide()
    }

    override fun onClipboardSelected(clipboardEntry: ClipboardEntry) {
        detailFragment = DetailFragment.newInstance(clipboardEntry)
        supportFragmentManager.inTransaction(R.id.container, detailFragment!!, true)
        changeFabIconAndShow(R.drawable.ic_add)
    }

    private val mainViewModel by viewModel<MainViewModel>()

    private var manager: ClipboardManager? = null

    private var serviceIntent: Intent? = null

    private var detailFragment: DetailFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(bottom_bar)


        manager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        serviceIntent = Intent(this, ClipboardService::class.java)
        serviceIntent?.action = ClipboardService.ACTION_START_FOREGROUND_SERVICE

        bottom_bar.setNavigationOnClickListener {
            showNavigation()
        }

        fab.setOnClickListener {
            if (detailFragment != null) {
                showAddCategory()
            } else {
                showFilter()
            }
        }

        supportFragmentManager.inTransaction(R.id.container, MainFragment())
    }

    override fun onResume() {
        super.onResume()

        if (!isMyServiceRunning(this, ClipboardService::class.java)) {
            startService(serviceIntent)
        }
    }

    private fun showFilter() {
        if (supportFragmentManager.findFragmentByTag("categories") == null) {
            val popup = AllCategoriesDialog.newInstance(ArrayList(mainViewModel.categories.value ?: mutableListOf()))
            popup.listener = {
                mainViewModel.updateAllCategories(it)
            }
            popup.show(supportFragmentManager, "categories")
        }
    }

    private fun showAddCategory() {
        if (supportFragmentManager.findFragmentByTag("newCategory") == null) {
            val popup = NewCategoryDialog.newInstance(ArrayList(mainViewModel.categories.value ?: mutableListOf()))
            popup.listener = {
                detailFragment?.addCategory(it)
                mainViewModel.addCategory(it)
            }
            popup.show(supportFragmentManager, "newCategory")
        }
    }

    private fun showNavigation(){
        if (supportFragmentManager.findFragmentByTag("navigation") == null) {
            val popup = NavigationDialog()
            popup.show(supportFragmentManager, "navigation")
        }
    }

    private fun changeFabIconAndShow(@DrawableRes icon: Int){
        if(fab.isOrWillBeShown){
            fab.setImageDrawable(ContextCompat.getDrawable(this@MainActivity, icon))
        } else {
            fab.show(object : FloatingActionButton.OnVisibilityChangedListener() {
                override fun onShown(fab: FloatingActionButton?) {
                    super.onShown(fab)
                    fab?.setImageDrawable(ContextCompat.getDrawable(this@MainActivity, icon))
                }
            })
        }
    }

    override fun onBackPressed() {
        if (detailFragment != null) {
            super.onBackPressed()
            detailFragment = null
            changeFabIconAndShow(R.drawable.ic_filter)
        } else {
            super.onBackPressed()
        }
    }
}
