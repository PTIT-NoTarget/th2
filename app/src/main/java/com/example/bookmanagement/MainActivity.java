package com.example.bookmanagement;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookmanagement.adapter.BookAdapter;
import com.example.bookmanagement.database.DatabaseHelper;
import com.example.bookmanagement.model.Book;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements BookAdapter.OnBookItemClickListener {
    private RecyclerView recyclerView;
    private BookAdapter bookAdapter;
    private List<Book> bookList;
    private DatabaseHelper databaseHelper;
    private Button btnAdd, btnStatistics;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);
        
        recyclerView = findViewById(R.id.recyclerView);
        btnAdd = findViewById(R.id.btnAdd);
        btnStatistics = findViewById(R.id.btnStatistics);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        loadBooks();
        
        btnAdd.setOnClickListener(v -> showBookDialog(null));
        
        btnStatistics.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, StatisticsActivity.class);
            startActivity(intent);
        });
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        loadBooks();
    }
    
    private void loadBooks() {
        bookList = databaseHelper.getAllBooks();
        bookAdapter = new BookAdapter(this, bookList, this);
        recyclerView.setAdapter(bookAdapter);
    }

    @Override
    public void onBookItemClick(Book book, int position) {
        showBookDialog(book);
    }
    
    private void showBookDialog(Book book) {
        boolean isEdit = (book != null);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(isEdit ? "Sửa thông tin sách" : "Thêm sách mới");
        
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_book, null);
        builder.setView(view);
        
        // Initialize views
        View layoutBookId = view.findViewById(R.id.layoutBookId);
        TextView tvDialogBookId = view.findViewById(R.id.tvDialogBookId);
        TextInputEditText etBookTitle = view.findViewById(R.id.etBookTitle);
        TextInputEditText etBookAuthor = view.findViewById(R.id.etBookAuthor);
        TextView tvPublishDate = view.findViewById(R.id.tvPublishDate);
        CheckBox cbScience = view.findViewById(R.id.cbScience);
        CheckBox cbNovel = view.findViewById(R.id.cbNovel);
        CheckBox cbChildren = view.findViewById(R.id.cbChildren);
        
        // Set up date picker
        final Calendar calendar = Calendar.getInstance();
        final Date[] selectedDate = {calendar.getTime()};
        
        if (isEdit) {
            // Show book ID for edit mode
            layoutBookId.setVisibility(View.VISIBLE);
            tvDialogBookId.setText(book.getFormattedId());
            
            // Fill in book details
            etBookTitle.setText(book.getTitle());
            etBookAuthor.setText(book.getAuthor());
            selectedDate[0] = book.getPublishDate();
            tvPublishDate.setText(dateFormat.format(selectedDate[0]));
            cbScience.setChecked(book.isScience());
            cbNovel.setChecked(book.isNovel());
            cbChildren.setChecked(book.isChildren());
        } else {
            // Hide book ID for add mode
            layoutBookId.setVisibility(View.GONE);
            
            // Set default date to today
            tvPublishDate.setText(dateFormat.format(selectedDate[0]));
        }
        
        // Set up date picker dialog
        tvPublishDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            if (selectedDate[0] != null) {
                cal.setTime(selectedDate[0]);
            }
            
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    MainActivity.this,
                    (view1, year, month, dayOfMonth) -> {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, month, dayOfMonth);
                        selectedDate[0] = newDate.getTime();
                        tvPublishDate.setText(dateFormat.format(selectedDate[0]));
                    },
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
        
        // Set up dialog buttons
        builder.setPositiveButton(isEdit ? "Cập nhật" : "Thêm", null);
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        
        if (isEdit) {
            builder.setNeutralButton("Xóa", (dialog, which) -> {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Xác nhận xóa")
                        .setMessage("Bạn có chắc chắn muốn xóa sách này?")
                        .setPositiveButton("Xóa", (dialogConfirm, whichConfirm) -> {
                            databaseHelper.deleteBook(book.getId());
                            loadBooks();
                            Toast.makeText(MainActivity.this, "Đã xóa sách", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            });
        }
        
        AlertDialog dialog = builder.create();
        dialog.show();
        
        // Override the positive button click to validate input before dismissing
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String title = etBookTitle.getText().toString().trim();
            String author = etBookAuthor.getText().toString().trim();
            boolean hasCategory = cbScience.isChecked() || cbNovel.isChecked() || cbChildren.isChecked();
            
            // Validate input
            if (title.isEmpty()) {
                etBookTitle.setError("Vui lòng nhập tên sách");
                return;
            }
            
            if (author.isEmpty()) {
                etBookAuthor.setError("Vui lòng nhập tên tác giả");
                return;
            }
            
            if (!hasCategory) {
                Toast.makeText(MainActivity.this, "Vui lòng chọn ít nhất một thể loại", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Create or update book
            if (isEdit) {
                // Update existing book
                book.setTitle(title);
                book.setAuthor(author);
                book.setPublishDate(selectedDate[0]);
                book.setScience(cbScience.isChecked());
                book.setNovel(cbNovel.isChecked());
                book.setChildren(cbChildren.isChecked());
                
                databaseHelper.updateBook(book);
                Toast.makeText(MainActivity.this, "Đã cập nhật sách", Toast.LENGTH_SHORT).show();
            } else {
                // Add new book
                Book newBook = new Book();
                newBook.setId(databaseHelper.getNextId());
                newBook.setTitle(title);
                newBook.setAuthor(author);
                newBook.setPublishDate(selectedDate[0]);
                newBook.setScience(cbScience.isChecked());
                newBook.setNovel(cbNovel.isChecked());
                newBook.setChildren(cbChildren.isChecked());
                
                databaseHelper.addBook(newBook);
                Toast.makeText(MainActivity.this, "Đã thêm sách mới", Toast.LENGTH_SHORT).show();
            }
            
            loadBooks();
            dialog.dismiss();
        });
    }
}
