package com.warung.haryati.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.warung.haryati.model.AnalisisResult;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class FPGrowthService {
    
    private static AnalisisResult latestResult;
    private final ObjectMapper mapper = new ObjectMapper();

    public static AnalisisResult getLatestResult() {
        return latestResult;
    }

    public AnalisisResult runAnalysis(double minSupport, double minConfidence) throws Exception {
        // Menggunakan user.dir agar path dinamis menyesuaikan lokasi jalannya aplikasi
        String appDir = System.getProperty("user.dir");
        File pythonExe = new File(appDir, "fp_growth.exe");

        if (!pythonExe.exists()) {
            throw new Exception("File executable Python (FP-Growth) tidak ditemukan di: " + pythonExe.getAbsolutePath());
        }

        // ProcessBuilder memanggil EXE Python dan argumennya
        ProcessBuilder pb = new ProcessBuilder(
            pythonExe.getAbsolutePath(), 
            String.valueOf(minSupport), 
            String.valueOf(minConfidence)
        );
        
        Process process = pb.start();
        
        // Membaca output (JSON) dari Python
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line);
        }
        
        // Membaca error/log dari Python
        BufferedReader errReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        StringBuilder errorOutput = new StringBuilder();
        while ((line = errReader.readLine()) != null) {
            errorOutput.append(line).append("\n");
            System.out.println("Python Log: " + line);
        }
        
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new Exception("Python Error (Exit " + exitCode + "):\n" + errorOutput.toString());
        }
        
        String resultJson = output.toString().trim();
        if (resultJson.isEmpty()) {
            throw new Exception("Python tidak memberikan hasil. Error:\n" + errorOutput.toString());
        }

        // Memastikan hanya mengambil bagian JSON jika ada print lain sebelumnya
        int jsonStartIndex = resultJson.indexOf("{");
        if (jsonStartIndex == -1) {
            throw new Exception("Format output Python tidak valid (Bukan JSON). Output:\n" + resultJson);
        }
        resultJson = resultJson.substring(jsonStartIndex);
        
        AnalisisResult res = mapper.readValue(resultJson, AnalisisResult.class);
        latestResult = res;
        return res;
    }
}