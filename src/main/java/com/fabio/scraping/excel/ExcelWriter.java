package com.fabio.scraping.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class ExcelWriter
{
    private static Logger logger = LoggerFactory.getLogger(ExcelWriter.class);

    public void writeExcel(String filename, ExcelData data) {

        Path output = Paths.get("output");

        try {
            Files.createDirectory(output);
        } catch (IOException e) {
            // do nothing
        }

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Sheet1");

        int rowNum = 0;

        for (List<Object> datatype : data.getData()) {
            Row row = sheet.createRow(rowNum++);
            int colNum = 0;
            for (Object field : datatype) {
                Cell cell = row.createCell(colNum++);
                if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else if (field instanceof Integer) {
                    cell.setCellValue((Integer) field);
                }
            }
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

        try {
            FileOutputStream outputStream = new FileOutputStream(output.toString()+"/"+formatter.format(LocalDateTime.now())+"-"+filename+".xlsx");
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            logger.error("Error while creating excel", e);
        }
    }
}
