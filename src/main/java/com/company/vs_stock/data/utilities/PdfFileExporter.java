package com.company.vs_stock.data.utilities;

import com.itextpdf.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Map;

public class PdfFileExporter {
//    public void exportPdfFile(String templateFileName, Map<String, Object> data, String pdfFileName) {
//        String htmlContent = generateHtml(templateFileName, data);
//        try {
//            FileOutputStream fileOutputStream = new FileOutputStream(pdfFileName);
//            ITextRenderer renderer = new ITextRenderer();
//            ITextFontResolver fontResolver = renderer.getFontResolver();
//            fontResolver.addFont("/static/verdana.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
//            renderer.setDocumentFromString(htmlContent);
//            renderer.layout();
//            renderer.createPDF(fileOutputStream, false);
//            renderer.finishPDF();
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (DocumentException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public ResponseEntity<InputStreamResource>  showPdfFilePreview(String templateFileName, Map<String, Object> data, String pdfFileName) throws IOException {
        String htmlContent = generateHtml(templateFileName, data);
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(pdfFileName);
            ITextRenderer renderer = new ITextRenderer();
            ITextFontResolver fontResolver = renderer.getFontResolver();
            fontResolver.addFont("/static/verdana.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(fos, false);
            renderer.finishPDF();
            File file = new File(pdfFileName);
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            Base64.Encoder encoder = Base64.getEncoder();
            var content = encoder.encodeToString(fileBytes);
            byte[] decoder = Base64.getDecoder().decode(content);
            is = new ByteArrayInputStream(decoder);
            InputStreamResource resource = new InputStreamResource(is);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);

            ContentDisposition disposition = ContentDisposition.inline().filename("offer.pdf").build();
            headers.setContentDisposition(disposition);

            return new ResponseEntity<InputStreamResource>(resource, headers, HttpStatus.OK);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }  catch (IOException e) {
            e.printStackTrace();
        } catch (com.lowagie.text.DocumentException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                is.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
        return null;
    }

    private String generateHtml(String templateFileName, Map<String, Object> data) {
        TemplateEngine templateEngine = createTemplateEngine();
        Context context = new Context();
        context.setVariables(data);
        return templateEngine.process(templateFileName, context);
    }

    private TemplateEngine createTemplateEngine() {
        ClassLoaderTemplateResolver pdfTemplateResolver = new ClassLoaderTemplateResolver();
        pdfTemplateResolver.setPrefix("templates/pdf_templates/");
        pdfTemplateResolver.setSuffix(".html");
        pdfTemplateResolver.setTemplateMode("HTML");
        pdfTemplateResolver.setCharacterEncoding("UTF-8");
        pdfTemplateResolver.setOrder(1);

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(pdfTemplateResolver);
        return templateEngine;
    }
}
