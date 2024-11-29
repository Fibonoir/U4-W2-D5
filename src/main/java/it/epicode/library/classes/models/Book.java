package it.epicode.library.classes.models;

public class Book extends Item {
    private String author;
    private String genre;

    public Book(String isbn, String title, int yearOfPublication, int numberOfPages, String author, String genre) {
        super(isbn, title, yearOfPublication, numberOfPages);
        this.author = author;
        this.genre = genre;
    }


    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
}