package com.ksh.beam.common.utils;

import com.ksh.beam.common.base.entity.ExcelEntity;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.util.CellUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件工具
 */
public class FileUtil {

    /**
     * 导出Excel文件
     *
     * @param list     要导出数据
     * @param fieldMap 表头字英文字段名和中文名对应
     * @param filName  文件名
     * @param response 响应
     * @param workbook 创建模板
     * @param excelEntity excel文档属性
     */
    public static void exportFile(List<Map<String, Object>> list,
                                  LinkedHashMap<String, String> fieldMap,
                                  String filName,
                                  HSSFWorkbook workbook,
                                  ExcelEntity excelEntity,
                                  HttpServletResponse response) throws IOException {

        //Excel文档信息
        workbook.createInformationProperties();
        DocumentSummaryInformation info = workbook.getDocumentSummaryInformation();
        info.setCompany(excelEntity.getCompany());
        info.setManager(excelEntity.getManager());
        info.setCategory(excelEntity.getCategory());
        //Excel摘要信息
        SummaryInformation si = workbook.getSummaryInformation();
        si.setSubject(excelEntity.getSubject());
        si.setTitle(excelEntity.getTitle());
        si.setAuthor(excelEntity.getAuthor());
        si.setComments(excelEntity.getComments());

        // 定义存放英文字段名和中文字段名的数组
        String[] enFields = new String[fieldMap.size()];
        String[] cnFields = new String[fieldMap.size()];

        // 填充数组
        int count = 0;
        for (Map.Entry<String, String> entry : fieldMap.entrySet()) {
            enFields[count] = entry.getKey();
            cnFields[count] = entry.getValue();
            count++;
        }

        //表格边框
        Map<String, Object> properties = new HashMap<>();
        properties.put(CellUtil.BORDER_TOP, BorderStyle.THIN);
        properties.put(CellUtil.BORDER_BOTTOM, BorderStyle.THIN);
        properties.put(CellUtil.BORDER_LEFT, BorderStyle.THIN);
        properties.put(CellUtil.BORDER_RIGHT, BorderStyle.THIN);

        //设置新工作表1
        int rowIndex = 0;
        HSSFSheet sheet = workbook.createSheet(filName + "1");
        HSSFRow row = sheet.createRow(0);
        row.setHeight((short) (16.5 * 20));//设置行高
        //设置表头
        for (int i = 0; i < cnFields.length; i++) {
            HSSFCell headCell = row.createCell(i);
            headCell.setCellValue(cnFields[i]);
            CellUtil.setCellStyleProperties(headCell, properties);
        }

        //写入数据
        for (int i = 0; i < list.size(); i++) {
            if (rowIndex > 99999) {
                //设置新工作表2
                sheet = workbook.createSheet(filName + "2");
                row = sheet.createRow(0);
                row.setHeight((short) (16.5 * 20));//设置行高
                //设置表头
                for (int k = 0; k < cnFields.length; k++) {
                    HSSFCell headCell = row.createCell(k);
                    headCell.setCellValue(cnFields[k]);
                    CellUtil.setCellStyleProperties(headCell, properties);
                }
            }
            row = sheet.createRow(i + 1);
            for (int j = 0; j < enFields.length; j++) {
                Object data = list.get(i).get(enFields[j]);//获取第i行第j列所放数据
                HSSFCell contentCell = row.createCell(j);
                boolean isNum = false;
                boolean isInteger = false;
                if (data != null) {
                    //判断data是否为数值型
                    isNum = data.toString().matches("^(-?\\d+)(\\.\\d+)?$");
                    //判断data是否为整数（小数部分是否为0）
                    isInteger = data.toString().matches("^[-\\+]?[\\d]*$");
                }
                if (isNum) {
                    //设置格子单元样式
                    HSSFCellStyle contextStyle = workbook.createCellStyle();
                    if (isInteger) {
                        //数据格式只显示整数
                        contextStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0"));
                        // 设置单元格内容为double类型
                        contentCell.setCellValue(data.toString());
                    } else {
                        //保留两位小数点
                        contextStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
                        // 设置单元格内容为double类型
                        contentCell.setCellValue(Double.parseDouble(data.toString()));
                        // 设置单元格格式
                        contentCell.setCellStyle(contextStyle);
                    }
                } else {
                    contentCell.setCellValue(list.get(i).get(enFields[j]) == null ? "" : list.get(i).get(enFields[j]).toString());
                }
                CellUtil.setCellStyleProperties(contentCell, properties);
            }
            rowIndex++;
        }
        // 遍历集合数据，产生数据行
        sheet.setDefaultRowHeight((short) (16.5 * 20));
        //列宽自适应
        for (int i = 0; i < cnFields.length; i++) {
            sheet.autoSizeColumn(i);
            // 解决自动设置列宽中文失效的问题
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 17 / 10);
        }
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        OutputStream os = response.getOutputStream();
        response.setHeader("Content-disposition", "attachment;filename=" + filName);
        workbook.write(os);
        os.flush();
        os.close();
    }

    /**
     * 删除目录
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    /**
     * 复制文件
     */
    public static void copy(File src, File dst) throws Exception {
        int BUFFER_SIZE = 4096;
        try (InputStream is = new BufferedInputStream(new FileInputStream(src), BUFFER_SIZE);
             OutputStream os = new BufferedOutputStream(new FileOutputStream(dst), BUFFER_SIZE)
        ) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int len;
            while ((len = is.read(buffer)) > 0) {
                os.write(buffer, 0, len);
            }
        }
    }

    /**
     *获取文件大小
     */
    public static String getFileSize(long size){
        double cache = size / 1024f;
        String unit = "KB";
        if (cache >= 1024f) {
            cache /= 1024f;
            unit = "MB";
        }
        if (cache >= 1024f) {
            cache /= 1024f;
            unit = "GB";
        }
        if (cache >= 1024f) {
            cache /= 1024f;
            unit = "TB";
        }
        return new DecimalFormat("0.00").format(cache) + unit;
    }
}