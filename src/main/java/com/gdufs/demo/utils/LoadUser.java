package com.gdufs.demo.utils;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileInputStream;

/**
 * 读取excel用户数据，应该没用到了
 */
public class LoadUser {
    public static void poiTestMethod() throws Exception {
        //1.读取Excel文档对象
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(new FileInputStream("G:\\javaweb\\学生基本信息.xls"));
        //2.获取要解析的表格（第一个表格）
        HSSFSheet sheet = hssfWorkbook.getSheetAt(0);
        //获得最后一行的行号
        int lastRowNum = sheet.getLastRowNum();
        for (int i = 0; i <= 3; i++) {//遍历每一行
            //3.获得要解析的行
            HSSFRow row = sheet.getRow(i);
            //4.获得每个单元格中的内容（String）
            String stringCellValue0 = row.getCell(0).getStringCellValue();
            String stringCellValue1 = row.getCell(1).getStringCellValue();

        }
    }
}
