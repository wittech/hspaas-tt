package com.huashi.web.util.excel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ExcelUtil{

    public static final String  VERSION_2007         = "xlsx";
    public static DecimalFormat DECIMAL_FORMAT       = new DecimalFormat("#");

    /**
     * 生成文件默认以03扩展名
     */
    public static final String  EXCEL_SUFFIX_NAME    = ".xls";

    /**
     * 默认标题行行号
     */
    public static final int     DEFAULT_TITLE_ROW_NO = 0;

    /**
     * 空值
     */
    private static final String EMPTY_VALUE          = "";

    /**
     * 导出EXCEL，表单Sheet最多容纳30000W条数据，多余则导入至其他SHEET表单
     */
    private static final int    SINGLE_SHEET_MAX_NUM = 50000;

    /**
     * 逗号分隔符号定义
     */
    public static final String  DATA_SPLIT_CHARCATOR = ",";

    private static Logger       LOGGER               = LoggerFactory.getLogger(ExcelUtil.class);

    /**
     * 创建工作薄
     * 
     * @param inpuStream
     * @return
     */
    public static Workbook createWorkBook(InputStream inpuStream) {
        if (inpuStream == null) {
            return null;
        }

        try {
            return new XSSFWorkbook(inpuStream);
        } catch (Exception e) {
            LOGGER.warn("Create work book failed with 2007.2010 mode", e);
            try {
                return new HSSFWorkbook(inpuStream);
            } catch (Exception e2) {
                LOGGER.error("Create work book failed with 2003 mode", e2);
                throw new RuntimeException(e2);
            }
        }
    }

    /**
     * TODO 读取EXCEL文件
     * 
     * @param inputStream 输入流
     * @return
     */
    public static List<String[]> read(InputStream inputStream) {
        List<String[]> list = new ArrayList<String[]>();
        try {
            Workbook workBook = createWorkBook(inputStream);
            Sheet sheet = workBook.getSheetAt(0);
            int rows = sheet.getPhysicalNumberOfRows(); // 获得行数
            if (rows == 0) {
                return list;
            }

            // EXCEL前1行为总标题和列标题，因此在第2行开始读取数据
            // 遍历行
            for (int r = 1; r < rows; r++) {
                Row row = sheet.getRow(r);
                if (row == null) {
                    continue;
                }

                // 获得列数
                int cells = row.getLastCellNum();
                String[] array = new String[cells];
                // 遍历列
                for (int c = 0; c < cells; c++) {
                    Cell cell = row.getCell(c);
                    if (cell == null) {
                        continue;
                    }
                    array[c] = getCellValue(cell);
                }
                list.add(array);
            }

            return list;
        } catch (Exception ex) {
            LOGGER.error("Create work book failed with 2007.2010 mode", ex);
            return null;
        }

    }

    /**
     * TODO 读取EXCEL文件
     * 
     * @param inputStream 输入流
     * @return
     */

    public static Set<String> readFirstColoum(InputStream inputStream) {
        try {
            Workbook workBook = createWorkBook(inputStream);
            Sheet sheet = workBook.getSheetAt(0);
            int rows = sheet.getPhysicalNumberOfRows(); // 获得行数
            if (rows == 0) {
                return null;
            }

            Set<String> list = new HashSet<>();
            // EXCEL前1行为总标题和列标题，因此在第2行开始读取数据
            // 遍历行
            for (int r = 1; r < rows; r++) {
                Row row = sheet.getRow(r);
                if (row == null) {
                    continue;
                }

                // 获取第一列数据
                Cell cell = row.getCell(0);
                if (cell == null) {
                    continue;
                }

                list.add(getCellValue(cell));
            }
            return list;
        } catch (Exception ex) {
            LOGGER.error("readFirstColoum failed", ex);
            return null;
        }
    }

    /**
     * TODO 标题列是否包含其中一列
     * 
     * @param exceptName
     * @param factName
     * @return
     */
    public static boolean titleIsContain(String[] exceptName, String[] factName) {
        boolean contain = false;
        try {
            if (exceptName.length == 0) {
                return true;
            }
            if (factName.length == 0) {
                return false;
            }
            for (String fn : factName) {
                for (String en : exceptName) {
                    if (en.equals(fn)) {
                        contain = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contain;

    }

    /**
     * 获取列的值
     * 
     * @param cell
     * @return
     */
    public static String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        String value = null;
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC: // 数值型
                if (DateUtil.isCellDateFormatted(cell)) {
                    // 如果是date类型 ，获取该cell的date值
                    value = new SimpleDateFormat("yyyy-MM-dd").format(DateUtil.getJavaDate(cell.getNumericCellValue()));
                } else {// 纯数字
                    value = DECIMAL_FORMAT.format(cell.getNumericCellValue());
                }
                break;
            /* 此行表示单元格的内容为string类型 */
            case Cell.CELL_TYPE_STRING: // 字符串型
                value = cell.getRichStringCellValue().toString();
                break;
            case Cell.CELL_TYPE_FORMULA:// 公式型
                // 读公式计算值
                value = String.valueOf(cell.getNumericCellValue());
                if (value.equals("NaN")) {// 如果获取的数据值为非法值,则转换为获取字符串
                    value = cell.getRichStringCellValue().toString();
                }
                // cell.getCellFormula();读公式
                break;
            case Cell.CELL_TYPE_BOOLEAN:// 布尔
                value = " " + cell.getBooleanCellValue();
                break;
            /* 此行表示该单元格值为空 */
            case Cell.CELL_TYPE_BLANK: // 空值
                value = "";
                break;
            case Cell.CELL_TYPE_ERROR: // 故障
                value = "";
                break;
            default:
                value = cell.getRichStringCellValue().toString();
        }
        return value;
    }

    /**
     * TODO 获取下载后的文件扩展名
     * 
     * @return
     * @throws Exception
     */
    public static String downloadFilename() {
        return String.format("%s.%s", com.huashi.common.util.DateUtil.getNowNumberStr(), EXCEL_SUFFIX_NAME);
    }

    /**
     * TODO 生成EXCEL下载流
     * 
     * @param workbook
     * @return
     * @throws Exception
     */
    public static InputStream downloadStream(HSSFWorkbook workbook) throws Exception {
        if (workbook != null) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            workbook.write(os);
            os.flush();
            byte[] content = os.toByteArray();
            InputStream excelStream = new ByteArrayInputStream(content, 0, content.length);
            os.close();
            return excelStream;
        }
        return null;
    }

    public static InputStream downloadStream(String[] title, List<String[]> list) throws Exception {
        HSSFWorkbook workbook = excelWorkBook(title, list);
        if (workbook != null) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            workbook.write(os);
            os.flush();
            byte[] content = os.toByteArray();
            InputStream excelStream = new ByteArrayInputStream(content, 0, content.length);
            os.close();
            return excelStream;
        }
        return null;
    }

    /**
     * TODO 创建EXCEL对象
     * 
     * @param title
     * @param content
     * @return
     * @throws Exception
     */
    public static HSSFWorkbook excelWorkBook(String[] title, List<String[]> list) throws Exception {
        String sheetname = new SimpleDateFormat("yyyyMMdd").format(new Date());
        HSSFWorkbook workbook = null;
        try {
            // 创建一个EXCEL
            workbook = new HSSFWorkbook();
            // 计算应导出Sheet数目
            int last = list.size() % SINGLE_SHEET_MAX_NUM;
            int count = 0;
            if (last == 0) {
                count = list.size() / SINGLE_SHEET_MAX_NUM;
            } else {
                count = list.size() / SINGLE_SHEET_MAX_NUM + 1;
            }
            // 创建Sheet表单
            for (int s = 0; s < count; s++) {
                Sheet sheet = workbook.createSheet(sheetname + "_" + (s + 1));
                // 表头显示内容
                Font font = workbook.createFont();
                font.setFontHeightInPoints((short) 12);// 字体高度
                font.setColor(HSSFFont.COLOR_NORMAL); // 字体颜色
                font.setFontName("宋体");// 字体
                font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 加粗
                CellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 水平居中
                cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直居中
                cellStyle.setFont(font);
                cellStyle.setWrapText(false);
                // 创建标题行
                Row row = sheet.createRow(0);
                // 填充标题
                for (int i = 0; i < title.length; i++) {
                    sheet.setColumnWidth(i + 1, 800 * 10);
                    Cell cell = row.createCell(i);
                    cell.setCellValue(title[i]);
                    cell.setCellStyle(cellStyle);
                }
                // 判断当前Sheet表单应该打的行数
                int ecn = s == (count - 1) ? (s * SINGLE_SHEET_MAX_NUM + last) : (s + 1) * SINGLE_SHEET_MAX_NUM;
                int rowno = 1;
                for (int i = (s * SINGLE_SHEET_MAX_NUM); i < ecn; i++) {
                    String[] values = list.get(i);
                    Row row1 = sheet.createRow(rowno);
                    for (int c = 0; c < values.length; c++) {
                        // 创建列值
                        Cell cell = row1.createCell(c);
                        cell.setCellValue(StringUtils.isEmpty(values[c]) ? EMPTY_VALUE : values[c]);
                    }
                    rowno++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return workbook;
    }

    public static void main(String args[]) {
        try {
            // List<String[]> list = readExcel("d:/2007.xlsx");
            // List<String[]> list = readExcel("d:\\excel\\20140708192436.xls");
            // for (int i = 0; i < list.size() - 1; i++) {
            // String[] s = list.get(i);
            // System.out.print(s[0] + "----------" + s[1]);
            // System.out.println();
            // }
            // for(String[] s: list){
            // System.out.print(s[0]+"----------"+s[1]);
            // System.out.println();
            // }
            // System.out.println(list.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
