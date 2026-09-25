package com.company.inventory.util;

import com.company.inventory.model.Category;
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

class CategoryExcelExporterTest {

    @Test
    void export_success_generatesValidExcel() throws Exception {

        Category category = new Category();
        category.setId(1L);
        category.setName("Bebidas");
        category.setDescription("Categoria de bebidas");

        CategoryExcelExporter exporter =
                new CategoryExcelExporter(List.of(category));

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        exporter.export(response);

        byte[] bytes = response.getContentAsByteArray();

        assertTrue(bytes.length > 0);

        try (XSSFWorkbook workbook =
                     new XSSFWorkbook(
                             new ByteArrayInputStream(bytes))) {

            XSSFSheet sheet =
                    workbook.getSheet("Resultado");

            assertNotNull(sheet);

            assertAll(
                    () -> assertEquals(
                            "ID",
                            sheet.getRow(0)
                                    .getCell(0)
                                    .getStringCellValue()),

                    () -> assertEquals(
                            "Nombre",
                            sheet.getRow(0)
                                    .getCell(1)
                                    .getStringCellValue()),

                    () -> assertEquals(
                            "Descripción",
                            sheet.getRow(0)
                                    .getCell(2)
                                    .getStringCellValue()),

                    () -> assertEquals(
                            "1",
                            sheet.getRow(1)
                                    .getCell(0)
                                    .getStringCellValue()),

                    () -> assertEquals(
                            "Bebidas",
                            sheet.getRow(1)
                                    .getCell(1)
                                    .getStringCellValue()),

                    () -> assertEquals(
                            "Categoria de bebidas",
                            sheet.getRow(1)
                                    .getCell(2)
                                    .getStringCellValue())
            );
        }
    }

    @Test
    void export_emptyList_generatesHeaderOnly() throws Exception {

        CategoryExcelExporter exporter =
                new CategoryExcelExporter(List.of());

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
    void createCell_coversIntegerBooleanAndStringBranches()
            throws Exception {

        CategoryExcelExporter exporter =
                new CategoryExcelExporter(List.of());

        Method writeHeaderLine =
                CategoryExcelExporter.class
                        .getDeclaredMethod("writeHeaderLine");

        writeHeaderLine.setAccessible(true);
        writeHeaderLine.invoke(exporter);

        Field workbookField =
                CategoryExcelExporter.class
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
                CategoryExcelExporter.class.getDeclaredMethod(
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
                Integer.valueOf(25),
                style);

        createCell.invoke(
                exporter,
                row,
                1,
                Boolean.TRUE,
                style);

        createCell.invoke(
                exporter,
                row,
                2,
                "Texto",
                style);

        assertAll(
                () -> assertEquals(
                        25.0,
                        row.getCell(0).getNumericCellValue()),

                () -> assertTrue(
                        row.getCell(1).getBooleanCellValue()),

                () -> assertEquals(
                        "Texto",
                        row.getCell(2).getStringCellValue())
        );

        workbook.close();
    }
}