package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.BorrowRecord;
import models.User;
import services.BorrowService;
import utils.AlertUtil;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class BorrowController implements Initializable {

    @FXML private Label welcomeLabel;
    @FXML private Label fineLabel;

    @FXML private TextField borrowMemberIdField;
    @FXML private TextField borrowItemIdField;
    @FXML private TextField returnRecordIdField;

    @FXML private TableView<BorrowRecord> borrowTable;
    @FXML private TableColumn<BorrowRecord, Integer> colRecordId;
    @FXML private TableColumn<BorrowRecord, Integer> colMemberId;
    @FXML private TableColumn<BorrowRecord, String> colItemTitle;
    @FXML private TableColumn<BorrowRecord, String> colBorrowDate;
    @FXML private TableColumn<BorrowRecord, String> colDueDate;
    @FXML private TableColumn<BorrowRecord, String> colBorrowStatus;

    private User currentUser;
    private BorrowService borrowService = new BorrowService();
    private ObservableList<BorrowRecord> borrowList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colRecordId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colMemberId.setCellValueFactory(new PropertyValueFactory<>("memberId"));
        colItemTitle.setCellValueFactory(new PropertyValueFactory<>("itemTitle"));
        colBorrowDate.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        colDueDate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        colBorrowStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        borrowTable.setItems(borrowList);
        loadActiveBorrows();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        welcomeLabel.setText("Welcome, " + user.getUsername());
    }

    private void loadActiveBorrows() {
        List<BorrowRecord> records = borrowService.getActiveBorrows();
        borrowList.setAll(records);
    }

    @FXML
    private void handleBorrow() {
        String memberIdText = borrowMemberIdField.getText().trim();
        String itemIdText = borrowItemIdField.getText().trim();

        if (memberIdText.isEmpty() || itemIdText.isEmpty()) {
            AlertUtil.showWarning("Missing Fields", "Please enter both Member ID and Item ID.");
            return;
        }

        try {
            int memberId = Integer.parseInt(memberIdText);
            int itemId = Integer.parseInt(itemIdText);

            boolean success = borrowService.borrowItem(memberId, itemId);

            if (success) {
                AlertUtil.showInfo("Success", "Item successfully borrowed.");
                borrowMemberIdField.clear();
                borrowItemIdField.clear();
                fineLabel.setText("");
                loadActiveBorrows();
            } else {
                AlertUtil.showError("Failed", "Could not process borrow. Item may be unavailable or member has reached their limit.");
            }
        } catch (NumberFormatException e) {
            AlertUtil.showWarning("Invalid Input", "Member ID and Item ID must be numbers.");
        }
    }

    @FXML
    private void handleReturn() {
        String recordIdText = returnRecordIdField.getText().trim();

        if (recordIdText.isEmpty()) {
            AlertUtil.showWarning("Missing Field", "Please enter a Borrow Record ID.");
            return;
        }

        try {
            int recordId = Integer.parseInt(recordIdText);
            double fine = borrowService.returnItem(recordId);

            if (fine < 0) {
                AlertUtil.showError("Failed", "Could not process return. Record not found.");
                return;
            }

            if (fine > 0) {
                fineLabel.setText("Overdue fine applied: $" + String.format("%.2f", fine));
                AlertUtil.showWarning("Overdue Fine", "Item returned with a fine of $" + String.format("%.2f", fine));
            } else {
                fineLabel.setText("Returned on time. No fine.");
                AlertUtil.showInfo("Success", "Item returned successfully. No fine.");
            }

            returnRecordIdField.clear();
            loadActiveBorrows();

        } catch (NumberFormatException e) {
            AlertUtil.showWarning("Invalid Input", "Borrow Record ID must be a number.");
        }
    }

    @FXML
    private void goToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/DashboardView.fxml"));
            Parent root = loader.load();

            DashboardController dashboardController = loader.getController();
            dashboardController.setCurrentUser(currentUser);

            Stage stage = (Stage) borrowTable.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.setTitle("LibraTrack - Dashboard");
        } catch (Exception e) {
            AlertUtil.showError("Navigation Error", "Could not load Dashboard.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        boolean confirmed = AlertUtil.showConfirmation("Logout", "Are you sure you want to logout?");
        if (confirmed) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/ui/LoginView.fxml"));
                Stage stage = (Stage) borrowTable.getScene().getWindow();
                stage.setScene(new Scene(root, 900, 600));
                stage.setTitle("LibraTrack - Login");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}