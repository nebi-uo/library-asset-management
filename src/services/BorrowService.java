package services;

import models.BorrowRecord;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class BorrowService {

    public List<BorrowRecord> getActiveBorrows() {

        List<BorrowRecord> records = new ArrayList<>();

        String sql = """
            SELECT 
                t.transaction_id,
                t.user_id,
                b.title,
                t.borrow_date,
                t.due_date,
                CASE 
                    WHEN DATE(t.due_date) < DATE('now')
                    THEN 'Overdue'
                    ELSE 'Active'
                END AS status
            FROM transactions t
            JOIN books b 
            ON t.book_id = b.book_id
            WHERE t.return_date IS NULL
        """;

        try (
            Connection conn = DatabaseService.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql)
        ) {

            while (rs.next()) {

                records.add(new BorrowRecord(
                    rs.getInt("transaction_id"),
                    rs.getInt("user_id"),
                    rs.getString("title"),
                    rs.getString("borrow_date"),
                    rs.getString("due_date"),
                    rs.getString("status")
                ));
            }

        } catch (SQLException e) {
            System.out.println("LOAD BORROWS ERROR:");
            e.printStackTrace();
        }

        return records;
    }


    public boolean borrowItem(int memberId, int itemId) {

        try (Connection conn = DatabaseService.getConnection()) {

            conn.setAutoCommit(false);

            // Check member exists
            PreparedStatement memberCheck = conn.prepareStatement(
                """
                SELECT user_id
                FROM users
                WHERE user_id = ?
                AND role = 'Member'
                """
            );

            memberCheck.setInt(1, memberId);

            ResultSet memberResult = memberCheck.executeQuery();

            if (!memberResult.next()) {
                System.out.println("BORROW FAILED: Member does not exist or is not a Member");
                conn.rollback();
                return false;
            }


            // Check book exists and availability
            PreparedStatement bookCheck = conn.prepareStatement(
                """
                SELECT available_copies
                FROM books
                WHERE book_id = ?
                """
            );

            bookCheck.setInt(1, itemId);

            ResultSet bookResult = bookCheck.executeQuery();


            if (!bookResult.next()) {
                System.out.println("BORROW FAILED: Book does not exist");
                conn.rollback();
                return false;
            }


            int available = bookResult.getInt("available_copies");


            if (available <= 0) {
                System.out.println("BORROW FAILED: No available copies");
                conn.rollback();
                return false;
            }



            // Insert transaction
            PreparedStatement insert = conn.prepareStatement(
                """
                INSERT INTO transactions
                (user_id, book_id, borrow_date, due_date)
                VALUES (?, ?, ?, ?)
                """
            );


            LocalDate today = LocalDate.now();


            insert.setInt(1, memberId);
            insert.setInt(2, itemId);
            insert.setDate(3, Date.valueOf(today));
            insert.setDate(4, Date.valueOf(today.plusDays(14)));

            insert.executeUpdate();



            // Update available copies
            PreparedStatement update = conn.prepareStatement(
                """
                UPDATE books
                SET available_copies = available_copies - 1
                WHERE book_id = ?
                """
            );


            update.setInt(1, itemId);

            update.executeUpdate();



            conn.commit();

            return true;



        } catch (SQLException e) {

            System.out.println("BORROW ERROR:");
            e.printStackTrace();

            return false;
        }
    }



    public double returnItem(int recordId) {

        try(Connection conn = DatabaseService.getConnection()) {


            PreparedStatement find = conn.prepareStatement(
                """
                SELECT book_id, due_date
                FROM transactions
                WHERE transaction_id = ?
                AND return_date IS NULL
                """
            );


            find.setInt(1, recordId);


            ResultSet rs = find.executeQuery();


            if(!rs.next()) {

                System.out.println("RETURN FAILED: Record not found");
                return -1;
            }


            int bookId = rs.getInt("book_id");

            LocalDate dueDate =
                    rs.getDate("due_date").toLocalDate();


            LocalDate today = LocalDate.now();


            double fine = 0;


            if(today.isAfter(dueDate)) {

                long daysLate =
                        ChronoUnit.DAYS.between(dueDate, today);


                fine = daysLate * 0.50;


                PreparedStatement fineInsert = conn.prepareStatement(
                    """
                    INSERT INTO fines
                    (transaction_id, fine_amount, is_paid)
                    VALUES (?, ?, 0)
                    """
                );


                fineInsert.setInt(1, recordId);
                fineInsert.setDouble(2, fine);

                fineInsert.executeUpdate();
            }



            PreparedStatement returnBook = conn.prepareStatement(
                """
                UPDATE transactions
                SET return_date = ?
                WHERE transaction_id = ?
                """
            );


            returnBook.setDate(1, Date.valueOf(today));
            returnBook.setInt(2, recordId);

            returnBook.executeUpdate();



            PreparedStatement increaseCopies = conn.prepareStatement(
                """
                UPDATE books
                SET available_copies = available_copies + 1
                WHERE book_id = ?
                """
            );


            increaseCopies.setInt(1, bookId);

            increaseCopies.executeUpdate();


            return fine;


        } catch(SQLException e) {

            System.out.println("RETURN ERROR:");
            e.printStackTrace();

            return -1;
        }
    }
}