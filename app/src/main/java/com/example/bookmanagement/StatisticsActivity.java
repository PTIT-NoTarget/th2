package com.example.bookmanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookmanagement.adapter.BookAdapter;
import com.example.bookmanagement.database.DatabaseHelper;
import com.example.bookmanagement.model.Book;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StatisticsActivity extends AppCompatActivity {
    private EditText etStartYear, etEndYear;
    private CheckBox cbScience, cbNovel, cbChildren;
    private Button btnSearch;
    private TextView tvResultCount;
    private RecyclerView recyclerViewStatistics;
    private BookAdapter bookAdapter;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        databaseHelper = new DatabaseHelper(this);
        
        // Initialize views
        etStartYear = findViewById(R.id.etStartYear);
        etEndYear = findViewById(R.id.etEndYear);
        cbScience = findViewById(R.id.cbScience);
        cbNovel = findViewById(R.id.cbNovel);
        cbChildren = findViewById(R.id.cbChildren);
        btnSearch = findViewById(R.id.btnSearch);
        tvResultCount = findViewById(R.id.tvResultCount);
        recyclerViewStatistics = findViewById(R.id.recyclerViewStatistics);
        
        // Set default years (current year - 10 to current year)
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        etStartYear.setText(String.valueOf(currentYear - 10));
        etEndYear.setText(String.valueOf(currentYear));
        
        // Set up RecyclerView
        recyclerViewStatistics.setLayoutManager(new LinearLayoutManager(this));
        
        // Set up empty adapter initially
        bookAdapter = new BookAdapter(this, new ArrayList<>(), null);
        recyclerViewStatistics.setAdapter(bookAdapter);
        
        // Set up search button
        btnSearch.setOnClickListener(v -> performSearch());

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }
    
    private void performSearch() {
        String startYearStr = etStartYear.getText().toString().trim();
        String endYearStr = etEndYear.getText().toString().trim();
        
        if (startYearStr.isEmpty() || endYearStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập năm bắt đầu và kết thúc", Toast.LENGTH_SHORT).show();
            return;
        }
        
        int startYear, endYear;
        try {
            startYear = Integer.parseInt(startYearStr);
            endYear = Integer.parseInt(endYearStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Năm không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (startYear > endYear) {
            Toast.makeText(this, "Năm bắt đầu phải nhỏ hơn hoặc bằng năm kết thúc", Toast.LENGTH_SHORT).show();
            return;
        }
        
        boolean isScience = cbScience.isChecked();
        boolean isNovel = cbNovel.isChecked();
        boolean isChildren = cbChildren.isChecked();
        
        // Get books by year range and categories
        List<Book> filteredBooks = databaseHelper.getBooksByYearRangeAndCategories(
                startYear, endYear, isScience, isNovel, isChildren);
        
        // Update result count
        tvResultCount.setText("Kết quả: " + filteredBooks.size() + " sách");
        
        // Update adapter
        bookAdapter = new BookAdapter(this, filteredBooks, null);
        recyclerViewStatistics.setAdapter(bookAdapter);
    }
}
