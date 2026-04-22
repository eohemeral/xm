package com.example.springboot.utils;

import org.apache.poi.xwpf.usermodel.*;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class ExportUtils {

    // 导出用户信息为Word文档
    public static void exportUsersToWord(List<Map<String, Object>> users, OutputStream outputStream) throws IOException {
        // 创建Word文档
        XWPFDocument document = new XWPFDocument();
        
        // 添加标题
        XWPFParagraph title = document.createParagraph();
        XWPFParagraph titleParagraph = document.createParagraph();
        XWPFRun titleRun = titleParagraph.createRun();
        titleRun.setText("用户信息表");
        titleRun.setFontSize(16);
        titleRun.setBold(true);
        titleParagraph.setAlignment(ParagraphAlignment.CENTER);
        
        // 添加表格
        XWPFTable table = document.createTable(users.size() + 1, 5);
        
        // 设置表头
        XWPFTableRow headerRow = table.getRow(0);
        headerRow.getCell(0).setText("ID");
        headerRow.getCell(1).setText("用户名");
        headerRow.getCell(2).setText("邮箱");
        headerRow.getCell(3).setText("状态");
        headerRow.getCell(4).setText("注册时间");
        
        // 设置表头样式
        for (int i = 0; i < 5; i++) {
            XWPFTableCell cell = headerRow.getCell(i);
            cell.setColor("E6E6FA");
            XWPFParagraph paragraph = cell.getParagraphArray(0);
            if (paragraph != null) {
                List<XWPFRun> runs = paragraph.getRuns();
                if (!runs.isEmpty()) {
                    XWPFRun run = runs.get(0);
                    run.setBold(true);
                }
            }
        }
        
        // 填充数据
        for (int i = 0; i < users.size(); i++) {
            Map<String, Object> user = users.get(i);
            XWPFTableRow row = table.getRow(i + 1);
            row.getCell(0).setText(user.get("id").toString());
            row.getCell(1).setText(user.get("username").toString());
            row.getCell(2).setText(user.get("email") != null ? user.get("email").toString() : "");
            row.getCell(3).setText((Integer) user.get("status") == 1 ? "启用" : "禁用");
            row.getCell(4).setText(user.get("createdAt") != null ? user.get("createdAt").toString() : "");
        }
        
        // 调整列宽
        table.setWidth("100%");
        table.getCTTbl().addNewTblGrid().addNewGridCol().setW(java.math.BigInteger.valueOf(500));
        table.getCTTbl().getTblGrid().addNewGridCol().setW(java.math.BigInteger.valueOf(1500));
        table.getCTTbl().getTblGrid().addNewGridCol().setW(java.math.BigInteger.valueOf(2000));
        table.getCTTbl().getTblGrid().addNewGridCol().setW(java.math.BigInteger.valueOf(800));
        table.getCTTbl().getTblGrid().addNewGridCol().setW(java.math.BigInteger.valueOf(1500));
        
        // 写入输出流
        document.write(outputStream);
        document.close();
    }

    // 导出课程信息为Word文档
    public static void exportCoursesToWord(List<Map<String, Object>> courses, OutputStream outputStream) throws IOException {
        // 创建Word文档
        XWPFDocument document = new XWPFDocument();
        
        // 添加标题
        XWPFParagraph titleParagraph = document.createParagraph();
        XWPFRun titleRun = titleParagraph.createRun();
        titleRun.setText("课程信息表");
        titleRun.setFontSize(16);
        titleRun.setBold(true);
        titleParagraph.setAlignment(ParagraphAlignment.CENTER);
        
        // 添加表格
        XWPFTable table = document.createTable(courses.size() + 1, 6);
        
        // 设置表头
        XWPFTableRow headerRow = table.getRow(0);
        headerRow.getCell(0).setText("ID");
        headerRow.getCell(1).setText("课程名称");
        headerRow.getCell(2).setText("类型");
        headerRow.getCell(3).setText("难度");
        headerRow.getCell(4).setText("浏览量");
        headerRow.getCell(5).setText("收藏量");
        
        // 设置表头样式
        for (int i = 0; i < 6; i++) {
            XWPFTableCell cell = headerRow.getCell(i);
            cell.setColor("E6E6FA");
            XWPFParagraph paragraph = cell.getParagraphArray(0);
            if (paragraph != null) {
                List<XWPFRun> runs = paragraph.getRuns();
                if (!runs.isEmpty()) {
                    XWPFRun run = runs.get(0);
                    run.setBold(true);
                }
            }
        }
        
        // 填充数据
        for (int i = 0; i < courses.size(); i++) {
            Map<String, Object> course = courses.get(i);
            XWPFTableRow row = table.getRow(i + 1);
            row.getCell(0).setText(course.get("id").toString());
            row.getCell(1).setText(course.get("title").toString());
            row.getCell(2).setText(course.get("type").toString());
            row.getCell(3).setText(course.get("level").toString());
            row.getCell(4).setText(course.get("viewCount").toString());
            row.getCell(5).setText(course.get("collectCount").toString());
        }
        
        // 调整列宽
        table.setWidth("100%");
        table.getCTTbl().addNewTblGrid().addNewGridCol().setW(java.math.BigInteger.valueOf(500));
        table.getCTTbl().getTblGrid().addNewGridCol().setW(java.math.BigInteger.valueOf(2000));
        table.getCTTbl().getTblGrid().addNewGridCol().setW(java.math.BigInteger.valueOf(1000));
        table.getCTTbl().getTblGrid().addNewGridCol().setW(java.math.BigInteger.valueOf(1000));
        table.getCTTbl().getTblGrid().addNewGridCol().setW(java.math.BigInteger.valueOf(1000));
        table.getCTTbl().getTblGrid().addNewGridCol().setW(java.math.BigInteger.valueOf(1000));
        
        // 写入输出流
        document.write(outputStream);
        document.close();
    }
}
