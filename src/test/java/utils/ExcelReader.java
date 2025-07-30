package utils;

import java.io.File;
import java.io.FileInputStream;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.*;

public class ExcelReader {

    // Method 1: Returns a full row of card details as key-value pairs from second row
    public Map<String, String> getCardDetails(String filePath, String sheetName) {
        return getCardDetails(filePath, sheetName, 1); // Default to second row (index 1)
    }

    // Overloaded Method 1: Returns a specific row of card details as key-value pairs
    public Map<String, String> getCardDetails(String filePath, String sheetName, int rowNumber) {
        Map<String, String> data = new HashMap<>();

        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            
            if (sheet == null) {
                System.out.println("❌ Sheet not found: " + sheetName);
                return data;
            }
            
            Row headerRow = sheet.getRow(0); // Header is always at row 0
            Row dataRow = sheet.getRow(rowNumber); // Control which data row to read

            if (headerRow == null) {
                System.out.println("❌ Header row not found");
                return data;
            }
            
            if (dataRow == null) {
                System.out.println("❌ Data row not found at index: " + rowNumber);
                return data;
            }

            // Process each column in the header row
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell headerCell = headerRow.getCell(i);
                Cell dataCell = dataRow.getCell(i);

                // Get header name (column name)
                String columnName = getCellValueAsString(headerCell);
                
                // Get data value (handle empty cells gracefully)
                String cellValue = getCellValueAsString(dataCell);
                
                // Store the mapping (empty string if cell is empty)
                data.put(columnName, cellValue);
                
                System.out.println("Column: '" + columnName + "' = '" + cellValue + "'");
            }

        } catch (Exception e) {
            System.out.println("❌ Error reading Excel file: " + e.getMessage());
            e.printStackTrace();
        }

        return data;
    }

    // Method 2: Reads a specific cell by row and column number
    public String readExcel(String filePath, String sheetName, int rowNum, int cellNum) {
        String cellValue = "";

        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            
            if (sheet == null) {
                System.out.println("❌ Sheet not found: " + sheetName);
                return cellValue;
            }
            
            Row row = sheet.getRow(rowNum);
            if (row == null) {
                System.out.println("⚠️ Row " + rowNum + " is empty");
                return cellValue;
            }
            
            Cell cell = row.getCell(cellNum);
            cellValue = getCellValueAsString(cell);

        } catch (Exception e) {
            System.out.println("❌ Error reading cell [" + rowNum + "," + cellNum + "]: " + e.getMessage());
            e.printStackTrace();
        }

        return cellValue;
    }

    // Helper method to convert any cell to String (handles null and empty cells)
    private String getCellValueAsString(Cell cell) {
        // Handle null cells
        if (cell == null) {
            return ""; // Return empty string for null cells
        }
        
        // Handle different cell types
        switch (cell.getCellType()) {
            case STRING:
                String stringValue = cell.getStringCellValue();
                return stringValue != null ? stringValue.trim() : "";
                
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    try {
                        // Format date as dd/MM/yyyy
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        return formatter.format(cell.getLocalDateTimeCellValue().toLocalDate());
                    } catch (Exception e) {
                        System.out.println("⚠️ Error formatting date cell: " + e.getMessage());
                        return "";
                    }
                } else {
                    // Handle numeric values
                    double numericValue = cell.getNumericCellValue();
                    // Check if it's a whole number
                    if (numericValue == (long) numericValue) {
                        return String.valueOf((long) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }
                }
                
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
                
            case FORMULA:
                try {
                    // Try to evaluate the formula
                    return getCellValueAsString(cell.getCachedFormulaResultType(), cell);
                } catch (Exception e) {
                    System.out.println("⚠️ Error evaluating formula: " + e.getMessage());
                    return "";
                }
                
            case BLANK:
            case _NONE:
            default:
                return ""; // Return empty string for blank or unknown cell types
        }
    }
    
    // Helper method for handling formula cell results
    private String getCellValueAsString(CellType cellType, Cell cell) {
        switch (cellType) {
            case STRING:
                return cell.getRichStringCellValue().getString();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    return formatter.format(cell.getLocalDateTimeCellValue().toLocalDate());
                } else {
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == (long) numericValue) {
                        return String.valueOf((long) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }
}