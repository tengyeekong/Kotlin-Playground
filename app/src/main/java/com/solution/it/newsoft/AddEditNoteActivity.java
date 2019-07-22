package com.solution.it.newsoft;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.solution.it.newsoft.databinding.ActivityAddNoteBinding;

public class AddEditNoteActivity extends AppCompatActivity {
    public static final String EXTRA_ID =
            "com.solution.it.newsoft.EXTRA_ID";
    public static final String EXTRA_TITLE =
            "com.solution.it.newsoft.EXTRA_TITLE";
    public static final String EXTRA_DESCRIPTION =
            "com.solution.it.newsoft.EXTRA_DESCRIPTION";
    public static final String EXTRA_PRIORITY =
            "com.solution.it.newsoft.EXTRA_PRIORITY";

    private ActivityAddNoteBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_note);

        binding.numberPickerPriority.setMinValue(1);
        binding.numberPickerPriority.setMaxValue(10);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        Intent intent = getIntent();

        if (intent.hasExtra(EXTRA_ID)) {
            setTitle("Edit Login");
            binding.editTextTitle.setText(intent.getStringExtra(EXTRA_TITLE));
            binding.editTextDescription.setText(intent.getStringExtra(EXTRA_DESCRIPTION));
            binding.numberPickerPriority.setValue(intent.getIntExtra(EXTRA_PRIORITY, 1));
        } else {
            setTitle("Add Login");
        }
    }

    private void saveNote() {
        String title = binding.editTextTitle.getText().toString();
        String description = binding.editTextDescription.getText().toString();
        int priority = binding.numberPickerPriority.getValue();

        if (title.trim().isEmpty() || description.trim().isEmpty()) {
            Toast.makeText(this, "Please insert a title and description", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent data = new Intent();
        data.putExtra(EXTRA_TITLE, title);
        data.putExtra(EXTRA_DESCRIPTION, description);
        data.putExtra(EXTRA_PRIORITY, priority);

        int id = getIntent().getIntExtra(EXTRA_ID, -1);
        if (id != -1) {
            data.putExtra(EXTRA_ID, id);
        }

        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_note:
                saveNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
