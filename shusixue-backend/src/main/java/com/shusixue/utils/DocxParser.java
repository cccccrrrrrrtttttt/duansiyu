package com.shusixue.utils;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Docx文件解析工具类
 */
public class DocxParser {

    /**
     * 解析docx文件，提取文本内容
     * @param filePath 文件路径
     * @return 提取的文本内容
     * @throws IOException 解析异常
     */
    public static String parseDocx(String filePath) throws IOException {
        try (InputStream is = new FileInputStream(filePath);
             XWPFDocument document = new XWPFDocument(is)) {
            StringBuilder content = new StringBuilder();

            // 提取段落内容
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                for (XWPFRun run : paragraph.getRuns()) {
                    content.append(run.getText(0));
                }
                content.append("\n");
            }

            return content.toString().trim();
        }
    }

    /**
     * 解析docx文件流，提取文本内容
     * @param inputStream 文件输入流
     * @return 提取的文本内容
     * @throws IOException 解析异常
     */
    public static String parseDocx(InputStream inputStream) throws IOException {
        try (XWPFDocument document = new XWPFDocument(inputStream)) {
            StringBuilder content = new StringBuilder();

            // 提取段落内容
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                for (XWPFRun run : paragraph.getRuns()) {
                    content.append(run.getText(0));
                }
                content.append("\n");
            }

            return content.toString().trim();
        }
    }
}
