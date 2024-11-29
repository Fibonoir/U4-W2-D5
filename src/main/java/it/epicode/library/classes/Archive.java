package it.epicode.library.classes;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import it.epicode.library.classes.models.Book;
import it.epicode.library.classes.models.Item;
import it.epicode.library.classes.models.Magazine;
import it.epicode.library.exceptions.ItemNotFoundException;
import it.epicode.library.persistence.Constants;
import it.epicode.library.persistence.RuntimeTypeAdapterFactory;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class Archive {
    private Map<String, Item> items;

    public Archive() {
        items = new HashMap<>();
    }

    public void addItem(Item item) {
        if (items.containsKey(item.getIsbn())) {
            throw new IllegalArgumentException("Item with isbn " + item.getIsbn() + " already exists");
        }
        items.put(item.getIsbn(), item);
        saveCatalog();
    }

    public Item searchByISBN(String isbn) throws ItemNotFoundException {
        if (!items.containsKey(isbn)) {
            throw new ItemNotFoundException("Item with isbn " + isbn + " not found");
        }
        return items.get(isbn);
    }

    public void removeItemByISBN(String isbn) throws ItemNotFoundException {
        if (!items.containsKey(isbn)) {
            throw new ItemNotFoundException("Item with isbn " + isbn + " not found");
        }
        items.remove(isbn);
        saveCatalog();
    }

    public List<Item> searchByYear(int year) {
        return items.values().stream()
                .filter(item -> item.getYearOfPublication() == year)
                .collect(Collectors.toList());
    }

    public List<Item> searchByTitle(String title) {
        return items.values().stream()
                .filter(item -> item.getTitle().contains(title))
                .collect(Collectors.toList());
    }

    public List<Book> searchByAuthor(String author) {
        return items.values().stream()
                .filter(item -> item instanceof Book)
                .map(item -> (Book) item)
                .filter(book -> book.getAuthor().equalsIgnoreCase(author))
                .collect(Collectors.toList());
    }

    public void updateItemByISBN(String isbn, Item item) throws ItemNotFoundException {
        if (!items.containsKey(isbn)) {
            throw new ItemNotFoundException("Item with isbn " + isbn + " not found");
        }
        items.put(isbn, item);
        saveCatalog();
    }

    public long getTotalBooks() {
        return items.values().stream()
                .filter(item -> item instanceof Book)
                .count();
    }

    public long getTotalMagazines() {
        return items.values().stream()
                .filter(item -> item instanceof Magazine)
                .count();
    }

    public Optional<Item> getItemWithHighestPages() {
        return items.values().stream()
                .max(Comparator.comparing(Item::getNumberOfPages));
    }

    public double getAveragePages() {
        return items.values().stream()
                .mapToInt(Item::getNumberOfPages)
                .average()
                .orElse(0);
    }

    private static final RuntimeTypeAdapterFactory<Item> itemTypeFactory = RuntimeTypeAdapterFactory
            .of(Item.class, "type")
            .registerSubtype(Book.class, "Book")
            .registerSubtype(Magazine.class, "Magazine");

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapterFactory(itemTypeFactory)
            .setPrettyPrinting()
            .create();

    public void saveToFile(String filename) throws IOException {
        try (Writer writer = new FileWriter(filename)) {
            gson.toJson(items.values(), writer);
        }
    }

    public void loadFromFile(String filename) throws IOException {
        try (Reader reader = new FileReader(filename)) {
            Type collectionType = new TypeToken<Collection<Item>>() {}.getType();
            Collection<Item> loadedItems = gson.fromJson(reader, collectionType);
            items = loadedItems.stream().collect(Collectors.toMap(Item::getIsbn, item -> item));
        }
    }

    private void saveCatalog() {
        try {
            saveToFile(Constants.CATALOG_FILENAME);
        } catch (IOException e) {
            System.out.println("Error saving catalog: " + e.getMessage());
        }
    }
}
