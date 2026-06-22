package services;

import java.sql.*;

public class DatabaseService {

    private static final String URL = "jdbc:sqlite:library_asset.db";

    private static Connection connection;


    public static Connection getConnection() throws SQLException {

        if (connection == null || connection.isClosed()) {

            connection = DriverManager.getConnection(URL);

            initializeDatabase();
        }

        return connection;
    }


    private static void initializeDatabase() throws SQLException {

        Statement st = connection.createStatement();


        st.execute("""
            CREATE TABLE IF NOT EXISTS users (
                user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL UNIQUE,
                password_hash TEXT NOT NULL,
                role TEXT NOT NULL,
                email TEXT NOT NULL UNIQUE
            )
        """);


        st.execute("""
            CREATE TABLE IF NOT EXISTS books (
                book_id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                author TEXT NOT NULL,
                isbn TEXT NOT NULL UNIQUE,
                category TEXT NOT NULL,
                total_copies INTEGER NOT NULL,
                available_copies INTEGER NOT NULL
            )
        """);


        st.execute("""
            CREATE TABLE IF NOT EXISTS transactions (
                transaction_id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                book_id INTEGER NOT NULL,
                borrow_date DATE NOT NULL,
                due_date DATE NOT NULL,
                return_date DATE,
                FOREIGN KEY(user_id) REFERENCES users(user_id),
                FOREIGN KEY(book_id) REFERENCES books(book_id)
            )
        """);


        st.execute("""
            CREATE TABLE IF NOT EXISTS fines (
                fine_id INTEGER PRIMARY KEY AUTOINCREMENT,
                transaction_id INTEGER NOT NULL UNIQUE,
                fine_amount REAL NOT NULL,
                is_paid INTEGER NOT NULL DEFAULT 0
            )
        """);


        seedUsers();
        seedBooks();
    }



    private static void seedUsers() throws SQLException {

        String sql =
        """
        INSERT OR IGNORE INTO users
        (username,password_hash,role,email)
        VALUES
        ('system_admin','securepass1','Admin','admin@library.com'),
        ('librarian_desk','securepass2','Librarian','desk@library.com'),
        ('student_user','securepass3','Member','student@university.edu')
        """;


        connection.createStatement().execute(sql);
    }



    private static void seedBooks() throws SQLException {


        String sql =
        """
        INSERT OR IGNORE INTO books
        (title,author,isbn,category,total_copies,available_copies)
        VALUES

        ('Introduction to Java Programming',
        'Y. Daniel Liang',
        '978-0134670942',
        'Textbooks',
        3,
        2),

        ('Design Patterns',
        'Erich Gamma',
        '978-0201633610',
        'Software Architecture',
        2,
        2)
        """;


        connection.createStatement().execute(sql);
    }
}