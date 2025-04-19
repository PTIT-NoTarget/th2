package com.example.bookmanagement.model;

import java.io.Serializable;
import java.util.Date;

public class Book implements Serializable {
    private int id;
    private String title;
    private String author;
    private Date publishDate;
    private boolean isScience;
    private boolean isNovel;
    private boolean isChildren;

    public Book() {
    }

    public Book(int id, String title, String author, Date publishDate, boolean isScience, boolean isNovel, boolean isChildren) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.publishDate = publishDate;
        this.isScience = isScience;
        this.isNovel = isNovel;
        this.isChildren = isChildren;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public boolean isScience() {
        return isScience;
    }

    public void setScience(boolean science) {
        isScience = science;
    }

    public boolean isNovel() {
        return isNovel;
    }

    public void setNovel(boolean novel) {
        isNovel = novel;
    }

    public boolean isChildren() {
        return isChildren;
    }

    public void setChildren(boolean children) {
        isChildren = children;
    }

    public String getFormattedId() {
        return String.format("%03d", id);
    }
    
    public String getCategoriesString() {
        StringBuilder categories = new StringBuilder();
        if (isScience) {
            categories.append("Khoa học, ");
        }
        if (isNovel) {
            categories.append("Tiểu thuyết, ");
        }
        if (isChildren) {
            categories.append("Thiếu nhi, ");
        }
        
        String result = categories.toString();
        if (result.endsWith(", ")) {
            result = result.substring(0, result.length() - 2);
        }
        return result;
    }
    
    public boolean hasAtLeastOneCategory() {
        return isScience || isNovel || isChildren;
    }
}
