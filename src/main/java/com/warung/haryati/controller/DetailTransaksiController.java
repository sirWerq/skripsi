package com.warung.haryati.controller;

import com.warung.haryati.dao.DetailTransaksiDao;
import com.warung.haryati.dao.ProdukDao;
import com.warung.haryati.model.DetailTransaksi;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DetailTransaksiController {

    @FXML private TableView<DetailTransaksi> tableDetailTransaksi;
    @FXML private TableColumn<DetailTransaksi, String> colId;
    @FXML private TableColumn<DetailTransaksi, String> colTransaksiId;
    @FXML private TableColumn<DetailTransaksi, String> colProduk;
    @FXML private TableColumn<DetailTransaksi, Integer> colKuantitas;
    @FXML private TableColumn<DetailTransaksi, String> colSubtotal;
    @FXML private TableColumn<DetailTransaksi, String> colLaba;
    @FXML private TableColumn<DetailTransaksi, Void> colAksi;
    @FXML private Pagination pagination;

    private DetailTransaksiDao detailTransaksiDao = new DetailTransaksiDao();
    private ProdukDao produkDao = new ProdukDao();
    
    private final int ROWS_PER_PAGE = 15;
    private ObservableList<DetailTransaksi> allData = FXCollections.observableArrayList();
    private Map<String, String> produkMap = new HashMap<>();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("detailId"));
        colId.getStyleClass().add("center-column");
        
        colTransaksiId.setCellValueFactory(new PropertyValueFactory<>("transaksiId"));
        colTransaksiId.getStyleClass().add("center-column");

        colProduk.setCellValueFactory(cellData -> {
            String pid = cellData.getValue().getProdukId();
            return new SimpleStringProperty(produkMap.getOrDefault(pid, pid));
        });

        colKuantitas.setCellValueFactory(new PropertyValueFactory<>("kuantitas"));
        colKuantitas.getStyleClass().add("center-column");

        colSubtotal.setCellValueFactory(cellData -> 
            new SimpleStringProperty(CurrencyUtil.format(cellData.getValue().getSubtotal())));
        colSubtotal.getStyleClass().add("currency-column");
        
        colLaba.setCellValueFactory(cellData -> 
            new SimpleStringProperty(CurrencyUtil.format(cellData.getValue().getLaba())));
        colLaba.getStyleClass().add("currency-column");
        
        setupActionColumn();
        if (tableDetailTransaksi != null) {
            for (TableColumn<?, ?> col : tableDetailTransaksi.getColumns()) {
                col.setSortable(false);
            }
        }
        loadData();

        pagination.currentPageIndexProperty().addListener((obs, oldIdx, newIdx) -> updatePage(newIdx.intValue()));
    }

    private void loadData() {
        try {
            // Load Produk Map
            produkMap.clear();
            List<Produk> produks = produkDao.getAll();
            for (Produk p : produks) {
                produkMap.put(p.getIdProduk(), p.getNamaBarang());
            }

            List<DetailTransaksi> list = detailTransaksiDao.getAll();
            allData.setAll(list);
            
            int pageCount = (int) Math.ceil((double) allData.size() / ROWS_PER_PAGE);
            pagination.setPageCount(pageCount > 0 ? pageCount : 1);
            updatePage(pagination.getCurrentPageIndex());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updatePage(int pageIndex) {
        int from = pageIndex * ROWS_PER_PAGE;
        int to = Math.min(from + ROWS_PER_PAGE, allData.size());
        if (from < allData.size()) {
            tableDetailTransaksi.setItems(FXCollections.observableArrayList(allData.subList(from, to)));
        } else {
            tableDetailTransaksi.setItems(FXCollections.observableArrayList());
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
                    DetailTransaksi t = getTableView().getItems().get(getIndex());
                    handleEdit(t);
                });

                btnDelete.setGraphic(new FontIcon("fas-trash"));
                btnDelete.getStyleClass().add("button-primary");
                btnDelete.setStyle("-fx-background-color: #e74c3c;");
                btnDelete.setOnAction(event -> {
                    DetailTransaksi t = getTableView().getItems().get(getIndex());
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

    private void handleEdit(DetailTransaksi t) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/detail_transaksi_form.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Edit Detail Transaksi #" + t.getDetailId());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(loader.load()));
            
            DetailTransaksiFormController controller = loader.getController();
            controller.setDetailTransaksi(t);
            stage.showAndWait();
            
            if (controller.isSaved()) {
                loadData();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDelete(DetailTransaksi t) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Hapus Detail Transaksi");
        alert.setHeaderText(null);
        alert.setContentText("Apakah Anda yakin ingin menghapus detail transaksi #" + t.getDetailId() + "?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                produkDao.updateStok(t.getProdukId(), t.getKuantitas());
                detailTransaksiDao.delete(t.getDetailId());
                loadData();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleTambah() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/detail_transaksi_form.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Tambah Detail Transaksi");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(loader.load()));
            
            DetailTransaksiFormController controller = loader.getController();
            stage.showAndWait();
            
            if (controller.isSaved()) {
                loadData();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
