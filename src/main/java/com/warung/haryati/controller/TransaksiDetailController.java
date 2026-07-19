package com.warung.haryati.controller;

import com.warung.haryati.dao.ProdukDao;
import com.warung.haryati.dao.TransaksiDao;
import com.warung.haryati.model.DetailTransaksi;
import com.warung.haryati.model.Produk;
import com.warung.haryati.model.Transaksi;
import com.warung.haryati.util.CurrencyUtil;
import com.warung.haryati.util.IDGenerator;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransaksiDetailController {

    @FXML private Text txtId, txtTanggal, txtTotal;
    @FXML private TableView<DetailTransaksi> tableItems;
    @FXML private TableColumn<DetailTransaksi, String> colProduk;
    @FXML private TableColumn<DetailTransaksi, String> colHarga;
    @FXML private TableColumn<DetailTransaksi, Integer> colQty;
    @FXML private TableColumn<DetailTransaksi, String> colSubtotal;

    private TransaksiDao transaksiDao = new TransaksiDao();
    private ProdukDao produkDao = new ProdukDao();
    private Map<String, String> produkCache = new HashMap<>();

    @FXML
    public void initialize() {
        colProduk.setCellValueFactory(cellData -> {
            String pId = cellData.getValue().getProdukId();
            return new SimpleStringProperty(getProdukNama(pId));
        });
        colHarga.setCellValueFactory(cellData -> 
            new SimpleStringProperty(CurrencyUtil.format(cellData.getValue().getSubtotal() / cellData.getValue().getKuantitas())));
        colHarga.getStyleClass().add("currency-column");

        colQty.setCellValueFactory(new PropertyValueFactory<>("kuantitas"));
        colQty.getStyleClass().add("center-column");
        
        colSubtotal.setCellValueFactory(cellData -> 
            new SimpleStringProperty(CurrencyUtil.format(cellData.getValue().getSubtotal())));
        colSubtotal.getStyleClass().add("currency-column");

        if (tableItems != null) {
            for (TableColumn<?, ?> col : tableItems.getColumns()) {
                col.setSortable(false);
            }
        }
    }

    public void setData(Transaksi t) {
        txtId.setText("#" + t.getId());
        txtTanggal.setText(com.warung.haryati.util.DateUtil.formatLong(t.getTanggal()));
        txtTotal.setText(CurrencyUtil.format(t.getTotal()));
        
        try {
            List<DetailTransaksi> details = transaksiDao.getDetailsByTransaksiId(t.getId());
            tableItems.setItems(FXCollections.observableArrayList(details));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getProdukNama(String id) {
        if (produkCache.containsKey(id)) return produkCache.get(id);
        try {
            List<Produk> all = produkDao.getAll();
            for (Produk p : all) {
                if (p.getId().equals(id)) {
                    produkCache.put(id, p.getNamaBarang());
                    return p.getNamaBarang();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknown (" + id + ")";
    }

    @FXML
    private void handleTutup() {
        ((Stage) txtId.getScene().getWindow()).close();
    }
}
