package com.warung.haryati.controller;

import com.warung.haryati.model.AnalisisResult;
import com.warung.haryati.service.FPGrowthService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import com.warung.haryati.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AnalisisController {

    @FXML private TextField txtMinSupport;
    @FXML private TextField txtMinConfidence;
    @FXML private ProgressIndicator loadingIndicator;
    
    @FXML private TableView<AnalisisResult.FrequentItemset> tableItemsets;
    @FXML private TableColumn<AnalisisResult.FrequentItemset, String> colItems;
    @FXML private TableColumn<AnalisisResult.FrequentItemset, Double> colSupport;
    
    @FXML private TableView<AnalisisResult.AssociationRule> tableRules;
    @FXML private TableColumn<AnalisisResult.AssociationRule, String> colAntecedents;
    @FXML private TableColumn<AnalisisResult.AssociationRule, String> colConsequents;
    @FXML private TableColumn<AnalisisResult.AssociationRule, Double> colRuleSupport;
    @FXML private TableColumn<AnalisisResult.AssociationRule, Double> colConfidence;
    @FXML private TableColumn<AnalisisResult.AssociationRule, Double> colLift;

    private final FPGrowthService analysisService = new FPGrowthService();

    @FXML
    public void initialize() {
        setupTables();
        loadConfigFromDatabase();
    }

    private void loadConfigFromDatabase() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT min_support, min_confidence FROM proses_analisis_fp_growth LIMIT 1");
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                txtMinSupport.setText(String.valueOf(rs.getDouble("min_support")));
                txtMinConfidence.setText(String.valueOf(rs.getDouble("min_confidence")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveConfigToDatabase(double minSup, double minConf) {
        String sql = "UPDATE proses_analisis_fp_growth SET min_support = ?, min_confidence = ? WHERE id_analisis = '1'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, minSup);
            pstmt.setDouble(2, minConf);
            int updated = pstmt.executeUpdate();
            if (updated == 0) {
                // If id_analisis = '1' doesn't exist, insert it
                try (PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO proses_analisis_fp_growth (id_analisis, min_support, min_confidence) VALUES ('1', ?, ?)")) {
                    insertStmt.setDouble(1, minSup);
                    insertStmt.setDouble(2, minConf);
                    insertStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupTables() {
        colItems.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getItemsetsString()));
        colSupport.setCellValueFactory(new PropertyValueFactory<>("support"));
        
        colAntecedents.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAntecedentsString()));
        colConsequents.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getConsequentsString()));
        colRuleSupport.setCellValueFactory(new PropertyValueFactory<>("support"));
        colConfidence.setCellValueFactory(new PropertyValueFactory<>("confidence"));
        colLift.setCellValueFactory(new PropertyValueFactory<>("lift"));
        
        formatDoubleColumn(colSupport);
        formatDoubleColumn(colRuleSupport);
        formatDoubleColumn(colConfidence);
        formatDoubleColumn(colLift);

        for (TableView<?> table : new TableView<?>[]{tableItemsets, tableRules}) {
            if (table != null) {
                for (TableColumn<?, ?> col : table.getColumns()) {
                    col.setSortable(false);
                }
            }
        }
    }

    private <S> void formatDoubleColumn(TableColumn<S, Double> column) {
        column.setCellFactory(tc -> new TableCell<S, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.3f", item));
                }
            }
        });
    }


    @FXML
    private void handleAnalyze() {
        try {
            double minSup = Double.parseDouble(txtMinSupport.getText());
            double minConf = Double.parseDouble(txtMinConfidence.getText());
            
            if (minSup <= 0 || minSup > 1 || minConf <= 0 || minConf > 1) {
                showAlert("Input Salah", "Nilai support dan confidence harus antara 0.01 sampai 1.0");
                return;
            }
            
            saveConfigToDatabase(minSup, minConf);

            System.out.println("Memulai analisis dengan support: " + minSup + ", confidence: " + minConf);
            loadingIndicator.setVisible(true);
            
            new Thread(() -> {
                try {
                    System.out.println("Menjalankan FPGrowthService...");
                    AnalisisResult result = analysisService.runAnalysis(minSup, minConf);
                    System.out.println("Analisis selesai. Jumlah itemsets: " + 
                        (result.getFrequent_itemsets() != null ? result.getFrequent_itemsets().size() : 0));
                    
                    Platform.runLater(() -> {
                        loadingIndicator.setVisible(false);
                        if (result.getError() != null) {
                            showAlert("Error Python", result.getError());
                        } else {
                            tableItemsets.setItems(FXCollections.observableArrayList(result.getFrequent_itemsets()));
                            tableRules.setItems(FXCollections.observableArrayList(result.getAssociation_rules()));
                            System.out.println("Data telah dimuat ke tabel UI.");
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Error saat analisis: " + e.getMessage());
                    Platform.runLater(() -> {
                        loadingIndicator.setVisible(false);
                        showAlert("Error", "Gagal menjalankan analisis: " + e.getMessage());
                        e.printStackTrace();
                    });
                }
            }).start();


        } catch (NumberFormatException e) {
            showAlert("Input Salah", "Harap masukkan angka yang valid untuk support dan confidence.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
