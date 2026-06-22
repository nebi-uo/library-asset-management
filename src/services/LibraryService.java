package services;

import models.LibraryItem;
import models.PhysicalBook;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibraryService {

    private LibraryItem mapRow(ResultSet rs) throws SQLException {
        return new PhysicalBook(
            rs.getInt("book_id"),
            rs.getString("title"),
            rs.getString("author"),
            rs.getString("isbn"),
            rs.getString("category"),
            rs.getInt("total_copies"),
            rs.getInt("available_copies")
        );
    }

    public List<LibraryItem> getAllItems() {
        List<LibraryItem> items = new ArrayList<>();
        try (Connection conn = DatabaseService.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM books")) {
            while (rs.next()) items.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return items;
    }

    public List<LibraryItem> searchItems(String query) {
        List<LibraryItem> items = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ?";
        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + query + "%");
            ps.setString(2, "%" + query + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) items.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return items;
    }

    public void deleteItem(int id) {
        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM books WHERE book_id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public int getTotalItems() {
        return getCount("SELECT COUNT(*) FROM books");
    }

    public int getTotalBorrowed() {
        return getCount("SELECT COUNT(*) FROM transactions WHERE return_date IS NULL");
    }

    public int getTotalOverdue() {
        return getCount("SELECT COUNT(*) FROM transactions WHERE return_date IS NULL AND due_date < DATE('now')");
    }

    public int getTotalMembers() {
        return getCount("SELECT COUNT(*) FROM users WHERE role = 'Member'");
    }

    private int getCount(String sql) {
        try (Connection conn = DatabaseService.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public void addItem(
        String title,
        String author,
        String isbn,
        String category
    ) {

        String sql =
        """
        INSERT INTO books
        (title,author,isbn,category,total_copies,available_copies)
        VALUES(?,?,?,?,?,?)
        """;


        try(Connection conn = DatabaseService.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {


            ps.setString(1,title);
            ps.setString(2,author);
            ps.setString(3,isbn);
            ps.setString(4,category);
            ps.setInt(5,1);
            ps.setInt(6,1);


            ps.executeUpdate();


        } catch(SQLException e) {

            e.printStackTrace();
        }
    }

    public boolean addItem(
        String title,
        String author,
        String isbn,
        String category,
        int copies
    ) {

        String sql =
            "INSERT INTO books " +
            "(title, author, isbn, category, total_copies, available_copies) " +
            "VALUES (?, ?, ?, ?, ?, ?)";


        try(Connection conn = DatabaseService.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {


            ps.setString(1, title);
            ps.setString(2, author);
            ps.setString(3, isbn);
            ps.setString(4, category);
            ps.setInt(5, copies);
            ps.setInt(6, copies);


            ps.executeUpdate();

            return true;


        } catch(SQLException e){

            e.printStackTrace();

            return false;
        }
    }
}