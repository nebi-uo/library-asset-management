package models;

public class BorrowRecord {
    private int id;
    private int memberId;
    private String itemTitle;
    private String borrowDate;
    private String dueDate;
    private String status;

    public BorrowRecord(int id, int memberId, String itemTitle,
                        String borrowDate, String dueDate, String status) {
        this.id = id;
        this.memberId = memberId;
        this.itemTitle = itemTitle;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.status = status;
    }

    public int getId() { return id; }
    public int getMemberId() { return memberId; }
    public String getItemTitle() { return itemTitle; }
    public String getBorrowDate() { return borrowDate; }
    public String getDueDate() { return dueDate; }
    public String getStatus() { return status; }
}