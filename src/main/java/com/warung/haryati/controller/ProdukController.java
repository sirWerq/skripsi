package com.warung.haryati.controller;

import com.warung.haryati.dao.ProdukDao;
import com.warung.haryati.model.Produk;
import com.warung.haryati.util.CurrencyUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ProdukController {

    @FXML private TableView<Produk> tableProduk;
    @FXML private TableColumn<Produk, Integer> colId;
    @FXML private TableColumn<Produk, String> colNama;
    @FXML private TableColumn<Produk, String> colHargaBeli;
    @FXML private TableColumn<Produk, String> colHargaJual;
    @FXML private TableColumn<Produk, Void> colAksi;
    @FXML private Pagination pagination;

    private ProdukDao produkDao = new ProdukDao();
    private final int ROWS_PER_PAGE = 15;
    private ObservableList<Produk> allProduk = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNama.setCellValueFactory(new PropertyValueFactory<>("namaBarang"));
        
        colHargaBeli.setCellValueFactory(cellData -> 
            new SimpleStringProperty(CurrencyUtil.format(cellData.getValue().getHargaBeli())));
        colHargaBeli.getStyleClass().add("currency-column");

        colHargaJual.setCellValueFactory(cellData -> 
            new SimpleStringProperty(CurrencyUtil.format(cellData.getValue().getHargaJual())));
        colHargaJual.getStyleClass().add("currency-column");
        
        setupActionColumn();
        loadData();
        
        pagination.currentPageIndexProperty().addListener((obs, oldIdx, newIdx) -> updatePage(newIdx.intValue()));
    }

    private void loadData() {
        try {
            allProduk.setAll(produkDao.getAll());
            int pageCount = (int) Math.ceil((double) allProduk.size() / ROWS_PER_PAGE);
            pagination.setPageCount(pageCount > 0 ? pageCount : 1);
            updatePage(0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updatePage(int pageIndex) {
        int from = pageIndex * ROWS_PER_PAGE;
        int to = Math.min(from + ROWS_PER_PAGE, allProduk.size());
        if (from < allProduk.size()) {
            tableProduk.setItems(FXCollections.observableArrayList(allProduk.subList(from, to)));
        } else {
            tableProduk.setItems(FXCollections.observableArrayList());
        }
    }

    private void setupActionColumn() {
        colAksi.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button();
            private final Button btnDelete = new Button();
            private final HBox pane = new HBox(5, btnEdit, btnDelete);

            {
                btnEdit.setGraphic(new FontIcon("fas-edit"));
                btnEdit.getStyleClass().add("button-primary");
                btnEdit.setStyle("-fx-background-color: #f1c40f;");
                btnEdit.setOnAction(event -> {
                    Produk p = getTableView().getItems().get(getIndex());
                    handleEdit(p);
                });

                btnDelete.setGraphic(new FontIcon("fas-trash"));
                btnDelete.getStyleClass().add("button-primary");
                btnDelete.setStyle("-fx-background-color: #e74c3c;");
                btnDelete.setOnAction(event -> {
                    Produk p = getTableView().getItems().get(getIndex());
                    handleDelete(p);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else setGraphic(pane);
            }
        });
    }

    @FXML
    private void handleTambah() {
        showForm(null);
    }

    private void handleEdit(Produk p) {
        showForm(p);
    }

    private void showForm(Produk p) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/produk_form.fxml"));
            Stage stage = new Stage();
            stage.setTitle(p == null ? "Tambah Produk" : "Edit Produk");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(loader.load()));
            
            ProdukFormController controller = loader.getController();
            if (p != null) controller.setProduk(p);
            
            stage.showAndWait();
            if (controller.isSaved()) loadData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDelete(Produk p) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Hapus Produk");
        alert.setHeaderText(null);
        alert.setContentText("Apakah Anda yakin ingin menghapus " + p.getNamaBarang() + "?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                produkDao.delete(p.getId());
                loadData();
            } catch (SQLException e) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Gagal Hapus");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText(e.getMessage());
                errorAlert.showAndWait();
            }
        }
    }
}
