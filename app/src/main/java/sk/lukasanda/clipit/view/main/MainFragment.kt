package sk.lukasanda.clipit.view.main

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.fab
import kotlinx.android.synthetic.main.fragment_main.view.empty
import kotlinx.android.synthetic.main.fragment_main.view.recycler
import kotlinx.android.synthetic.main.fragment_main.view.refresh
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import sk.lukasanda.clipit.R
import sk.lukasanda.clipit.data.db.entity.ClipboardEntry
import sk.lukasanda.clipit.utils.hide
import sk.lukasanda.clipit.utils.show

class MainFragment : Fragment() {
    private val viewModel: MainViewModel by sharedViewModel()

    lateinit var listener: FragmentInteraction

    private val adapter = ClipboardAdapter(mutableListOf(), {
        viewModel.removeClip(it)
    }, {
        listener.onClipboardSelected(it)
    })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_main, container, false)
        v.recycler.apply {
            adapter = this@MainFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
            addOnScrollListener(object: RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if(dy > 0)
                        listener.onScrollDown()
                    else if(dy < 0)
                        listener.onScrollUp()
                }
            })
        }
        v.refresh.setOnRefreshListener {
            viewModel.getAllClipsNew()
        }
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.clips.observe(this, Observer {
            view.refresh.isRefreshing = false
            if (it.isEmpty()) {
                view.empty.show()
            } else {
                view.empty.hide()
            }
            adapter.setData(it)
        })
    }

    override fun onResume() {
        super.onResume()
        Handler().postDelayed({
            viewModel.getAllClipsNew()
        }, 500)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is FragmentInteraction){
            listener = context
        } else throw IllegalStateException("Fragment must implement FragmentInteraction interface")
    }

    interface FragmentInteraction {
        fun onClipboardSelected(clipboardEntry: ClipboardEntry)
        fun onScrollUp()
        fun onScrollDown()
    }
}