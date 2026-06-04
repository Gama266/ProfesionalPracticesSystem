/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logic.util;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author gamal
 */

public class ReportManager {

    public void generateDocument(File destinationFile, String reportType) throws IOException {
        String templateName = getTemplateFileName(reportType);
        
        String resourcePath = "templates/" + templateName; 
        
        try (InputStream templateStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath);
             FileOutputStream outputStream = new FileOutputStream(destinationFile)) {

            if (templateStream == null) {
          
                try (InputStream fallbackStream = ReportManager.class.getClassLoader().getResourceAsStream(resourcePath)) {
                    if (fallbackStream == null) {
                        throw new IOException("La plantilla " + templateName + " no se encontró en los recursos.");
                    }
                    copyStream(fallbackStream, outputStream);
                }
            } else {
                copyStream(templateStream, outputStream);
            }
            
            outputStream.flush();
        }
    }
    private void copyStream(InputStream input, FileOutputStream output) throws IOException {
        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    private String getTemplateFileName(String reportType) {
        String fileName = "Reporte-Mensual.docx";
        
        if (reportType.equals("Final")) {
            fileName = "PRAIS-05-Reporte-FINAL.docx";
        } else if (reportType.equals("Parcial")) {
            fileName = "PRAIS-P-02-Reporte-parcial-100-200l.docx";
        }
        
        return fileName;
    }
}