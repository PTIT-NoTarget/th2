package com.example.bookmanagement.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.bookmanagement.model.Book;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "book_management.db";
    private static final int DATABASE_VERSION = 1;

    // Table name
    private static final String TABLE_BOOKS = "books";

    // Column names
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_AUTHOR = "author";
    private static final String COLUMN_PUBLISH_DATE = "publish_date";
    private static final String COLUMN_IS_SCIENCE = "is_science";
    private static final String COLUMN_IS_NOVEL = "is_novel";
    private static final String COLUMN_IS_CHILDREN = "is_children";

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_BOOKS_TABLE = "CREATE TABLE " + TABLE_BOOKS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TITLE + " TEXT,"
                + COLUMN_AUTHOR + " TEXT,"
                + COLUMN_PUBLISH_DATE + " TEXT,"
                + COLUMN_IS_SCIENCE + " INTEGER,"
                + COLUMN_IS_NOVEL + " INTEGER,"
                + COLUMN_IS_CHILDREN + " INTEGER"
                + ")";
        db.execSQL(CREATE_BOOKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKS);
        onCreate(db);
    }

    // Add a new book
    public long addBook(Book book) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_TITLE, book.getTitle());
        values.put(COLUMN_AUTHOR, book.getAuthor());
        values.put(COLUMN_PUBLISH_DATE, dateFormat.format(book.getPublishDate()));
        values.put(COLUMN_IS_SCIENCE, book.isScience() ? 1 : 0);
        values.put(COLUMN_IS_NOVEL, book.isNovel() ? 1 : 0);
        values.put(COLUMN_IS_CHILDREN, book.isChildren() ? 1 : 0);

        long id = db.insert(TABLE_BOOKS, null, values);
        db.close();
        return id;
    }

    // Get a single book
    public Book getBook(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_BOOKS, null, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);

        Book book = null;
        if (cursor != null && cursor.moveToFirst()) {
            book = cursorToBook(cursor);
            cursor.close();
        }

        db.close();
        return book;
    }

    // Get all books
    public List<Book> getAllBooks() {
        List<Book> bookList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_BOOKS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Book book = cursorToBook(cursor);
                bookList.add(book);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return bookList;
    }

    // Update a book
    public int updateBook(Book book) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_TITLE, book.getTitle());
        values.put(COLUMN_AUTHOR, book.getAuthor());
        values.put(COLUMN_PUBLISH_DATE, dateFormat.format(book.getPublishDate()));
        values.put(COLUMN_IS_SCIENCE, book.isScience() ? 1 : 0);
        values.put(COLUMN_IS_NOVEL, book.isNovel() ? 1 : 0);
        values.put(COLUMN_IS_CHILDREN, book.isChildren() ? 1 : 0);

        int result = db.update(TABLE_BOOKS, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(book.getId())});
        db.close();
        return result;
    }

    // Delete a book
    public void deleteBook(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BOOKS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    // Get books by year range and categories
    public List<Book> getBooksByYearRangeAndCategories(int startYear, int endYear, 
                                                      boolean isScience, boolean isNovel, boolean isChildren) {
        List<Book> bookList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String startDate = startYear + "-01-01";
        String endDate = endYear + "-12-31";
        
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT * FROM ").append(TABLE_BOOKS)
                .append(" WHERE ").append(COLUMN_PUBLISH_DATE)
                .append(" BETWEEN ? AND ?");
        
        List<String> args = new ArrayList<>();
        args.add(startDate);
        args.add(endDate);
        
        // Add category filters if any is selected
        if (isScience || isNovel || isChildren) {
            queryBuilder.append(" AND (");
            List<String> categoryConditions = new ArrayList<>();
            
            if (isScience) {
                categoryConditions.add(COLUMN_IS_SCIENCE + " = 1");
            }
            if (isNovel) {
                categoryConditions.add(COLUMN_IS_NOVEL + " = 1");
            }
            if (isChildren) {
                categoryConditions.add(COLUMN_IS_CHILDREN + " = 1");
            }
            
            queryBuilder.append(String.join(" OR ", categoryConditions));
            queryBuilder.append(")");
        }
        
        Cursor cursor = db.rawQuery(queryBuilder.toString(), args.toArray(new String[0]));
        
        if (cursor.moveToFirst()) {
            do {
                Book book = cursorToBook(cursor);
                bookList.add(book);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return bookList;
    }

    // Get the next available ID
    public int getNextId() {
        SQLiteDatabase db = this.getReadableDatabase();
        int nextId = 1; // Default starting ID
        
        Cursor cursor = db.rawQuery("SELECT MAX(" + COLUMN_ID + ") FROM " + TABLE_BOOKS, null);
        if (cursor.moveToFirst()) {
            nextId = cursor.getInt(0) + 1;
        }
        
        cursor.close();
        db.close();
        return nextId;
    }

    // Helper method to convert cursor to Book object
    private Book cursorToBook(Cursor cursor) {
        Book book = new Book();
        book.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
        book.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
        book.setAuthor(cursor.getString(cursor.getColumnIndex(COLUMN_AUTHOR)));
        
        String dateStr = cursor.getString(cursor.getColumnIndex(COLUMN_PUBLISH_DATE));
        try {
            book.setPublishDate(dateFormat.parse(dateStr));
        } catch (ParseException e) {
            book.setPublishDate(new Date()); // Default to current date if parsing fails
        }
        
        book.setScience(cursor.getInt(cursor.getColumnIndex(COLUMN_IS_SCIENCE)) == 1);
        book.setNovel(cursor.getInt(cursor.getColumnIndex(COLUMN_IS_NOVEL)) == 1);
        book.setChildren(cursor.getInt(cursor.getColumnIndex(COLUMN_IS_CHILDREN)) == 1);
        
        return book;
    }
}
