package it.epicode;


import com.google.gson.JsonParseException;
import it.epicode.library.classes.models.Item;
import it.epicode.library.classes.Archive;
import it.epicode.library.classes.models.Book;
import it.epicode.library.classes.models.Magazine;
import it.epicode.library.enums.Periodicity;
import it.epicode.library.exceptions.ItemNotFoundException;
import it.epicode.library.persistence.Constants;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Application {
    private static Archive archivio = new Archive();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        loadCatalogAtStartup();

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    addItemMenu();
                    break;
                case "2":
                    searchByISBNMenu();
                    break;
                case "3":
                    removeItemByISBNMenu();
                    break;
                case "4":
                    searchByYearMenu();
                    break;
                case "5":
                    searchByAuthorMenu();
                    break;
                case "6":
                    updateItemByISBNMenu();
                    break;
                case "7":
                    showCatalogStatistics();
                    break;
                case "0":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option, please select again.");
            }
        }
        scanner.close();
    }

    private static void loadCatalogAtStartup() {
        try {
            archivio.loadFromFile(Constants.CATALOG_FILENAME);
            System.out.println("Catalog loaded successfully from " + Constants.CATALOG_FILENAME);
        } catch (IOException e) {
            System.out.println("No existing catalog found. Starting with an empty catalog.");
        } catch (JsonParseException e) {
            System.out.println("Error parsing catalog file: " + e.getMessage());
        }
    }

    private static void printMenu() {
        System.out.println("\n=== Library Catalog Management System ===");
        System.out.println("1. Add an Item");
        System.out.println("2. Search by ISBN");
        System.out.println("3. Remove an Item by ISBN");
        System.out.println("4. Search by Year of Publication");
        System.out.println("5. Search by Author");
        System.out.println("6. Update an Existing Item by ISBN");
        System.out.println("7. Catalog Statistics");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
    }

    private static void addItemMenu() {
        System.out.print("Is the item a Book or Magazine? (B/M): ");
        String type = scanner.nextLine().trim().toUpperCase();
        System.out.print("Enter ISBN Code: ");
        String isbn = scanner.nextLine();
        System.out.print("Enter Title: ");
        String title = scanner.nextLine();
        System.out.print("Enter Year of Publication: ");
        int year = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter Number of Pages: ");
        int pages = Integer.parseInt(scanner.nextLine());

        try {
            if (type.equals("B")) {
                System.out.print("Enter Author: ");
                String author = scanner.nextLine();
                System.out.print("Enter Genre: ");
                String genre = scanner.nextLine();

                Book book = new Book(isbn, title, year, pages, author, genre);
                archivio.addItem(book);
                System.out.println("Book added successfully.");
            } else if (type.equals("M")) {
                System.out.print("Enter Periodicity (WEEKLY, MONTHLY, SEMI_ANNUAL): ");
                String periodicityInput = scanner.nextLine().trim().toUpperCase();
                Periodicity periodicity = Periodicity.valueOf(periodicityInput);

                Magazine magazine = new Magazine(isbn, title, year, pages, periodicity);
                archivio.addItem(magazine);
                System.out.println("Magazine added successfully.");
            } else {
                System.out.println("Invalid item type.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error adding item: " + e.getMessage());
        }
    }

    private static void searchByISBNMenu() {
        System.out.print("Enter ISBN to search: ");
        String isbn = scanner.nextLine();
        try {
            Item item = archivio.searchByISBN(isbn);
            printItemDetails(item);
        } catch (ItemNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void removeItemByISBNMenu() {
        System.out.print("Enter ISBN to remove: ");
        String isbn = scanner.nextLine();
        try {
            archivio.removeItemByISBN(isbn);
            System.out.println("Item removed successfully.");
        } catch (ItemNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void searchByYearMenu() {
        System.out.print("Enter Year of Publication to search: ");
        int year = Integer.parseInt(scanner.nextLine());
        List<Item> items = archivio.searchByYear(year);
        if (items.isEmpty()) {
            System.out.println("No items found for year " + year);
        } else {
            items.forEach(it.epicode.Application::printItemDetails);
        }
    }

    private static void searchByAuthorMenu() {
        System.out.print("Enter Author to search: ");
        String author = scanner.nextLine();
        List<Book> books = archivio.searchByAuthor(author);
        if (books.isEmpty()) {
            System.out.println("No books found for author " + author);
        } else {
            books.forEach(it.epicode.Application::printItemDetails);
        }
    }

    private static void updateItemByISBNMenu() {
        System.out.print("Enter ISBN of the item to update: ");
        String isbn = scanner.nextLine();
        try {
            Item existingItem = archivio.searchByISBN(isbn);
            if (existingItem instanceof Book) {
                System.out.println("Updating a Book:");
                System.out.print("Enter new Title: ");
                String title = scanner.nextLine();
                System.out.print("Enter new Year of Publication: ");
                int year = Integer.parseInt(scanner.nextLine());
                System.out.print("Enter new Number of Pages: ");
                int pages = Integer.parseInt(scanner.nextLine());
                System.out.print("Enter new Author: ");
                String author = scanner.nextLine();
                System.out.print("Enter new Genre: ");
                String genre = scanner.nextLine();

                Book updatedBook = new Book(isbn, title, year, pages, author, genre);
                archivio.updateItemByISBN(isbn, updatedBook);
                System.out.println("Book updated successfully.");
            } else if (existingItem instanceof Magazine) {
                System.out.println("Updating a Magazine:");
                System.out.print("Enter new Title: ");
                String title = scanner.nextLine();
                System.out.print("Enter new Year of Publication: ");
                int year = Integer.parseInt(scanner.nextLine());
                System.out.print("Enter new Number of Pages: ");
                int pages = Integer.parseInt(scanner.nextLine());
                System.out.print("Enter new Periodicity (WEEKLY, MONTHLY, SEMI_ANNUAL): ");
                String periodicityInput = scanner.nextLine().trim().toUpperCase();
                Periodicity periodicity = Periodicity.valueOf(periodicityInput);

                Magazine updatedMagazine = new Magazine(isbn, title, year, pages, periodicity);
                archivio.updateItemByISBN(isbn, updatedMagazine);
                System.out.println("Magazine updated successfully.");
            }
        } catch (ItemNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Error updating item: " + e.getMessage());
        }
    }

    private static void showCatalogStatistics() {
        long totalBooks = archivio.getTotalBooks();
        long totalMagazines = archivio.getTotalMagazines();
        Optional<Item> itemWithMaxPages = archivio.getItemWithHighestPages();
        double averagePages = archivio.getAveragePages();

        System.out.println("\n=== Catalog Statistics ===");
        System.out.println("Total number of books: " + totalBooks);
        System.out.println("Total number of magazines: " + totalMagazines);
        itemWithMaxPages.ifPresent(item -> {
            System.out.println("Item with the highest number of pages:");
            printItemDetails(item);
        });
        System.out.println("Average number of pages for all items: " + averagePages);
    }

    private static void printItemDetails(Item item) {
        System.out.println("\n--- Item Details ---");
        System.out.println("ISBN: " + item.getIsbn());
        System.out.println("Title: " + item.getTitle());
        System.out.println("Year of Publication: " + item.getYearOfPublication());
        System.out.println("Number of Pages: " + item.getNumberOfPages());
        if (item instanceof Book book) {
            System.out.println("Author: " + book.getAuthor());
            System.out.println("Genre: " + book.getGenre());
        } else if (item instanceof Magazine magazine) {
            System.out.println("Periodicity: " + magazine.getPeriodicity());
        }
        System.out.println("------------------------");
    }
}
