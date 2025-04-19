package com.example.bookmanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookmanagement.R;
import com.example.bookmanagement.model.Book;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
    private List<Book> bookList;
    private Context context;
    private OnBookItemClickListener listener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public interface OnBookItemClickListener {
        void onBookItemClick(Book book, int position);
    }

    public BookAdapter(Context context, List<Book> bookList, OnBookItemClickListener listener) {
        this.context = context;
        this.bookList = bookList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);
        
        holder.tvBookId.setText(book.getFormattedId());
        holder.tvBookTitle.setText(book.getTitle());
        holder.tvBookAuthor.setText(book.getAuthor());
        holder.tvPublishDate.setText(dateFormat.format(book.getPublishDate()));
        
        holder.cbScience.setChecked(book.isScience());
        holder.cbNovel.setChecked(book.isNovel());
        holder.cbChildren.setChecked(book.isChildren());
        
        // Disable checkbox interaction in the list view
        holder.cbScience.setEnabled(false);
        holder.cbNovel.setEnabled(false);
        holder.cbChildren.setEnabled(false);
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBookItemClick(book, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public void updateData(List<Book> newBooks) {
        this.bookList = newBooks;
        notifyDataSetChanged();
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView tvBookId, tvBookTitle, tvBookAuthor, tvPublishDate;
        CheckBox cbScience, cbNovel, cbChildren;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBookId = itemView.findViewById(R.id.tvBookId);
            tvBookTitle = itemView.findViewById(R.id.tvBookTitle);
            tvBookAuthor = itemView.findViewById(R.id.tvBookAuthor);
            tvPublishDate = itemView.findViewById(R.id.tvPublishDate);
            cbScience = itemView.findViewById(R.id.cbScience);
            cbNovel = itemView.findViewById(R.id.cbNovel);
            cbChildren = itemView.findViewById(R.id.cbChildren);
        }
    }
}
