package com.warung.haryati.util;

import com.warung.haryati.controller.LaporanController.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Utility class untuk export Laporan ke format Microsoft Excel (.xlsx)
 * yang profesional, rapi, enak dilihat mata, dan hanya menampilkan informasi penting yang to the point.
 */
public class ExcelReportUtil {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Export Laporan Laba Rugi ke Excel (.xlsx)
     */
    public static void exportLabaRugi(File file, List<LabaRugiRow> data, LocalDate start, LocalDate end) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Laba Rugi");
            enableGridLines(sheet);

            Styles styles = new Styles(workbook);

            // Title Block
            createTitleBlock(sheet, styles, "LAPORAN LABA RUGI & OMSET PENJUALAN", 
                    "Periode: " + formatPeriode(start, end) + " | Warung Haryati", 4);

            // Header Row
            int rowIdx = 3;
            Row headerRow = sheet.createRow(rowIdx++);
            headerRow.setHeightInPoints(26);
            String[] headers = {"Tanggal", "Jumlah Transaksi", "Omset Penjualan", "Modal (HPP)", "Laba Bersih"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(styles.headerStyle);
            }

            // Data Rows
            int totalTx = 0;
            double grandOmset = 0;
            double grandModal = 0;
            double grandLaba = 0;

            for (int i = 0; i < data.size(); i++) {
                LabaRugiRow item = data.get(i);
                Row row = sheet.createRow(rowIdx++);
                row.setHeightInPoints(20);
                boolean zebra = (i % 2 == 1);

                CellStyle textStyle = zebra ? styles.textZebra : styles.textNormal;
                CellStyle intStyle = zebra ? styles.intZebra : styles.intNormal;
                CellStyle currStyle = zebra ? styles.currZebra : styles.currNormal;

                Cell c0 = row.createCell(0);
                c0.setCellValue(item.getTanggal());
                c0.setCellStyle(textStyle);

                Cell c1 = row.createCell(1);
                c1.setCellValue(item.getJmlTransaksi());
                c1.setCellStyle(intStyle);

                Cell c2 = row.createCell(2);
                c2.setCellValue(item.getOmset());
                c2.setCellStyle(currStyle);

                Cell c3 = row.createCell(3);
                c3.setCellValue(item.getModal());
                c3.setCellStyle(currStyle);

                Cell c4 = row.createCell(4);
                c4.setCellValue(item.getLaba());
                c4.setCellStyle(currStyle);

                totalTx += item.getJmlTransaksi();
                grandOmset += item.getOmset();
                grandModal += item.getModal();
                grandLaba += item.getLaba();
            }

            // Summary Totals Row
            Row totalRow = sheet.createRow(rowIdx);
            totalRow.setHeightInPoints(24);

            Cell t0 = totalRow.createCell(0);
            t0.setCellValue("TOTAL KESELURUHAN");
            t0.setCellStyle(styles.totalLabelStyle);

            Cell t1 = totalRow.createCell(1);
            t1.setCellValue(totalTx);
            t1.setCellStyle(styles.totalIntStyle);

            Cell t2 = totalRow.createCell(2);
            t2.setCellValue(grandOmset);
            t2.setCellStyle(styles.totalCurrStyle);

            Cell t3 = totalRow.createCell(3);
            t3.setCellValue(grandModal);
            t3.setCellStyle(styles.totalCurrStyle);

            Cell t4 = totalRow.createCell(4);
            t4.setCellValue(grandLaba);
            t4.setCellStyle(styles.totalCurrStyle);

            autoFitColumns(sheet, headers.length);

            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
        }
    }

    /**
     * Export Laporan Barang Terlaris ke Excel (.xlsx)
     */
    public static void exportBarangTerlaris(File file, List<BarangTerlarisRow> data, LocalDate start, LocalDate end) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Barang Terlaris");
            enableGridLines(sheet);

            Styles styles = new Styles(workbook);

            createTitleBlock(sheet, styles, "LAPORAN PERINGKAT BARANG TERLARIS", 
                    "Periode: " + formatPeriode(start, end) + " | Warung Haryati", 4);

            int rowIdx = 3;
            Row headerRow = sheet.createRow(rowIdx++);
            headerRow.setHeightInPoints(26);
            String[] headers = {"Peringkat", "Nama Barang", "Kuantitas Terjual (Pcs)", "Total Omset", "Total Laba Kontribusi"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(styles.headerStyle);
            }

            int totalQty = 0;
            double grandOmset = 0;
            double grandLaba = 0;

            for (int i = 0; i < data.size(); i++) {
                BarangTerlarisRow item = data.get(i);
                Row row = sheet.createRow(rowIdx++);
                row.setHeightInPoints(20);
                boolean zebra = (i % 2 == 1);

                CellStyle textStyle = zebra ? styles.textZebra : styles.textNormal;
                CellStyle intStyle = zebra ? styles.intZebra : styles.intNormal;
                CellStyle currStyle = zebra ? styles.currZebra : styles.currNormal;

                Cell c0 = row.createCell(0);
                c0.setCellValue(item.getRank());
                c0.setCellStyle(intStyle);

                Cell c1 = row.createCell(1);
                c1.setCellValue(item.getNamaBarang());
                c1.setCellStyle(textStyle);

                Cell c2 = row.createCell(2);
                c2.setCellValue(item.getJmlTerjual());
                c2.setCellStyle(intStyle);

                Cell c3 = row.createCell(3);
                c3.setCellValue(item.getOmset());
                c3.setCellStyle(currStyle);

                Cell c4 = row.createCell(4);
                c4.setCellValue(item.getLaba());
                c4.setCellStyle(currStyle);

                totalQty += item.getJmlTerjual();
                grandOmset += item.getOmset();
                grandLaba += item.getLaba();
            }

            Row totalRow = sheet.createRow(rowIdx);
            totalRow.setHeightInPoints(24);

            Cell t0 = totalRow.createCell(0);
            t0.setCellValue("");
            t0.setCellStyle(styles.totalLabelStyle);

            Cell t1 = totalRow.createCell(1);
            t1.setCellValue("TOTAL KESELURUHAN");
            t1.setCellStyle(styles.totalLabelStyle);

            Cell t2 = totalRow.createCell(2);
            t2.setCellValue(totalQty);
            t2.setCellStyle(styles.totalIntStyle);

            Cell t3 = totalRow.createCell(3);
            t3.setCellValue(grandOmset);
            t3.setCellStyle(styles.totalCurrStyle);

            Cell t4 = totalRow.createCell(4);
            t4.setCellValue(grandLaba);
            t4.setCellStyle(styles.totalCurrStyle);

            autoFitColumns(sheet, headers.length);

            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
        }
    }

    /**
     * Export Laporan Analisis FP-Growth ke Excel (.xlsx)
     */
    public static void exportFpGrowth(File file, List<FpGrowthReportRow> data, LocalDate start, LocalDate end) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Analisis FP-Growth");
            enableGridLines(sheet);

            Styles styles = new Styles(workbook);

            createTitleBlock(sheet, styles, "LAPORAN ANALISIS POLA PEMBELIAN (FP-GROWTH)", 
                    "Periode: " + formatPeriode(start, end) + " | Warung Haryati", 5);

            int rowIdx = 3;
            Row headerRow = sheet.createRow(rowIdx++);
            headerRow.setHeightInPoints(26);
            String[] headers = {"Jika Membeli (Antecedents)", "Maka Membeli (Consequents)", "Support", "Confidence", "Lift Ratio", "💡 Rekomendasi Strategi Bisnis"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(styles.headerStyle);
            }

            for (int i = 0; i < data.size(); i++) {
                FpGrowthReportRow item = data.get(i);
                Row row = sheet.createRow(rowIdx++);
                row.setHeightInPoints(28);
                boolean zebra = (i % 2 == 1);

                CellStyle textStyle = zebra ? styles.textZebra : styles.textNormal;
                CellStyle pctStyle = zebra ? styles.pctZebra : styles.pctNormal;
                CellStyle decStyle = zebra ? styles.decZebra : styles.decNormal;
                CellStyle wrapStyle = zebra ? styles.wrapZebra : styles.wrapNormal;

                Cell c0 = row.createCell(0);
                c0.setCellValue(item.getAntecedents());
                c0.setCellStyle(textStyle);

                Cell c1 = row.createCell(1);
                c1.setCellValue(item.getConsequents());
                c1.setCellStyle(textStyle);

                Cell c2 = row.createCell(2);
                c2.setCellValue(item.getSupport());
                c2.setCellStyle(pctStyle);

                Cell c3 = row.createCell(3);
                c3.setCellValue(item.getConfidence());
                c3.setCellStyle(pctStyle);

                Cell c4 = row.createCell(4);
                c4.setCellValue(item.getLift());
                c4.setCellStyle(decStyle);

                Cell c5 = row.createCell(5);
                c5.setCellValue(item.getRekomendasi());
                c5.setCellStyle(wrapStyle);
            }

            autoFitColumns(sheet, headers.length);
            // Sesuaikan lebar kolom rekomendasi agar nyaman dibaca saat wrap text
            sheet.setColumnWidth(5, 55 * 256);

            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
        }
    }

    /**
     * Export Laporan Detail Penjualan ke Excel (.xlsx) dengan 2 Sheet yang To the Point:
     * Sheet 1: Ringkasan Transaksi (Sesuai tampilan UI, tanpa pengulangan item)
     * Sheet 2: Rincian Item Terjual (Detail per item jika dibutuhkan audit)
     */
    public static void exportDetailTransaksi(File file, List<LaporanRow> reportData, LocalDate start, LocalDate end) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Styles styles = new Styles(workbook);

            // --- SHEET 1: RINGKASAN TRANSAKSI (TO THE POINT) ---
            XSSFSheet sheet1 = workbook.createSheet("Ringkasan Transaksi");
            enableGridLines(sheet1);

            createTitleBlock(sheet1, styles, "RINGKASAN PENJUALAN PER TRANSAKSI", 
                    "Periode: " + formatPeriode(start, end) + " | Warung Haryati", 4);

            int rowIdx1 = 3;
            Row header1 = sheet1.createRow(rowIdx1++);
            header1.setHeightInPoints(26);
            String[] headers1 = {"No", "Tanggal", "ID Transaksi", "Daftar Item yang Dibeli", "Total Belanja"};
            for (int i = 0; i < headers1.length; i++) {
                Cell cell = header1.createCell(i);
                cell.setCellValue(headers1[i]);
                cell.setCellStyle(styles.headerStyle);
            }

            double grandTotalSummary = 0;
            for (int i = 0; i < reportData.size(); i++) {
                LaporanRow item = reportData.get(i);
                Row row = sheet1.createRow(rowIdx1++);
                row.setHeightInPoints(22);
                boolean zebra = (i % 2 == 1);

                CellStyle textStyle = zebra ? styles.textZebra : styles.textNormal;
                CellStyle intStyle = zebra ? styles.intZebra : styles.intNormal;
                CellStyle currStyle = zebra ? styles.currZebra : styles.currNormal;
                CellStyle wrapStyle = zebra ? styles.wrapZebra : styles.wrapNormal;

                Cell c0 = row.createCell(0);
                c0.setCellValue(i + 1);
                c0.setCellStyle(intStyle);

                Cell c1 = row.createCell(1);
                c1.setCellValue(item.getTanggal());
                c1.setCellStyle(textStyle);

                Cell c2 = row.createCell(2);
                c2.setCellValue(item.getId());
                c2.setCellStyle(textStyle);

                Cell c3 = row.createCell(3);
                c3.setCellValue(item.getItems());
                c3.setCellStyle(wrapStyle);

                Cell c4 = row.createCell(4);
                c4.setCellValue(item.getTotal());
                c4.setCellStyle(currStyle);

                grandTotalSummary += item.getTotal();
            }

            Row totalRow1 = sheet1.createRow(rowIdx1);
            totalRow1.setHeightInPoints(24);

            Cell t0_1 = totalRow1.createCell(0);
            t0_1.setCellValue("");
            t0_1.setCellStyle(styles.totalLabelStyle);

            Cell t1_1 = totalRow1.createCell(1);
            t1_1.setCellValue("");
            t1_1.setCellStyle(styles.totalLabelStyle);

            Cell t2_1 = totalRow1.createCell(2);
            t2_1.setCellValue("TOTAL TRANSAKSI: " + reportData.size());
            t2_1.setCellStyle(styles.totalLabelStyle);

            Cell t3_1 = totalRow1.createCell(3);
            t3_1.setCellValue("TOTAL PENDAPATAN KESELURUHAN");
            t3_1.setCellStyle(styles.totalLabelStyle);

            Cell t4_1 = totalRow1.createCell(4);
            t4_1.setCellValue(grandTotalSummary);
            t4_1.setCellStyle(styles.totalCurrStyle);

            autoFitColumns(sheet1, headers1.length);
            sheet1.setColumnWidth(3, 50 * 256); // Lebar kolom daftar item nyaman dibaca

            // --- SHEET 2: RINCIAN ITEM TERJUAL (CLEAN BREAKDOWN) ---
            XSSFSheet sheet2 = workbook.createSheet("Rincian Item Terjual");
            enableGridLines(sheet2);

            createTitleBlock(sheet2, styles, "RINCIAN DETAIL BARANG TERJUAL", 
                    "Periode: " + formatPeriode(start, end) + " | Warung Haryati", 8);

            int rowIdx2 = 3;
            Row header2 = sheet2.createRow(rowIdx2++);
            header2.setHeightInPoints(26);
            String[] headers2 = {"No", "ID Transaksi", "Tanggal", "Nama Barang", "Harga Beli", "Harga Jual", "Kuantitas", "Subtotal", "Laba"};
            for (int i = 0; i < headers2.length; i++) {
                Cell cell = header2.createCell(i);
                cell.setCellValue(headers2[i]);
                cell.setCellStyle(styles.headerStyle);
            }

            int count2 = 0;
            int totalQty2 = 0;
            double totalSubtotal2 = 0;
            double totalLaba2 = 0;

            if (start != null && end != null) {
                String sql = "SELECT t.id, t.tanggal, p.nama_barang, p.harga_beli, p.harga_jual, " +
                             "dt.kuantitas, dt.subtotal, dt.laba " +
                             "FROM detail_transaksi dt " +
                             "JOIN transaksi t ON dt.transaksi_id = t.id " +
                             "JOIN produk p ON dt.produk_id = p.id " +
                             "WHERE DATE(t.tanggal) BETWEEN ? AND ? " +
                             "ORDER BY t.tanggal DESC, t.id ASC";

                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setDate(1, Date.valueOf(start));
                    pstmt.setDate(2, Date.valueOf(end));
                    ResultSet rs = pstmt.executeQuery();

                    while (rs.next()) {
                        Row row = sheet2.createRow(rowIdx2++);
                        row.setHeightInPoints(20);
                        boolean zebra = (count2 % 2 == 1);

                        CellStyle textStyle = zebra ? styles.textZebra : styles.textNormal;
                        CellStyle intStyle = zebra ? styles.intZebra : styles.intNormal;
                        CellStyle currStyle = zebra ? styles.currZebra : styles.currNormal;

                        Cell c0 = row.createCell(0);
                        c0.setCellValue(count2 + 1);
                        c0.setCellStyle(intStyle);

                        Cell c1 = row.createCell(1);
                        c1.setCellValue(rs.getString("id"));
                        c1.setCellStyle(textStyle);

                        Cell c2 = row.createCell(2);
                        c2.setCellValue(com.warung.haryati.util.DateUtil.formatShort(rs.getDate("tanggal")));
                        c2.setCellStyle(textStyle);

                        Cell c3 = row.createCell(3);
                        c3.setCellValue(rs.getString("nama_barang"));
                        c3.setCellStyle(textStyle);

                        Cell c4 = row.createCell(4);
                        c4.setCellValue(rs.getDouble("harga_beli"));
                        c4.setCellStyle(currStyle);

                        Cell c5 = row.createCell(5);
                        c5.setCellValue(rs.getDouble("harga_jual"));
                        c5.setCellStyle(currStyle);

                        int qty = rs.getInt("kuantitas");
                        Cell c6 = row.createCell(6);
                        c6.setCellValue(qty);
                        c6.setCellStyle(intStyle);

                        double sub = rs.getDouble("subtotal");
                        Cell c7 = row.createCell(7);
                        c7.setCellValue(sub);
                        c7.setCellStyle(currStyle);

                        double laba = rs.getDouble("laba");
                        Cell c8 = row.createCell(8);
                        c8.setCellValue(laba);
                        c8.setCellStyle(currStyle);

                        count2++;
                        totalQty2 += qty;
                        totalSubtotal2 += sub;
                        totalLaba2 += laba;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            Row totalRow2 = sheet2.createRow(rowIdx2);
            totalRow2.setHeightInPoints(24);

            for (int i = 0; i <= 5; i++) {
                Cell c = totalRow2.createCell(i);
                c.setCellStyle(styles.totalLabelStyle);
                if (i == 3) c.setCellValue("TOTAL KESELURUHAN (" + count2 + " item)");
            }

            Cell t6 = totalRow2.createCell(6);
            t6.setCellValue(totalQty2);
            t6.setCellStyle(styles.totalIntStyle);

            Cell t7 = totalRow2.createCell(7);
            t7.setCellValue(totalSubtotal2);
            t7.setCellStyle(styles.totalCurrStyle);

            Cell t8 = totalRow2.createCell(8);
            t8.setCellValue(totalLaba2);
            t8.setCellStyle(styles.totalCurrStyle);

            autoFitColumns(sheet2, headers2.length);

            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
        }
    }

    private static void enableGridLines(XSSFSheet sheet) {
        sheet.setDisplayGridlines(true);
        sheet.setPrintGridlines(true);
    }

    private static String formatPeriode(LocalDate start, LocalDate end) {
        if (start != null && end != null) {
            return start.format(DATE_FMT) + " s/d " + end.format(DATE_FMT);
        }
        return "Semua Waktu";
    }

    private static void createTitleBlock(XSSFSheet sheet, Styles styles, String titleText, String subtitleText, int maxColIndex) {
        // Row 0: Title
        Row r0 = sheet.createRow(0);
        r0.setHeightInPoints(28);
        Cell cellTitle = r0.createCell(0);
        cellTitle.setCellValue(titleText);
        cellTitle.setCellStyle(styles.titleStyle);

        for (int i = 1; i <= maxColIndex; i++) {
            Cell c = r0.createCell(i);
            c.setCellStyle(styles.titleStyle);
        }
        if (maxColIndex > 0) {
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, maxColIndex));
        }

        // Row 1: Subtitle / Periode
        Row r1 = sheet.createRow(1);
        r1.setHeightInPoints(20);
        Cell cellSub = r1.createCell(0);
        cellSub.setCellValue(subtitleText);
        cellSub.setCellStyle(styles.subtitleStyle);

        for (int i = 1; i <= maxColIndex; i++) {
            Cell c = r1.createCell(i);
            c.setCellStyle(styles.subtitleStyle);
        }
        if (maxColIndex > 0) {
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, maxColIndex));
        }

        // Row 2: Empty row separator
        sheet.createRow(2).setHeightInPoints(10);
    }

    private static void autoFitColumns(XSSFSheet sheet, int colCount) {
        for (int i = 0; i < colCount; i++) {
            sheet.autoSizeColumn(i);
            int currentWidth = sheet.getColumnWidth(i);
            // Tambahkan padding extra agar angka/teks tidak rapat atau jadi ###
            int paddedWidth = currentWidth + (3 * 256);
            if (paddedWidth < (14 * 256)) {
                paddedWidth = 14 * 256; // Lebar minimum 14 karakter
            }
            if (paddedWidth > (60 * 256)) {
                paddedWidth = 60 * 256; // Batas maksimum
            }
            sheet.setColumnWidth(i, paddedWidth);
        }
    }

    /**
     * Kumpulan cell styles yang estetis dan rapi
     */
    private static class Styles {
        final CellStyle titleStyle;
        final CellStyle subtitleStyle;
        final CellStyle headerStyle;
        final CellStyle textNormal, textZebra;
        final CellStyle intNormal, intZebra;
        final CellStyle currNormal, currZebra;
        final CellStyle pctNormal, pctZebra;
        final CellStyle decNormal, decZebra;
        final CellStyle wrapNormal, wrapZebra;
        final CellStyle totalLabelStyle, totalIntStyle, totalCurrStyle;

        Styles(XSSFWorkbook wb) {
            DataFormat df = wb.createDataFormat();

            // Colors
            XSSFColor headerBg = new XSSFColor(new byte[]{(byte)0x1F, (byte)0x4E, (byte)0x3D}, null); // Elegant Deep Emerald
            XSSFColor titleBg = new XSSFColor(new byte[]{(byte)0x14, (byte)0x36, (byte)0x2A}, null);  // Darker Emerald for title block
            XSSFColor zebraBg = new XSSFColor(new byte[]{(byte)0xF5, (byte)0xF9, (byte)0xF6}, null);  // Soft Emerald Tint for zebra
            XSSFColor totalBg = new XSSFColor(new byte[]{(byte)0xE8, (byte)0xF2, (byte)0xEC}, null);  // Highlight for total

            // Fonts
            Font fontTitle = wb.createFont();
            fontTitle.setFontName("Calibri");
            fontTitle.setFontHeightInPoints((short) 15);
            fontTitle.setBold(true);
            fontTitle.setColor(IndexedColors.WHITE.getIndex());

            Font fontSub = wb.createFont();
            fontSub.setFontName("Calibri");
            fontSub.setFontHeightInPoints((short) 11);
            fontSub.setItalic(true);
            fontSub.setColor(IndexedColors.WHITE.getIndex());

            Font fontHeader = wb.createFont();
            fontHeader.setFontName("Calibri");
            fontHeader.setFontHeightInPoints((short) 11);
            fontHeader.setBold(true);
            fontHeader.setColor(IndexedColors.WHITE.getIndex());

            Font fontData = wb.createFont();
            fontData.setFontName("Calibri");
            fontData.setFontHeightInPoints((short) 11);

            Font fontTotal = wb.createFont();
            fontTotal.setFontName("Calibri");
            fontTotal.setFontHeightInPoints((short) 11);
            fontTotal.setBold(true);

            // Title Style
            titleStyle = wb.createCellStyle();
            titleStyle.setFont(fontTitle);
            ((XSSFCellStyle) titleStyle).setFillForegroundColor(titleBg);
            titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // Subtitle Style
            subtitleStyle = wb.createCellStyle();
            subtitleStyle.setFont(fontSub);
            ((XSSFCellStyle) subtitleStyle).setFillForegroundColor(titleBg);
            subtitleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            subtitleStyle.setAlignment(HorizontalAlignment.CENTER);
            subtitleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // Header Style
            headerStyle = wb.createCellStyle();
            headerStyle.setFont(fontHeader);
            ((XSSFCellStyle) headerStyle).setFillForegroundColor(headerBg);
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            setThinBorders(headerStyle);

            // Base Data Styles
            textNormal = createBaseStyle(wb, fontData, false, zebraBg);
            textNormal.setAlignment(HorizontalAlignment.LEFT);

            textZebra = createBaseStyle(wb, fontData, true, zebraBg);
            textZebra.setAlignment(HorizontalAlignment.LEFT);

            intNormal = createBaseStyle(wb, fontData, false, zebraBg);
            intNormal.setAlignment(HorizontalAlignment.CENTER);
            intNormal.setDataFormat(df.getFormat("#,##0"));

            intZebra = createBaseStyle(wb, fontData, true, zebraBg);
            intZebra.setAlignment(HorizontalAlignment.CENTER);
            intZebra.setDataFormat(df.getFormat("#,##0"));

            currNormal = createBaseStyle(wb, fontData, false, zebraBg);
            currNormal.setAlignment(HorizontalAlignment.RIGHT);
            currNormal.setDataFormat(df.getFormat("Rp #,##0"));

            currZebra = createBaseStyle(wb, fontData, true, zebraBg);
            currZebra.setAlignment(HorizontalAlignment.RIGHT);
            currZebra.setDataFormat(df.getFormat("Rp #,##0"));

            pctNormal = createBaseStyle(wb, fontData, false, zebraBg);
            pctNormal.setAlignment(HorizontalAlignment.CENTER);
            pctNormal.setDataFormat(df.getFormat("0.0%"));

            pctZebra = createBaseStyle(wb, fontData, true, zebraBg);
            pctZebra.setAlignment(HorizontalAlignment.CENTER);
            pctZebra.setDataFormat(df.getFormat("0.0%"));

            decNormal = createBaseStyle(wb, fontData, false, zebraBg);
            decNormal.setAlignment(HorizontalAlignment.CENTER);
            decNormal.setDataFormat(df.getFormat("0.00"));

            decZebra = createBaseStyle(wb, fontData, true, zebraBg);
            decZebra.setAlignment(HorizontalAlignment.CENTER);
            decZebra.setDataFormat(df.getFormat("0.00"));

            wrapNormal = createBaseStyle(wb, fontData, false, zebraBg);
            wrapNormal.setAlignment(HorizontalAlignment.LEFT);
            wrapNormal.setWrapText(true);

            wrapZebra = createBaseStyle(wb, fontData, true, zebraBg);
            wrapZebra.setAlignment(HorizontalAlignment.LEFT);
            wrapZebra.setWrapText(true);

            // Total Styles
            totalLabelStyle = wb.createCellStyle();
            totalLabelStyle.setFont(fontTotal);
            ((XSSFCellStyle) totalLabelStyle).setFillForegroundColor(totalBg);
            totalLabelStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            totalLabelStyle.setAlignment(HorizontalAlignment.LEFT);
            totalLabelStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            setTotalBorders(totalLabelStyle);

            totalIntStyle = wb.createCellStyle();
            totalIntStyle.setFont(fontTotal);
            ((XSSFCellStyle) totalIntStyle).setFillForegroundColor(totalBg);
            totalIntStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            totalIntStyle.setAlignment(HorizontalAlignment.CENTER);
            totalIntStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            totalIntStyle.setDataFormat(df.getFormat("#,##0"));
            setTotalBorders(totalIntStyle);

            totalCurrStyle = wb.createCellStyle();
            totalCurrStyle.setFont(fontTotal);
            ((XSSFCellStyle) totalCurrStyle).setFillForegroundColor(totalBg);
            totalCurrStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            totalCurrStyle.setAlignment(HorizontalAlignment.RIGHT);
            totalCurrStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            totalCurrStyle.setDataFormat(df.getFormat("Rp #,##0"));
            setTotalBorders(totalCurrStyle);
        }

        private CellStyle createBaseStyle(XSSFWorkbook wb, Font font, boolean zebra, XSSFColor zebraBg) {
            CellStyle cs = wb.createCellStyle();
            cs.setFont(font);
            cs.setVerticalAlignment(VerticalAlignment.CENTER);
            if (zebra) {
                ((XSSFCellStyle) cs).setFillForegroundColor(zebraBg);
                cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            }
            setThinBorders(cs);
            return cs;
        }

        private void setThinBorders(CellStyle cs) {
            cs.setBorderTop(BorderStyle.THIN);
            cs.setBorderBottom(BorderStyle.THIN);
            cs.setBorderLeft(BorderStyle.THIN);
            cs.setBorderRight(BorderStyle.THIN);
            cs.setTopBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
            cs.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
            cs.setLeftBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
            cs.setRightBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        }

        private void setTotalBorders(CellStyle cs) {
            cs.setBorderTop(BorderStyle.THIN);
            cs.setBorderBottom(BorderStyle.DOUBLE);
            cs.setBorderLeft(BorderStyle.THIN);
            cs.setBorderRight(BorderStyle.THIN);
            cs.setTopBorderColor(IndexedColors.BLACK.getIndex());
            cs.setBottomBorderColor(IndexedColors.BLACK.getIndex());
            cs.setLeftBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
            cs.setRightBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        }
    }
}
