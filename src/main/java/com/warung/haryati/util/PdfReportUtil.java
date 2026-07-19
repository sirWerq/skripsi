package com.warung.haryati.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.warung.haryati.controller.LaporanController.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PdfReportUtil {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void exportLabaRugi(File file, List<LabaRugiRow> data, LocalDate start, LocalDate end) throws IOException, DocumentException {
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        PdfWriter.getInstance(document, new FileOutputStream(file));
        document.open();

        addKopSurat(document);
        addReportTitle(document, "LAPORAN LABA RUGI & OMSET PENJUALAN", start, end);

        PdfPTable table = new PdfPTable(new float[]{1f, 2.3f, 1.8f, 2.2f, 2.2f, 2.5f});
        table.setWidthPercentage(100);

        addHeaderCell(table, "No");
        addHeaderCell(table, "Tanggal");
        addHeaderCell(table, "Jml Transaksi");
        addHeaderCell(table, "Omset Penjualan");
        addHeaderCell(table, "Modal (HPP)");
        addHeaderCell(table, "Laba Bersih");

        int totalTx = 0;
        double grandOmset = 0;
        double grandModal = 0;
        double grandLaba = 0;

        for (int i = 0; i < data.size(); i++) {
            LabaRugiRow item = data.get(i);
            addDataCell(table, String.valueOf(i + 1), Element.ALIGN_CENTER, false, false);
            addDataCell(table, item.getTanggal(), Element.ALIGN_CENTER, false, false);
            addDataCell(table, item.getJmlTransaksiStr(), Element.ALIGN_CENTER, false, false);
            addDataCell(table, item.getOmsetStr(), Element.ALIGN_RIGHT, false, false);
            addDataCell(table, item.getModalStr(), Element.ALIGN_RIGHT, false, false);
            addDataCell(table, item.getLabaStr(), Element.ALIGN_RIGHT, false, false);

            totalTx += item.getJmlTransaksi();
            grandOmset += item.getOmset();
            grandModal += item.getModal();
            grandLaba += item.getLaba();
        }

        addDataCell(table, "", Element.ALIGN_CENTER, true, true);
        addDataCell(table, "TOTAL KESELURUHAN", Element.ALIGN_LEFT, true, true);
        addDataCell(table, String.valueOf(totalTx), Element.ALIGN_CENTER, true, true);
        addDataCell(table, CurrencyUtil.format(grandOmset), Element.ALIGN_RIGHT, true, true);
        addDataCell(table, CurrencyUtil.format(grandModal), Element.ALIGN_RIGHT, true, true);
        addDataCell(table, CurrencyUtil.format(grandLaba), Element.ALIGN_RIGHT, true, true);

        document.add(table);
        addSignatureBlock(document);
        document.close();
    }

    public static void exportBarangTerlaris(File file, List<BarangTerlarisRow> data, LocalDate start, LocalDate end) throws IOException, DocumentException {
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        PdfWriter.getInstance(document, new FileOutputStream(file));
        document.open();

        addKopSurat(document);
        addReportTitle(document, "LAPORAN PERINGKAT BARANG TERLARIS (TOP SELLING)", start, end);

        PdfPTable table = new PdfPTable(new float[]{1f, 4.5f, 1.8f, 2.3f, 2.4f});
        table.setWidthPercentage(100);

        addHeaderCell(table, "No");
        addHeaderCell(table, "Nama Barang");
        addHeaderCell(table, "Terjual (Pcs)");
        addHeaderCell(table, "Total Omset");
        addHeaderCell(table, "Total Laba");

        int totalQty = 0;
        double grandOmset = 0;
        double grandLaba = 0;

        for (int i = 0; i < data.size(); i++) {
            BarangTerlarisRow item = data.get(i);
            addDataCell(table, String.valueOf(i + 1), Element.ALIGN_CENTER, false, false);
            addDataCell(table, item.getNamaBarang(), Element.ALIGN_LEFT, false, false);
            addDataCell(table, item.getJmlTerjualStr(), Element.ALIGN_CENTER, false, false);
            addDataCell(table, item.getOmsetStr(), Element.ALIGN_RIGHT, false, false);
            addDataCell(table, item.getLabaStr(), Element.ALIGN_RIGHT, false, false);

            totalQty += item.getJmlTerjual();
            grandOmset += item.getOmset();
            grandLaba += item.getLaba();
        }

        addDataCell(table, "", Element.ALIGN_CENTER, true, true);
        addDataCell(table, "TOTAL KESELURUHAN", Element.ALIGN_LEFT, true, true);
        addDataCell(table, String.valueOf(totalQty), Element.ALIGN_CENTER, true, true);
        addDataCell(table, CurrencyUtil.format(grandOmset), Element.ALIGN_RIGHT, true, true);
        addDataCell(table, CurrencyUtil.format(grandLaba), Element.ALIGN_RIGHT, true, true);

        document.add(table);
        addSignatureBlock(document);
        document.close();
    }

    public static void exportFpGrowth(File file, List<FpGrowthReportRow> data, LocalDate start, LocalDate end) throws IOException, DocumentException {
        Document document = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);
        PdfWriter.getInstance(document, new FileOutputStream(file));
        document.open();

        addKopSurat(document);
        addReportTitle(document, "LAPORAN ANALISIS FP-GROWTH", start, end);

        PdfPTable table = new PdfPTable(new float[]{0.8f, 2.5f, 2.5f, 1.2f, 1.2f, 1f, 3.8f});
        table.setWidthPercentage(100);

        addHeaderCell(table, "No");
        addHeaderCell(table, "Jika Membeli");
        addHeaderCell(table, "Maka Membeli");
        addHeaderCell(table, "Support");
        addHeaderCell(table, "Confidence");
        addHeaderCell(table, "Lift");
        addHeaderCell(table, "Rekomendasi Strategi Bisnis");

        for (int i = 0; i < data.size(); i++) {
            FpGrowthReportRow item = data.get(i);
            addDataCell(table, String.valueOf(i + 1), Element.ALIGN_CENTER, false, false);
            addDataCell(table, item.getAntecedents(), Element.ALIGN_LEFT, false, false);
            addDataCell(table, item.getConsequents(), Element.ALIGN_LEFT, false, false);
            addDataCell(table, item.getSupportStr(), Element.ALIGN_CENTER, false, false);
            addDataCell(table, item.getConfidenceStr(), Element.ALIGN_CENTER, false, false);
            addDataCell(table, item.getLiftStr(), Element.ALIGN_CENTER, false, false);
            addDataCell(table, item.getRekomendasi(), Element.ALIGN_LEFT, false, false);
        }

        document.add(table);
        addSignatureBlock(document);
        document.close();
    }

    public static void exportDetailTransaksi(File file, List<LaporanRow> reportData, LocalDate start, LocalDate end) throws IOException, DocumentException {
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        PdfWriter.getInstance(document, new FileOutputStream(file));
        document.open();

        addKopSurat(document);
        addReportTitle(document, "LAPORAN RIWAYAT & DETAIL TRANSAKSI PENJUALAN", start, end);

        PdfPTable table = new PdfPTable(new float[]{1f, 2f, 2.2f, 4.5f, 2.3f});
        table.setWidthPercentage(100);

        addHeaderCell(table, "No");
        addHeaderCell(table, "Tanggal");
        addHeaderCell(table, "ID Transaksi");
        addHeaderCell(table, "Rincian Item yang Dibeli");
        addHeaderCell(table, "Total Belanja");

        double grandTotal = 0;
        for (int i = 0; i < reportData.size(); i++) {
            LaporanRow item = reportData.get(i);
            addDataCell(table, String.valueOf(i + 1), Element.ALIGN_CENTER, false, false);
            addDataCell(table, item.getTanggal(), Element.ALIGN_CENTER, false, false);
            addDataCell(table, item.getId(), Element.ALIGN_CENTER, false, false);
            addDataCell(table, item.getItems(), Element.ALIGN_LEFT, false, false);
            addDataCell(table, item.getTotalStr(), Element.ALIGN_RIGHT, false, false);

            grandTotal += item.getTotal();
        }

        addDataCell(table, "", Element.ALIGN_CENTER, true, true);
        addDataCell(table, "TOTAL KESELURUHAN", Element.ALIGN_LEFT, true, true);
        addDataCell(table, "", Element.ALIGN_CENTER, true, true);
        addDataCell(table, "", Element.ALIGN_CENTER, true, true);
        addDataCell(table, CurrencyUtil.format(grandTotal), Element.ALIGN_RIGHT, true, true);

        document.add(table);
        addSignatureBlock(document);
        document.close();
    }

    public static void exportDataProduk(File file, List<DataProdukRow> data) throws IOException, DocumentException {
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        PdfWriter.getInstance(document, new FileOutputStream(file));
        document.open();

        addKopSurat(document);
        addReportTitle(document, "LAPORAN DATA MASTER PRODUK", null, null);

        PdfPTable table = new PdfPTable(new float[]{1f, 2f, 5.5f, 2.75f, 2.75f});
        table.setWidthPercentage(100);

        addHeaderCell(table, "No");
        addHeaderCell(table, "ID Produk");
        addHeaderCell(table, "Nama Barang");
        addHeaderCell(table, "Harga Beli");
        addHeaderCell(table, "Harga Jual");

        for (int i = 0; i < data.size(); i++) {
            DataProdukRow item = data.get(i);
            addDataCell(table, String.valueOf(i + 1), Element.ALIGN_CENTER, false, false);
            addDataCell(table, item.getId(), Element.ALIGN_CENTER, false, false);
            addDataCell(table, item.getNamaBarang(), Element.ALIGN_LEFT, false, false);
            addDataCell(table, item.getHargaBeliStr(), Element.ALIGN_RIGHT, false, false);
            addDataCell(table, item.getHargaJualStr(), Element.ALIGN_RIGHT, false, false);
        }

        addDataCell(table, "", Element.ALIGN_CENTER, true, true);
        addDataCell(table, "", Element.ALIGN_CENTER, true, true);
        addDataCell(table, "TOTAL (" + data.size() + " Item)", Element.ALIGN_LEFT, true, true);
        addDataCell(table, "", Element.ALIGN_RIGHT, true, true);
        addDataCell(table, "", Element.ALIGN_RIGHT, true, true);

        document.add(table);
        addSignatureBlock(document);
        document.close();
    }

    private static void addKopSurat(Document document) throws DocumentException, IOException {
        PdfPTable kopTable = new PdfPTable(new float[]{1.5f, 6.5f});
        kopTable.setWidthPercentage(100);
        kopTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        PdfPCell logoCell = new PdfPCell();
        logoCell.setBorder(Rectangle.NO_BORDER);
        logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        try {
            Image logo = null;
            java.net.URL logoUrl = PdfReportUtil.class.getResource("/images/logo.png");
            if (logoUrl != null) {
                logo = Image.getInstance(logoUrl);
            } else {
                File fileLogo = new File("src/main/resources/images/logo.png");
                if (!fileLogo.exists()) fileLogo = new File("images/logo.png");
                if (fileLogo.exists()) {
                    logo = Image.getInstance(fileLogo.getAbsolutePath());
                }
            }
            if (logo != null) {
                logo.scaleToFit(65, 65);
                logoCell.addElement(logo);
            }
        } catch (Exception e) {
            System.err.println("Gagal memuat logo untuk PDF KOP Surat: " + e.getMessage());
        }
        kopTable.addCell(logoCell);

        PdfPCell textCell = new PdfPCell();
        textCell.setBorder(Rectangle.NO_BORDER);
        textCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        textCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        Font fontHeader1 = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD, BaseColor.DARK_GRAY);
        Font fontHeader2 = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.BLACK);
        Font fontHeader3 = new Font(Font.FontFamily.HELVETICA, 9.5f, Font.NORMAL, BaseColor.DARK_GRAY);

        Paragraph p1 = new Paragraph("Sistem Analisis Penjualan & Market Basket Analysis", fontHeader1);
        p1.setAlignment(Element.ALIGN_CENTER);
        Paragraph p2 = new Paragraph("Warung Haryati", fontHeader2);
        p2.setAlignment(Element.ALIGN_CENTER);
        Paragraph p3 = new Paragraph("Jln. Tegalparang Selatan III RT 007/004 No. 120, Tegalparang, Kec. Mampang Prapatan, Jakarta Selatan 12790", fontHeader3);
        p3.setAlignment(Element.ALIGN_CENTER);

        textCell.addElement(p1);
        textCell.addElement(p2);
        textCell.addElement(p3);
        kopTable.addCell(textCell);

        document.add(kopTable);
        
        LineSeparator ls = new LineSeparator(2.5f, 100f, BaseColor.BLACK, Element.ALIGN_CENTER, -5f);
        document.add(new Chunk(ls));
        
        document.add(new Paragraph(" "));
    }

    private static void addReportTitle(Document document, String title, LocalDate start, LocalDate end) throws DocumentException {
        Font fontTitle = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.BLACK);
        Paragraph pTitle = new Paragraph(title, fontTitle);
        pTitle.setAlignment(Element.ALIGN_CENTER);
        document.add(pTitle);

        if (start != null && end != null) {
            Font fontPeriode = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.DARK_GRAY);
            Paragraph pPeriode = new Paragraph("Periode: " + formatPeriode(start, end), fontPeriode);
            pPeriode.setAlignment(Element.ALIGN_CENTER);
            document.add(pPeriode);
        }
        document.add(new Paragraph(" "));
    }

    private static void addHeaderCell(PdfPTable table, String text) {
        Font fontHeader = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.BLACK);
        PdfPCell cell = new PdfPCell(new Phrase(text, fontHeader));
        cell.setBackgroundColor(new BaseColor(235, 238, 245));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPaddingTop(6f);
        cell.setPaddingBottom(6f);
        cell.setPaddingLeft(4f);
        cell.setPaddingRight(4f);
        table.addCell(cell);
    }

    private static void addDataCell(PdfPTable table, String text, int align, boolean isBold, boolean isSummary) {
        Font fontData = new Font(Font.FontFamily.HELVETICA, 9.5f, isBold ? Font.BOLD : Font.NORMAL, BaseColor.BLACK);
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "", fontData));
        if (isSummary) {
            cell.setBackgroundColor(new BaseColor(245, 247, 250));
        }
        cell.setHorizontalAlignment(align);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPaddingTop(5f);
        cell.setPaddingBottom(5f);
        cell.setPaddingLeft(5f);
        cell.setPaddingRight(5f);
        table.addCell(cell);
    }

    private static void addSignatureBlock(Document document) throws DocumentException {
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));

        PdfPTable signTable = new PdfPTable(new float[]{4.5f, 3.5f});
        signTable.setWidthPercentage(100);

        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);
        signTable.addCell(leftCell);

        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setHorizontalAlignment(Element.ALIGN_CENTER);

        Font fontSign = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
        Font fontSignBold = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.BLACK);

        Paragraph pDate = new Paragraph("Jakarta, " + formatTanggalIndonesia(LocalDate.now()), fontSign);
        pDate.setAlignment(Element.ALIGN_CENTER);
        Paragraph pEntity = new Paragraph("Warung Haryati", fontSign);
        pEntity.setAlignment(Element.ALIGN_CENTER);

        Paragraph pSpace = new Paragraph("\n\n\n\n", fontSign);
        Paragraph pName = new Paragraph("( _________________________ )", fontSignBold);
        pName.setAlignment(Element.ALIGN_CENTER);

        rightCell.addElement(pDate);
        rightCell.addElement(pEntity);
        rightCell.addElement(pSpace);
        rightCell.addElement(pName);

        signTable.addCell(rightCell);
        document.add(signTable);
    }

    private static String formatPeriode(LocalDate start, LocalDate end) {
        if (start != null && end != null) {
            return start.format(DATE_FMT) + " s/d " + end.format(DATE_FMT);
        }
        return "Semua Periode";
    }

    private static String formatTanggalIndonesia(LocalDate date) {
        if (date == null) date = LocalDate.now();
        String[] hari = {"Senin", "Selasa", "Rabu", "Kamis", "Jum'at", "Sabtu", "Minggu"};
        String[] bulan = {"Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"};

        String namaHari = hari[date.getDayOfWeek().getValue() - 1];
        String namaBulan = bulan[date.getMonthValue() - 1];
        return namaHari + ", " + date.getDayOfMonth() + " " + namaBulan + " " + date.getYear();
    }
}
