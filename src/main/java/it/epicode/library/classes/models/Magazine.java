package it.epicode.library.classes.models;

import it.epicode.library.enums.Periodicity;

public class Magazine extends Item {
    private Periodicity periodicity;

    public Magazine(String isbn, String title, int yearOfPublication, int numberOfPages, Periodicity periodicity) {
        super(isbn, title, yearOfPublication, numberOfPages);
        this.periodicity = periodicity;
    }

    // Getters and Setters
    public Periodicity getPeriodicity() { return periodicity; }
    public void setPeriodicity(Periodicity periodicity) { this.periodicity = periodicity; }
}

