package zeroone3010.mediawiki.spreadsheettemplator;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class ExcelToTemplateDataParser {
    public TemplateDataCollection parseExcelFile(final File file) {
        Workbook workbook = null;
        try {
            workbook = Workbook.getWorkbook(file);
        } catch (BiffException | IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        final Sheet sheet = workbook.getSheet(0);
        final String templateName = sheet.getName();
        final Cell[] parameterNames = sheet.getRow(0);
        return new TemplateDataCollection(templateName, readRows(sheet, parameterNames));
    }

    private List<Map<String, String>> readRows(final Sheet sheet, final Cell[] parameterNames) {
        final List<Map<String, String>> templates = new ArrayList<>();
        for (int row = 1; row < sheet.getRows(); row++) {
            final Map<String, String> template = new LinkedHashMap<>();
            for (int param = 0; param < sheet.getRow(row).length; param++) {
                template.put(parameterNames[param].getContents(), sheet.getRow(row)[param].getContents());
            }
            templates.add(template);
        }
        return templates;
    }
}
