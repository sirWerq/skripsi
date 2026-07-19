package com.warung.haryati.controller;

import com.warung.haryati.dao.TransaksiDao;
import com.warung.haryati.model.Transaksi;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

public class TransaksiFormController {

    @FXML private DatePicker tanggalPicker;
    @FXML private TextField idField;

    private TransaksiDao transaksiDao = new TransaksiDao();
    private boolean saved = false;
    private Transaksi currentTransaksi;

    public void setTransaksi(Transaksi t) {
        this.currentTransaksi = t;
        this.idField.setText(t.getId());
        this.idField.setDisable(true);
        this.tanggalPicker.setValue(t.getTanggal().toLocalDate());
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
    }

    @FXML
    private void handleSimpan() {
        if (currentTransaksi == null && (idField.getText() == null || idField.getText().trim().isEmpty())) {
            showAlert("Error", "ID Transaksi tidak boleh kosong!");
            return;
        }

        if (tanggalPicker.getValue() == null) {
            showAlert("Error", "Pilih tanggal transaksi!");
            return;
        }
        
        try {
            if (currentTransaksi == null) {
                Transaksi t = new Transaksi();
                t.setId(idField.getText().trim());
                t.setTanggal(Date.valueOf(tanggalPicker.getValue()));
                transaksiDao.insert(t);
            } else {
                currentTransaksi.setTanggal(Date.valueOf(tanggalPicker.getValue()));
                transaksiDao.update(currentTransaksi);
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
    private void closeStage() { ((Stage) tanggalPicker.getScene().getWindow()).close(); }
}
