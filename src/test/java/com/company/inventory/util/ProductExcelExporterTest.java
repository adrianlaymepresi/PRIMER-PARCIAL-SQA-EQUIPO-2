package com.company.inventory.util;

import com.company.inventory.model.Category;
import com.company.inventory.model.Product;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductExcelExporterTest {

    @Test
    void export_success_generatesValidExcel() throws Exception {

        Category category = new Category();
        category.setId(1L);
        category.setName("Bebidas");

        Product product = new Product();
        product.setId(10L);
        product.setName("Agua");
        product.setPrice(15);
        product.setAccount(30);
        product.setCategory(category);

        ProductExcelExporter exporter =
                new ProductExcelExporter(List.of(product));

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        exporter.export(response);

        byte[] bytes =
                response.getContentAsByteArray();

        assertTrue(bytes.length > 0);

        try (XSSFWorkbook workbook =
                     new XSSFWorkbook(
                             new ByteArrayInputStream(bytes))) {

            XSSFSheet sheet =
                    workbook.getSheet("Resultado");

            assertNotNull(sheet);

            Row header = sheet.getRow(0);
            Row data = sheet.getRow(1);

            assertAll(
                    () -> assertEquals(
                            "ID",
                            header.getCell(0)
                                    .getStringCellValue()),

                    () -> assertEquals(
                            "Nombre",
                            header.getCell(1)
                                    .getStringCellValue()),

                    () -> assertEquals(
                            "Precio",
                            header.getCell(2)
                                    .getStringCellValue()),

                    () -> assertEquals(
                            "Cantidad",
                            header.getCell(3)
                                    .getStringCellValue()),

                    () -> assertEquals(
                            "Categoría",
                            header.getCell(4)
                                    .getStringCellValue()),

                    () -> assertEquals(
                            "10",
                            data.getCell(0)
                                    .getStringCellValue()),

                    () -> assertEquals(
                            "Agua",
                            data.getCell(1)
                                    .getStringCellValue()),

                    () -> assertEquals(
                            15.0,
                            data.getCell(2)
                                    .getNumericCellValue()),

                    () -> assertEquals(
                            30.0,
                            data.getCell(3)
                                    .getNumericCellValue()),

                    () -> assertEquals(
                            "Bebidas",
                            data.getCell(4)
                                    .getStringCellValue())
            );
        }
    }

    @Test
    void export_emptyList_generatesHeaderOnly() throws Exception {

        ProductExcelExporter exporter =
                new ProductExcelExporter(List.of());

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        exporter.export(response);

        try (XSSFWorkbook workbook =
                     new XSSFWorkbook(
                             new ByteArrayInputStream(
                                     response.getContentAsByteArray()))) {

            XSSFSheet sheet =
                    workbook.getSheet("Resultado");

            assertAll(
                    () -> assertNotNull(sheet),
                    () -> assertEquals(0, sheet.getLastRowNum())
            );
        }
    }

    @Test
    void createCell_coversBooleanBranch() throws Exception {

        ProductExcelExporter exporter =
                new ProductExcelExporter(List.of());

        Method writeHeaderLine =
                ProductExcelExporter.class
                        .getDeclaredMethod("writeHeaderLine");

        writeHeaderLine.setAccessible(true);
        writeHeaderLine.invoke(exporter);

        Field workbookField =
                ProductExcelExporter.class
                        .getDeclaredField("workbook");

        workbookField.setAccessible(true);

        XSSFWorkbook workbook =
                (XSSFWorkbook) workbookField.get(exporter);

        XSSFSheet sheet =
                workbook.getSheet("Resultado");

        Row row = sheet.createRow(2);

        CellStyle style =
                workbook.createCellStyle();

        Method createCell =
                ProductExcelExporter.class.getDeclaredMethod(
                        "createCell",
                        Row.class,
                        int.class,
                        Object.class,
                        CellStyle.class);

        createCell.setAccessible(true);

        createCell.invoke(
                exporter,
                row,
                0,
                Boolean.TRUE,
                style);

        assertTrue(
                row.getCell(0)
                        .getBooleanCellValue());

        workbook.close();
    }
}