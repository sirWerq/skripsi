package com.warung.haryati.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.warung.haryati.model.AnalisisResult;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class FPGrowthService {
    
    private final ObjectMapper mapper = new ObjectMapper();

    public AnalisisResult runAnalysis(double minSupport, double minConfidence) throws Exception {
        // Gunakan path absolut untuk venv agar tidak memanggil python sistem
        File venvPython = new File("python/venv/Scripts/python.exe");
        File scriptFile = new File("python/fp_growth.py");

        if (!venvPython.exists()) {
            throw new Exception("Python Virtual Environment tidak ditemukan di: " + venvPython.getAbsolutePath());
        }

        ProcessBuilder pb = new ProcessBuilder(
            venvPython.getAbsolutePath(), 
            scriptFile.getAbsolutePath(), 
            String.valueOf(minSupport), 
            String.valueOf(minConfidence)
        );
        
        Process process = pb.start();
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line);
        }
        
        BufferedReader errReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        StringBuilder errorOutput = new StringBuilder();
        while ((line = errReader.readLine()) != null) {
            errorOutput.append(line).append("\n");
            System.out.println("Python Log: " + line); // Cetak langsung ke terminal Java
        }
        
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new Exception("Python Error (Exit " + exitCode + "):\n" + errorOutput.toString());
        }
        
        String resultJson = output.toString().trim();
        if (resultJson.isEmpty()) {
            throw new Exception("Python tidak memberikan hasil. Error:\n" + errorOutput.toString());
        }

        // Cari posisi kurung kurawal pertama '{' untuk membuang Warning teks di depannya
        int jsonStartIndex = resultJson.indexOf("{");
        if (jsonStartIndex == -1) {
            throw new Exception("Format output Python tidak valid (Bukan JSON). Output:\n" + resultJson);
        }
        resultJson = resultJson.substring(jsonStartIndex);
        
        return mapper.readValue(resultJson, AnalisisResult.class);
    }
}
