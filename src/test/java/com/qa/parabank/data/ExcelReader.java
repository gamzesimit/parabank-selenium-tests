package com.qa.parabank.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Reads test data from an Excel sheet on the classpath with Apache POI. The first row
 * is a header and is skipped. Every cell is read as the text the sheet shows, so an
 * amount typed as 10.50 arrives as "10.50" and not as 10.5.
 */
public final class ExcelReader {

    private ExcelReader() {
    }

    public static Object[][] rows(String resource, String sheetName) {
        try (InputStream in = ExcelReader.class.getClassLoader().getResourceAsStream(resource)) {
            if (in == null) {
                throw new IllegalArgumentException("Test data file not found: " + resource);
            }
            try (Workbook book = new XSSFWorkbook(in)) {
                Sheet sheet = book.getSheet(sheetName);
                if (sheet == null) {
                    throw new IllegalArgumentException("Sheet " + sheetName + " not found in " + resource);
                }
                return read(sheet);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read " + resource, e);
        }
    }

    private static Object[][] read(Sheet sheet) {
        DataFormatter format = new DataFormatter();
        int columns = sheet.getRow(0).getLastCellNum();
        List<Object[]> rows = new ArrayList<>();
        for (int r = 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (row == null) {
                continue;
            }
            Object[] values = new Object[columns];
            for (int c = 0; c < columns; c++) {
                values[c] = format.formatCellValue(row.getCell(c)).trim();
            }
            rows.add(values);
        }
        return rows.toArray(new Object[0][]);
    }
}
