package com.warung.haryati.controller;

import com.warung.haryati.dao.ProdukDao;
import com.warung.haryati.dao.TransaksiDao;
import com.warung.haryati.model.DetailTransaksi;
import com.warung.haryati.model.Produk;
import com.warung.haryati.model.Transaksi;
import com.warung.haryati.util.CurrencyUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransaksiFormController {

    @FXML private DatePicker tanggalPicker;
    @FXML private ComboBox<Produk> produkCombo;
    @FXML private TextField qtyField;
    @FXML private TableView<DetailTransaksi> tableItems;
    @FXML private TableColumn<DetailTransaksi, String> colProduk;
    @FXML private TableColumn<DetailTransaksi, Integer> colQty;
    @FXML private TableColumn<DetailTransaksi, String> colSubtotal;
    @FXML private Label totalLabel;

    private ProdukDao produkDao = new ProdukDao();
    private TransaksiDao transaksiDao = new TransaksiDao();
    private ObservableList<DetailTransaksi> items = FXCollections.observableArrayList();
    private boolean saved = false;
    private Transaksi currentTransaksi;

    public void setTransaksi(Transaksi t) {
        this.currentTransaksi = t;
        this.tanggalPicker.setValue(t.getTanggal().toLocalDate());
        try {
            List<DetailTransaksi> existingItems = transaksiDao.getDetailsByTransaksiId(t.getId());
            items.setAll(existingItems);
            updateTotal();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isSaved() { return saved; }

    @FXML
    public void initialize() {
        tanggalPicker.setValue(LocalDate.now());
        tanggalPicker.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return (date != null) ? com.warung.haryati.util.DateUtil.formatShort(date) : "";
            }
            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.trim().isEmpty()) {
                    try {
                        return LocalDate.parse(string.trim(), com.warung.haryati.util.DateUtil.FORMAT_SHORT);
                    } catch (Exception e) {
                        return LocalDate.now();
                    }
                }
                return null;
            }
        });
        setupProdukCombo();
        
        colProduk.setCellValueFactory(new PropertyValueFactory<>("produkId")); 
        colQty.setCellValueFactory(new PropertyValueFactory<>("kuantitas"));
        colQty.getStyleClass().add("center-column");
        
        colSubtotal.setCellValueFactory(cellData -> 
            new SimpleStringProperty(CurrencyUtil.format(cellData.getValue().getSubtotal())));
        colSubtotal.getStyleClass().add("currency-column");
        
        tableItems.setItems(items);
    }

    private void setupProdukCombo() {
        try {
            produkCombo.setItems(FXCollections.observableArrayList(produkDao.getAll()));
            produkCombo.setConverter(new StringConverter<Produk>() {
                @Override public String toString(Produk p) { return p == null ? "" : p.getNamaBarang(); }
                @Override public Produk fromString(String s) { return null; }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleTambahItem() {
        Produk p = produkCombo.getValue();
        String qtyStr = qtyField.getText().trim();
        
        if (p == null) {
            showAlert("Error", "Pilih produk terlebih dahulu!");
            return;
        }
        if (qtyStr.isEmpty()) {
            showAlert("Error", "Masukkan kuantitas!");
            return;
        }
        
        try {
            int qty = Integer.parseInt(qtyStr);
            if (qty <= 0) {
                showAlert("Error", "Kuantitas harus lebih dari 0!");
                return;
            }

            double subtotal = p.getHargaJual() * qty;
            double laba = (p.getHargaJual() - p.getHargaBeli()) * qty;
            
            DetailTransaksi dt = new DetailTransaksi();
            dt.setProdukId(p.getId());
            dt.setKuantitas(qty);
            dt.setSubtotal(subtotal);
            dt.setLaba(laba);
            
            items.add(dt);
            updateTotal();
            
            qtyField.clear();
        } catch (NumberFormatException e) {
            showAlert("Error", "Kuantitas harus berupa angka bulat!");
        }
    }

    private void updateTotal() {
        double total = items.stream().mapToDouble(DetailTransaksi::getSubtotal).sum();
        totalLabel.setText(CurrencyUtil.format(total));
    }

    @FXML
    private void handleSimpan() {
        if (items.isEmpty()) {
            showAlert("Error", "Tambahkan minimal satu item!");
            return;
        }

        if (tanggalPicker.getValue() == null) {
            showAlert("Error", "Pilih tanggal transaksi!");
            return;
        }
        
        try {
            if (currentTransaksi == null) {
                Transaksi t = new Transaksi();
                t.setTanggal(Date.valueOf(tanggalPicker.getValue()));
                transaksiDao.insertWithDetails(t, new ArrayList<>(items));
            } else {
                currentTransaksi.setTanggal(Date.valueOf(tanggalPicker.getValue()));
                transaksiDao.updateWithDetails(currentTransaksi, new ArrayList<>(items));
            }
            saved = true;
            closeStage();
        } catch (SQLException e) {
            showAlert("Database Error", "Gagal menyimpan transaksi: " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML private void handleBatal() { closeStage(); }
    private void closeStage() { ((Stage) totalLabel.getScene().getWindow()).close(); }
}
