package controllers;

import javafx.scene.control.TextInputDialog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import models.LibraryItem;
import models.User;
import services.LibraryService;
import utils.AlertUtil;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label totalItemsLabel;

    @FXML
    private Label totalBorrowedLabel;

    @FXML
    private Label totalOverdueLabel;

    @FXML
    private Label totalMembersLabel;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<LibraryItem> itemsTable;

    @FXML
    private TableColumn<LibraryItem, Integer> colId;

    @FXML
    private TableColumn<LibraryItem, String> colTitle;

    @FXML
    private TableColumn<LibraryItem, String> colAuthor;

    @FXML
    private TableColumn<LibraryItem, String> colCategory;

    @FXML
    private TableColumn<LibraryItem, String> colType;

    @FXML
    private TableColumn<LibraryItem, String> colStatus;


    private User currentUser;

    private final LibraryService libraryService = new LibraryService();

    private final ObservableList<LibraryItem> itemList =
            FXCollections.observableArrayList();


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        itemsTable.setItems(itemList);

        loadStats();
        loadAllItems();
    }


    public void setCurrentUser(User user) {

        this.currentUser = user;

        if (user != null) {
            welcomeLabel.setText("Welcome, " + user.getUsername());
        }
    }


    private void loadAllItems() {

        List<LibraryItem> items = libraryService.getAllItems();

        itemList.setAll(items);
    }


    private void loadStats() {

        totalItemsLabel.setText(
                String.valueOf(libraryService.getTotalItems())
        );

        totalBorrowedLabel.setText(
                String.valueOf(libraryService.getTotalBorrowed())
        );

        totalOverdueLabel.setText(
                String.valueOf(libraryService.getTotalOverdue())
        );

        totalMembersLabel.setText(
                String.valueOf(libraryService.getTotalMembers())
        );
    }


    @FXML
    private void handleSearch() {

        String query = searchField.getText().trim();

        if (query.isEmpty()) {
            loadAllItems();
            return;
        }

        List<LibraryItem> results =
                libraryService.searchItems(query);

        itemList.setAll(results);
    }


    @FXML
    private void handleClear() {

        searchField.clear();

        loadAllItems();
    }


    @FXML
    private void handleAddItem() {

        TextInputDialog titleDialog =
                new TextInputDialog();

        titleDialog.setTitle("Add Book");
        titleDialog.setHeaderText("Enter book title");
        titleDialog.setContentText("Title:");

        String title = titleDialog.showAndWait().orElse(null);

        if (title == null || title.trim().isEmpty()) {
            return;
        }


        TextInputDialog authorDialog =
                new TextInputDialog();

        authorDialog.setTitle("Add Book");
        authorDialog.setHeaderText("Enter author");
        authorDialog.setContentText("Author:");

        String author = authorDialog.showAndWait().orElse(null);

        if (author == null || author.trim().isEmpty()) {
            return;
        }


        TextInputDialog isbnDialog =
                new TextInputDialog();

        isbnDialog.setTitle("Add Book");
        isbnDialog.setHeaderText("Enter ISBN");
        isbnDialog.setContentText("ISBN:");

        String isbn = isbnDialog.showAndWait().orElse(null);

        if (isbn == null || isbn.trim().isEmpty()) {
            return;
        }


        TextInputDialog categoryDialog =
                new TextInputDialog();

        categoryDialog.setTitle("Add Book");
        categoryDialog.setHeaderText("Enter category");
        categoryDialog.setContentText("Category:");

        String category = categoryDialog.showAndWait().orElse(null);

        if (category == null || category.trim().isEmpty()) {
            return;
        }


        TextInputDialog copiesDialog =
                new TextInputDialog("1");

        copiesDialog.setTitle("Add Book");
        copiesDialog.setHeaderText("Enter number of copies");
        copiesDialog.setContentText("Copies:");

        String copiesText =
                copiesDialog.showAndWait().orElse(null);


        try {

            int copies = Integer.parseInt(copiesText);


            boolean success =
                    libraryService.addItem(
                            title,
                            author,
                            isbn,
                            category,
                            copies
                    );


            if(success){

                AlertUtil.showInfo(
                        "Success",
                        "Book added successfully."
                );

                loadAllItems();
                loadStats();

            } else {

                AlertUtil.showError(
                        "Failed",
                        "Could not add book."
                );
            }


        } catch(NumberFormatException e){

            AlertUtil.showWarning(
                    "Invalid Input",
                    "Copies must be a number."
            );
        }
    }


    @FXML
    private void handleEditItem() {

        LibraryItem selected =
                itemsTable.getSelectionModel().getSelectedItem();

        if (selected == null) {

            AlertUtil.showWarning(
                    "No Selection",
                    "Please select an item."
            );

            return;
        }


        AlertUtil.showInfo(
                "Edit Item",
                "Editing: " + selected.getTitle()
        );
    }


    @FXML
    private void handleDeleteItem() {

        LibraryItem selected =
                itemsTable.getSelectionModel().getSelectedItem();


        if (selected == null) {

            AlertUtil.showWarning(
                    "No Selection",
                    "Please select an item."
            );

            return;
        }


        boolean confirmed =
                AlertUtil.showConfirmation(
                        "Delete Item",
                        "Delete \"" + selected.getTitle() + "\"?"
                );


        if (confirmed) {

            libraryService.deleteItem(selected.getId());

            loadAllItems();

            loadStats();
        }
    }


    @FXML
    private void showDashboard() {

        loadStats();

        loadAllItems();
    }


    @FXML
    private void showBorrowView() {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass()
                                    .getResource("/ui/BorrowView.fxml")
                    );


            Parent root = loader.load();


            BorrowController controller =
                    loader.getController();


            controller.setCurrentUser(currentUser);


            Stage stage =
                    (Stage) itemsTable
                            .getScene()
                            .getWindow();


            stage.setScene(
                    new Scene(root,900,600)
            );


            stage.setTitle(
                    "LibraTrack - Borrow / Return"
            );


        } catch(Exception e) {

            AlertUtil.showError(
                    "Navigation Error",
                    "Could not load Borrow view."
            );

            e.printStackTrace();
        }
    }


    @FXML
    private void showManageItems() {

        AlertUtil.showInfo(
                "Manage Items",
                "Use the table to manage items."
        );
    }


    @FXML
    private void showReservations() {

        AlertUtil.showInfo(
                "Reservations",
                "Reservation feature coming soon."
        );
    }


    @FXML
    private void showMembers() {

        AlertUtil.showInfo(
                "Members",
                "Member feature coming soon."
        );
    }


    @FXML
    private void handleLogout() {

        boolean confirmed =
                AlertUtil.showConfirmation(
                        "Logout",
                        "Are you sure?"
                );


        if (confirmed) {

            try {

                Parent root =
                        FXMLLoader.load(
                                getClass()
                                .getResource("/ui/LoginView.fxml")
                        );


                Stage stage =
                        (Stage) itemsTable
                                .getScene()
                                .getWindow();


                stage.setScene(
                        new Scene(root,900,600)
                );


                stage.setTitle(
                        "LibraTrack - Login"
                );


            } catch(Exception e) {

                e.printStackTrace();
            }
        }
    }
}