package com.solution.it.newsoft.ui

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity

import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

import com.google.android.material.snackbar.Snackbar
import com.solution.it.newsoft.R
import com.solution.it.newsoft.databinding.ActivityListingBinding
import com.solution.it.newsoft.databinding.DialogUpdateListBinding
import com.solution.it.newsoft.model.List
import com.solution.it.newsoft.model.NetworkState
import com.solution.it.newsoft.paging.FooterAdapter
import com.solution.it.newsoft.paging.ListingAdapter
import com.solution.it.newsoft.viewmodel.ListingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*

import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.concurrent.schedule

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ListingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListingBinding
    private lateinit var layoutManager: LinearLayoutManager
    private val disposable = CompositeDisposable()
    private lateinit var snackbar: Snackbar

    private val viewModel: ListingViewModel by viewModels()
    @Inject lateinit var adapter: ListingAdapter
    @Inject lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_listing)

        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(false)
        supportActionBar?.title = prefs.getString(ListingViewModel.USERNAME, "")

        layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.setHasFixedSize(true)

        val footerAdapter = FooterAdapter(adapter)
        binding.recyclerView.adapter = adapter.withLoadStateFooter(footerAdapter)

        binding.swipeRefresh.isRefreshing = true

        lifecycleScope.launch {
            viewModel.flow.collectLatest { pagingData ->
                binding.swipeRefresh.isRefreshing = false
                adapter.submitData(pagingData)
            }
        }

        binding.swipeRefresh.setColorSchemeColors(getColor(R.color.colorPrimary))
        binding.swipeRefresh.setOnRefreshListener { refreshList() }

        adapter.setOnItemClickListener(object : ListingAdapter.OnItemClickListener {
            override fun onItemClick(list: List) {
                snackbar = Snackbar.make(binding.coordinatorLayout,
                        StringBuilder("List name: ").append(list.list_name)
                                .append("\n")
                                .append("Distance: ").append(list.distance), Snackbar.LENGTH_SHORT)
                snackbar.show()
            }

            override fun onItemLongClick(list: List, position: Int) {
                showUpdateDialog(list, position)
            }
        })
    }

    private fun refreshList() {
        adapter.refresh()
    }

    private fun showUpdateDialog(list: List, position: Int) {
        val dialog = Dialog(this)
        val dialogBinding = DialogUpdateListBinding.inflate(LayoutInflater.from(this), binding.root as ViewGroup, false)
        dialogBinding.list = list
        dialogBinding.etListName.requestFocus()
        dialogBinding.btnUpdate.setOnClickListener { updateList(dialogBinding, list, position, dialog) }

        dialog.setContentView(dialogBinding.root)
        dialog.show()

        val window = dialog.window
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }

    private fun updateList(dialogBinding: DialogUpdateListBinding, list: List, position: Int, dialog: Dialog) {
        bindProgressButton(dialogBinding.btnUpdate)
        dialogBinding.btnUpdate.attachTextChangeAnimator()
        dialogBinding.btnUpdate.showProgress {
            buttonText = "Updating"
            progressColorRes = R.color.colorPrimary
        }

        val isUpdated = viewModel.updateList(list.id, dialogBinding.etListName.text.toString(),
                dialogBinding.etDistance.text.toString())
        isUpdated.observe(this, Observer { aBoolean ->
            if (aBoolean) {
                adapter.updateList(position, dialogBinding.etListName.text.toString(),
                        dialogBinding.etDistance.text.toString())
            }
            dialogBinding.btnUpdate.hideProgress("Updated")
            Timer().schedule(1_000) {
                dialog.dismiss()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                prefs.edit().clear().apply()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (layoutManager.findFirstVisibleItemPosition() > 5) {
            disposable.add(Completable.fromAction { binding.recyclerView.smoothScrollToPosition(0) }
                    .delay(200, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { layoutManager.scrollToPositionWithOffset(0, 0) })
        } else {
            super.onBackPressed()
            finishAffinity()
        }
    }

    override fun onDestroy() {
        disposable.clear()
        super.onDestroy()
    }
}
