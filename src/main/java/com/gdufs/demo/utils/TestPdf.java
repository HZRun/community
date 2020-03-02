package com.gdufs.demo.utils;

import com.gdufs.demo.entity.ActivityApply;
import com.gdufs.demo.service.ActivityApplyService;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * 用于测试，废弃
 */
public class TestPdf {
    @Autowired
    private ActivityApplyService activityApplyService;

    public static void main(String[] args) throws Exception {

        TestPdf pdf = new TestPdf();
        String filename = "G:/testTable3.pdf";
        Integer activityId = 14;
        pdf.createPDF(filename, activityId);
        System.out.println("打印完成");

    }

    public void createPDF(String filename, Integer activityId) throws IOException {
        Document document = new Document(PageSize.A4);
        ActivityApply activityApply = activityApplyService.getActivityApplyById(activityId);
        String title = activityApply.getTitle();
        String content = activityApply.getIntroduce();
        String area = activityApply.getAreaName();
        Date date = activityApply.getStartTime();
        Integer joinNum = activityApply.getMembersLess();
        String sponsor = activityApply.getSponsor();


        try {
            PdfWriter.getInstance(document, new FileOutputStream(filename));
            document.addTitle("example of PDF");
            document.open();
            document.add(new Paragraph("Hello World!"));
            document.add(new Paragraph("good moringing"));
            document.addAuthor(sponsor);
            document.addTitle(title);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
    }
}
