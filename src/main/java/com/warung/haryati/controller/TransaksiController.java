package com.warung.haryati.controller;

import com.warung.haryati.dao.TransaksiDao;
import com.warung.haryati.model.Transaksi;
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

public class TransaksiController {

    @FXML private TableView<Transaksi> tableTransaksi;
    @FXML private TableColumn<Transaksi, String> colId;
    @FXML private TableColumn<Transaksi, String> colTanggal;
    @FXML private TableColumn<Transaksi, String> colTotal;
    @FXML private TableColumn<Transaksi, Void> colAksi;
    @FXML private Pagination pagination;

    private TransaksiDao transaksiDao = new TransaksiDao();
    private final int ROWS_PER_PAGE = 15;
    private ObservableList<Transaksi> allTransaksi = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("transaksiId"));
        colId.getStyleClass().add("center-column");

        colTanggal.setCellValueFactory(cellData -> 
            new SimpleStringProperty(com.warung.haryati.util.DateUtil.formatShort(cellData.getValue().getTanggal())));
        colTanggal.getStyleClass().add("center-column");

        colTotal.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(CurrencyUtil.format(cellData.getValue().getTotalBelanja())));
        colTotal.getStyleClass().add("currency-column");
        
        setupActionColumn();
        if (tableTransaksi != null) {
            for (TableColumn<?, ?> col : tableTransaksi.getColumns()) {
                col.setSortable(false);
            }
        }
        loadData();

        pagination.currentPageIndexProperty().addListener((obs, oldIdx, newIdx) -> updatePage(newIdx.intValue()));
    }

    private void loadData() {
        try {
            List<Transaksi> list = transaksiDao.getAll();
            for (Transaksi t : list) {
                t.setTotalBelanja(transaksiDao.getTotalByTransaksiId(t.getTransaksiId()));
            }
            allTransaksi.setAll(list);
            
            int pageCount = (int) Math.ceil((double) allTransaksi.size() / ROWS_PER_PAGE);
            pagination.setPageCount(pageCount > 0 ? pageCount : 1);
            updatePage(0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updatePage(int pageIndex) {
        int from = pageIndex * ROWS_PER_PAGE;
        int to = Math.min(from + ROWS_PER_PAGE, allTransaksi.size());
        if (from < allTransaksi.size()) {
            tableTransaksi.setItems(FXCollections.observableArrayList(allTransaksi.subList(from, to)));
        } else {
            tableTransaksi.setItems(FXCollections.observableArrayList());
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
                btnEdit.setStyle("-fx-background-color: #f39c12;");
                btnEdit.setOnAction(event -> {
                    Transaksi t = getTableView().getItems().get(getIndex());
                    handleEdit(t);
                });

                btnDelete.setGraphic(new FontIcon("fas-trash"));
                btnDelete.getStyleClass().add("button-primary");
                btnDelete.setStyle("-fx-background-color: #e74c3c;");
                btnDelete.setOnAction(event -> {
                    Transaksi t = getTableView().getItems().get(getIndex());
                    handleDelete(t);
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

    private void handleEdit(Transaksi t) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/transaksi_form.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Edit Transaksi #" + t.getTransaksiId());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(loader.load()));
            
            TransaksiFormController controller = loader.getController();
            controller.setTransaksi(t);
            stage.showAndWait();
            
            if (controller.isSaved()) {
                loadData();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDelete(Transaksi t) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Hapus Transaksi");
        alert.setHeaderText(null);
        alert.setContentText("Apakah Anda yakin ingin menghapus transaksi #" + t.getTransaksiId() + "?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                transaksiDao.delete(t.getTransaksiId());
                loadData();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleTambah() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/transaksi_form.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Tambah Transaksi");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(loader.load()));
            
            TransaksiFormController controller = loader.getController();
            stage.showAndWait();
            
            if (controller.isSaved()) {
                loadData();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
