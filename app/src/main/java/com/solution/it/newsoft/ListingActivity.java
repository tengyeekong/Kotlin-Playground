package com.solution.it.newsoft;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.solution.it.newsoft.databinding.ActivityListingBinding;
import com.solution.it.newsoft.databinding.DialogUpdateListBinding;
import com.solution.it.newsoft.model.List;

public class ListingActivity extends AppCompatActivity {
    private ActivityListingBinding binding;
    private com.solution.it.newsoft.ViewModel viewModel;
    private Toast toast;
    private ListingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_listing);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setHasFixedSize(true);

        adapter = new ListingAdapter();
        binding.recyclerView.setAdapter(adapter);

        viewModel = ViewModelProviders.of(this).get(ViewModel.class);
        binding.swipeRefresh.setRefreshing(true);
        viewModel.getListLiveData().observe(this, lists -> {
            adapter.submitList(lists);
        });
        viewModel.getNetworkState().observe(this, networkState -> {
            adapter.setNetworkState(networkState);
            if (binding.swipeRefresh.isRefreshing())
                binding.swipeRefresh.setRefreshing(false);
        });

        binding.swipeRefresh.setColorSchemeColors(getColor(R.color.colorPrimary));
        binding.swipeRefresh.setOnRefreshListener(() -> refreshList());

        adapter.setOnItemClickListener(new ListingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(List list) {
                if (toast != null) toast.cancel();
                toast = Toast.makeText(ListingActivity.this,
                        new StringBuilder("List name: ").append(list.getList_name())
                                .append("\n")
                                .append("Distance: ").append(list.getDistance()), Toast.LENGTH_SHORT);
                toast.show();
            }

            @Override
            public void onItemLongClick(List list, int position) {
                showUpdateDialog(list, position);
            }
        });
    }

    private void refreshList() {
        binding.swipeRefresh.setRefreshing(true);
        viewModel.reload();
    }

    private void showUpdateDialog(List list, int position) {
        Dialog dialog = new Dialog(ListingActivity.this);
        DialogUpdateListBinding dialogBinding = DialogUpdateListBinding.inflate(LayoutInflater.from(ListingActivity.this), (ViewGroup) binding.getRoot(), false);
        dialogBinding.setList(list);

        dialogBinding.btnUpdate.setOnClickListener(view -> {
            updateList(dialogBinding, list, position, dialog);
        });
        dialog.setContentView(dialogBinding.getRoot());
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void updateList(DialogUpdateListBinding dialogBinding, List list, int position, Dialog dialog) {
        ProgressDialog progress = ProgressDialog.show(ListingActivity.this, "", "Updating...", true);
        progress.show();
        LiveData<Boolean> isUpdated = viewModel.updateList(list.getId(), dialogBinding.etListName.getText().toString(),
                dialogBinding.etDistance.getText().toString());
        isUpdated.observe(ListingActivity.this, aBoolean -> {
            if (aBoolean) {
                adapter.updateList(position, dialogBinding.etListName.getText().toString(),
                        dialogBinding.etDistance.getText().toString());
            }
            dialog.dismiss();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                viewModel.getPrefs().edit().clear().apply();
                Intent intent = new Intent(ListingActivity.this, LoginActivity.class);
                startActivity(intent);
                Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}
