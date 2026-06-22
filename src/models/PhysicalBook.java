package models;

public class PhysicalBook extends LibraryItem {
    public PhysicalBook(int id, String title, String author, String isbn,
                        String category, int totalCopies, int availableCopies) {
        super(id, title, author, isbn, category, totalCopies, availableCopies);
    }

    @Override
    public String getType() { return "Physical Book"; }
}