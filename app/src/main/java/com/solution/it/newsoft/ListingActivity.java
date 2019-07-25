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
        SharedPreferences prefs = viewModel.getPrefs();
        refreshList(prefs);
        viewModel.getNetworkState().observe(this, networkState -> adapter.setNetworkState(networkState));

        binding.swipeRefresh.setColorSchemeColors(getColor(R.color.colorPrimary));
        binding.swipeRefresh.setOnRefreshListener(() -> refreshList(prefs));

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
                showUpdateDialog(list, position, prefs);
            }
        });
    }

    private void refreshList(SharedPreferences prefs) {
        String username = prefs.getString(ViewModel.USERNAME, "");
        String password = prefs.getString(ViewModel.PASSWORD, "");
        binding.swipeRefresh.setRefreshing(true);
        viewModel.getListLiveData().observe(this, lists -> {
            if (lists == null) {
                //can't use retrofit interceptor in this case to get new token
                //since the token is not passing through header
                viewModel.login(username, password).observe(this, login -> {
                    if (login != null && login.getStatus().getCode().equals("200")) {
                        viewModel.getListLiveData().observe(this, lists1 -> {
                            adapter.submitList(lists1);
                            binding.swipeRefresh.setRefreshing(false);
                        });
                    }
                });
            } else {
                adapter.submitList(lists);
                binding.swipeRefresh.setRefreshing(false);
            }
        });
    }

    private void showUpdateDialog(List list, int position, SharedPreferences prefs) {
        Dialog dialog = new Dialog(ListingActivity.this);
        DialogUpdateListBinding dialogBinding = DialogUpdateListBinding.inflate(LayoutInflater.from(ListingActivity.this), (ViewGroup) binding.getRoot(), false);
        dialogBinding.setList(list);

        dialogBinding.btnUpdate.setOnClickListener(view -> {
            updateList(dialogBinding, list, position, prefs, dialog);
        });
        dialog.setContentView(dialogBinding.getRoot());
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void updateList(DialogUpdateListBinding dialogBinding, List list, int position,
                            SharedPreferences prefs, Dialog dialog) {
        String username = prefs.getString(ViewModel.USERNAME, "");
        String password = prefs.getString(ViewModel.PASSWORD, "");
        ProgressDialog progress = ProgressDialog.show(ListingActivity.this, "", "Updating...", true);
        progress.show();
        LiveData<String> statusCode = viewModel.updateList(list.getId(), dialogBinding.etListName.getText().toString(),
                dialogBinding.etDistance.getText().toString());
        statusCode.observe(ListingActivity.this, s -> {
            if (s.equals("200")) {
                adapter.updateList(position, dialogBinding.etListName.getText().toString(),
                        dialogBinding.etDistance.getText().toString());
                progress.dismiss();
            }
            else if (s.equals("400")) {
                viewModel.login(username, password).observe(ListingActivity.this, login -> {
                    if (login != null && login.getStatus().getCode().equals("200")) {
                        LiveData<String> statusCode1 = viewModel.updateList(list.getId(), dialogBinding.etListName.getText().toString(),
                                dialogBinding.etDistance.getText().toString());
                        statusCode1.observe(ListingActivity.this, s1 -> {
                            if (s1.equals("200")) {
                                adapter.updateList(position, dialogBinding.etListName.getText().toString(),
                                        dialogBinding.etDistance.getText().toString());
                            }
                            progress.dismiss();
                        });
                    } else progress.dismiss();
                });
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
