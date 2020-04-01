package com.example.jacob.springboot.demo.utils;

import com.aspose.words.License;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Objects;

/**
 * 验证Aspose相关产品证书工具类
 * @author duhongbo
 * @date 2020/3/20 9:32
 */

public class VerifyLicenseUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(VerifyLicenseUtil.class);

    /**
     * 校验aspose-slides许可证
     * @author duhongbo
     * @return boolean
     */
    public static boolean verifySlidesLicense(){
       InputStream pptLicense = com.aspose.slides.Presentation.class.getResourceAsStream("/license-ppt.xml");
        com.aspose.slides.License slidesLicense = new com.aspose.slides.License();
        slidesLicense.setLicense(pptLicense);
        return true;
    }

    /**
     * 校验aspose-word许可证
     * @author duhongbo
     * @return boolean
     */
    public static boolean verifyWordsLicense(){
        try(InputStream wordLicense = Objects.requireNonNull(com.aspose.words.Document.class.getResourceAsStream("/license-word.xml"))) {
            com.aspose.words.License license = new License();
            license.setLicense(wordLicense);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("校验aspose-word许可证出错",e);
        }
        return false;
    }

    /**
     * 校验aspose-pdf许可证
     * @author duhongbo
     * @param
     * @return boolean
     */
    public static boolean verifyPdfLicense(){
        try(InputStream pdfLicense = Objects.requireNonNull(com.aspose.pdf.Document.class.getResourceAsStream("/license-pdf.xml"))) {
            com.aspose.pdf.License license = new com.aspose.pdf.License();
            license.setLicense(pdfLicense);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("校验aspose-pdf许可证出错",e);
        }
        return false;
    }
}
