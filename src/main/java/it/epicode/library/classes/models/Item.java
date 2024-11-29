package it.epicode.library.classes.models;

public abstract class Item {
    private String isbn;
    private String title;
    private int yearOfPublication;
    private int numberOfPages;

    public Item(String isbn, String title, int yearOfPublication, int numberOfPages) {
        this.isbn = isbn;
        this.title = title;
        this.yearOfPublication = yearOfPublication;
        this.numberOfPages = numberOfPages;
    }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public int getYearOfPublication() { return yearOfPublication; }
    public void setYearOfPublication(int yearOfPublication) { this.yearOfPublication = yearOfPublication; }
    public int getNumberOfPages() { return numberOfPages; }
    public void setNumberOfPages(int numberOfPages) { this.numberOfPages = numberOfPages; }
}
