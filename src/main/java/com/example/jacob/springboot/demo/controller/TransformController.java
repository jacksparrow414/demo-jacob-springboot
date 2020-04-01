package com.example.jacob.springboot.demo.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aspose.pdf.*;
import com.aspose.pdf.devices.PngDevice;
import com.aspose.slides.Presentation;
import com.aspose.slides.SaveFormat;
import com.aspose.words.Document;
import com.aspose.words.ImageSaveOptions;
import com.example.jacob.springboot.demo.demoenum.FileTypeEnum;
import com.example.jacob.springboot.demo.service.DownloadService;
import com.example.jacob.springboot.demo.service.TransformService;
import com.example.jacob.springboot.demo.utils.VerifyLicenseUtil;
import com.example.jacob.springboot.demo.utils.VerifyUrlUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 *
 * 转换核心执行方法
 *
 * 此demo仅模拟客户端、服务端之间通过http/https调用
 *
 * 已破解的文件:
 * 1、aspose-slide(19.6版本-高版本-最新版本20.2)
 * 2、aspose-word(19.1版本-高版本-最新版本20.2)
 * 3、aspose-pdf(17.8版本-低版本,高版本的目前还未找到破解方法)
 * 以上破解文件,已经全部重新打为jar包deploy到maven私服,位置在<a href="http://java.dev.anoah.com/artifactory/thirdparty/"/>
 *
 * 已实现的功能:
 * 1、传入文件的下载url,从url中获得输入流
 * 2、ppt、word到pdf的下载转换
 * 3、word、pdf到图片的下载转换
 * 4、进行网络下载时加重试策略(这里使用的是Spring自带的Retry,也可以使用OpenFeign来进行整体网络请求,feign也提供了Retryer重试接口的方法)
 *
 * 待实现的功能:
 * 1、未支持本地转换(本地转换的机制(消息队列的发布/订阅模式来通知转换服务进行转换)、触发策略)
 * 3、未支持接收客户端自定义参数(例如转图片时,图片转换的像素、要生成的图片格式等)
 * 4、加入了线程池,但还没有进行多线程调用,以及多线程调用之后,执行结果的下推(可采用客户端传入回调地址进行回调 或者 客户端对消息队列进行订阅,转换处理之后发布到对应的事件频道即可)
 *
 * @author duhongbo
 * @date 2020/3/27 11:24
 */
@RestController
@RequestMapping(value = "convertFile")
public class TransformController {
    private static final String TEST_WORD_DOWNLOAD_URL= "https://proj289test.anoah.com/file/download/appd883fd21fb99/beikeDoc/20200320/150/c_1240836857537789953_20200320110535126.doc";
    private static final String TEST_PPT_DOWNLOAD_URL = "https://proj289test.anoah.com/file/download/appd883fd21fb99/beikeDoc/20200327/82/c_1243440738071511042_20200327153228626.ppt";
    private static final String TEST_PDF_DOWNLOAD_URL = "https://proj289test.anoah.com/file/download/appd883fd21fb99/beikeDoc/20200326/139/c_1242999937239646210_20200326102053515.pdf";
    private  Logger logger = LoggerFactory.getLogger(TransformController.class);

    @Autowired
    TransformService transformService;
    @Value("${save-dir.ppt-pdf-save-dir}")
    private String pptSaveDir;
    @Value("${save-dir.word-pdf-save-dir}")
    private String wordSaveDir;
    @Value("${save-dir.word-picture-save-dir}")
    private String picSaveDir;
    @Value("${save-dir.pdf-picture-save-dir}")
    private String pdfPicSaveDir;
    @javax.annotation.Resource(name = "threadPoolTaskExecutor")
    private ThreadPoolTaskExecutor taskExecutor;
    @Autowired
    DownloadService downloadService;


    /**
     * word转PDF
     * @author duhongbo
     * @param downloadUrl word下载地址
     * @return java.lang.String
     */
    @PostMapping(value = "convertWordDownload")
    public String convertWord(@RequestParam String downloadUrl) {
       if (!VerifyUrlUtil.isHttpUrl(downloadUrl)){
           return "url无效";
       }
       if (!VerifyLicenseUtil.verifyWordsLicense()){
           return "许可证错误";
       }
        String nowDateString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
       // 创建目录
        File wordDir =this.createDir(wordSaveDir,nowDateString);

        File file = new File(wordDir.getPath()+"/"+nowDateString+"_"+RandomUtils.nextInt()+"."+FileTypeEnum.PDF.getFileExtionName());
        try(InputStream word = Objects.requireNonNull(downloadService.downLoadFromPath(downloadUrl));
            FileOutputStream fileOutputStream = new FileOutputStream(file)){
            TimeInterval timer = DateUtil.timer();
            Document document = new Document(word);
            document.save(fileOutputStream, com.aspose.words.SaveFormat.PDF);
           return this.recordLog(downloadUrl,timer.interval()/1000,file.getPath());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("word转pdf出错",e);
            FileUtils.deleteQuietly(file);
        }
        return null;
    }



    /**
     * PPT转PDF
     * @author duhongbo
     * @param downloadUrl PPT下载地址
     * @return java.lang.String
     */
    @PostMapping(value = "convertPPTDownload")
    public String convertPptDownload(@RequestParam String downloadUrl) {
        if (!VerifyUrlUtil.isHttpUrl(downloadUrl)){
            return "ppt文件下载地址无效";
        }
        if (!VerifyLicenseUtil.verifySlidesLicense()) {
            logger.error("许可证无效了");
            return "ppt许可证过期";
        }
        String nowDateString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        // 创建目录
        File dir = this.createDir(pptSaveDir,nowDateString);
        File file = new File(dir.getPath()+"/"+nowDateString+"_"+RandomUtils.nextInt()+"."+ FileTypeEnum.PDF.getFileExtionName());
        try(InputStream inputStream = downloadService.downLoadFromPath(downloadUrl);
            FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            TimeInterval timer = DateUtil.timer();
            Presentation pres = new Presentation(inputStream);
            pres.save(fileOutputStream, SaveFormat.Pdf);
            return this.recordLog(downloadUrl,timer.interval()/1000,file.getPath());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("PPT转换PDF出错",e);
            FileUtils.deleteQuietly(file);
        }
        return null;
    }

    /**
     * word转图片
     * @author duhongbo
     * @param downloadUrl word的下载地址
     * @return java.lang.String
     */
    @PostMapping(value = "convertWordToImage")
    public String convertWordToImage(@RequestParam String downloadUrl){
        if (!VerifyUrlUtil.isHttpUrl(downloadUrl)){
            return "word文件下载无效";
        }
        if (!VerifyLicenseUtil.verifyWordsLicense()){
            return "word许可证过期";
        }
        String nowDateString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int dirNum = RandomUtils.nextInt();
        File targetDir = this.createDir(picSaveDir,nowDateString,dirNum);
        FileOutputStream picOutputStream = null;
        try (InputStream picInputStream = Objects.requireNonNull(downloadService.downLoadFromPath(downloadUrl))){
            Document document = new Document(picInputStream);
            ImageSaveOptions imageSaveOptions = new ImageSaveOptions(com.aspose.words.SaveFormat.PNG);
            TimeInterval timer = DateUtil.timer();
            for (int i = 1; i < document.getPageCount(); i++) {
                File pngFile = new File(targetDir.getPath() + "/"+nowDateString+"_"+dirNum+"_"+ i + "."+FileTypeEnum.PNG.getFileExtionName());
                picOutputStream = new FileOutputStream(pngFile);
                imageSaveOptions.setPageIndex(i);
                document.save(picOutputStream, imageSaveOptions);
                picOutputStream.flush();
            }
           return this.recordLog(downloadUrl,timer.interval()/1000,targetDir.getPath());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("word转图片出错",e);
            try {
                FileUtils.deleteDirectory(targetDir);
            } catch (IOException e1) {
                e1.printStackTrace();
                logger.error("删除文件夹错误:"+targetDir.getPath(),e1);
            }
        }finally {
            IOUtils.closeQuietly(picOutputStream);
        }
        return null;
    }

    /**
     * PDF转图片
     * @author duhongbo
     * @param downloadUrl pdf下载地址
     * @return java.lang.String
     */
    @PostMapping(value = "convertPdfToImage")
    public String convertPdfToImage(@RequestParam String downloadUrl){
        if (!VerifyUrlUtil.isHttpUrl(downloadUrl)){
            return "pdf文件下载路径错误";
        }
        if (!VerifyLicenseUtil.verifyPdfLicense()){
            return "pdf许可证过期";
        }
        String nowDateString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int dirNum = RandomUtils.nextInt();
        File picSaveDir = this.createDir(pdfPicSaveDir,nowDateString,dirNum);
        FileOutputStream fileOutputStream = null;
        try (InputStream inputStream = Objects.requireNonNull(this.downloadService.downLoadFromPath(downloadUrl))){
            com.aspose.pdf.Document document = new com.aspose.pdf.Document(inputStream);
            // 设置图片的像素,这里使用默认配置
            PngDevice pngDevice = new PngDevice();
            TimeInterval timeInterval = DateUtil.timer();
            for (int i = 1;i<document.getPages().size();i++){
                File pngFile = new File(picSaveDir.getPath()+"/"+nowDateString+"_"+dirNum+"_"+i+"."+FileTypeEnum.PNG.getFileExtionName());
                fileOutputStream = new FileOutputStream(pngFile);
                pngDevice.process(document.getPages().get_Item(i),fileOutputStream);
                fileOutputStream.flush();
            }
            return this.recordLog(downloadUrl,timeInterval.interval()/1000,picSaveDir.getPath());
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("pdf转图片出错",e);
            try {
                FileUtils.deleteDirectory(picSaveDir);
            } catch (IOException e1) {
                e1.printStackTrace();
                logger.error("删除文件夹错误:"+picSaveDir.getPath(),e1);
            }
        }finally {
            IOUtils.closeQuietly(fileOutputStream);
        }
        return null;
    }

    @RequestMapping(value = "addStamp")
    public  String pdfAddStamp(String pdfPath){
        if (!VerifyLicenseUtil.verifyPdfLicense()){
            return "许可证错误";
        }
        com.aspose.pdf.Document pdfDocument = new com.aspose.pdf.Document(pdfPath);
        for (int i = 1;i<pdfDocument.getPages().size();i++){
            pdfDocument.getPages().get_Item(i).addStamp(this.insertText());
        }
        File file = new File("test.pdf");
        pdfDocument.save(file.getPath());
        return file.getPath();
    }

    /**
     * 简单的加水印
     * @author duhongbo
     * @param
     * @return com.aspose.pdf.TextStamp
     */
    private TextStamp insertText(){
        TextStamp textStamp1 = new TextStamp("优学天下");
        textStamp1.setBackground(true);
        textStamp1.setXIndent(200);
        textStamp1.setYIndent(400);
        textStamp1.setRotate(Rotation.None);
        textStamp1.getTextState().setFont( FontRepository.findFont("Arial"));
        textStamp1.getTextState().setFontSize(34.0F);
        textStamp1.getTextState().setFontStyle(FontStyles.Regular);
        textStamp1.getTextState().setForegroundColor(Color.getBlack());
        textStamp1.getTextState().setBackgroundColor(Color.getTransparent());
        return textStamp1;
    }

    /**
     * 创建文件目录
     * @author duhongbo
     * @param dirPath 文件保存根目录
     * @param dateStr 时间格式字符串
     * @return java.io.File
     */
    private File createDir(String dirPath,String dateStr){
       return this.createDir(dirPath,dateStr,null);
    }


    /**
     * 统一创建文件保存目录
     * @author duhongbo
     * @param dirPath 文件保存根目录
     * @param dateStr 时间格式字符串
     * @return java.io.File
     */
    private File createDir(String dirPath,String dateStr,Integer childDirNum){
        File file = new File(dirPath+"/"+dateStr);
        if (!file.exists()){
            file.mkdirs();
        }
        if (ObjectUtil.isNotNull(childDirNum)){
            File targetDir = new File(file.getPath()+"/"+childDirNum);
            if (!targetDir.exists()){
                targetDir.mkdir();
            }
            return targetDir;
        }
        return file;
    }

    /**
     * 返回文件保存位置
     * @author duhongbo
     * @param downloadUrl 文件下载地址
     * @param seconds 转换耗时
     * @param targetPath 文件最终保存位置
     * @return java.lang.String
     */
    private String recordLog(String downloadUrl,Long seconds,String targetPath){
        String infoStr = "原始文件下载地址:{},共耗时:{}秒,文件保存在:{}";
        String resultStr = "文件保存在:{}";
        logger.info(infoStr,downloadUrl,seconds,targetPath);
        return StrUtil.format(resultStr,targetPath);
    }


     /**
      * 一直执行四个转换方法
      * @author duhongbo
      * @param
      * @return void
      */
    @SuppressWarnings("InfiniteLoopStatement")
    @PostConstruct
    public void alwaysRun(){

            taskExecutor.execute(()->{
                while (true){
                    this.convertWordToImage(TEST_WORD_DOWNLOAD_URL);
                }
            });
            taskExecutor.execute(()->{
                while (true){
                    this.convertPdfToImage(TEST_PDF_DOWNLOAD_URL);
                }
            });
            taskExecutor.execute(()->{
                while (true){
                this.convertPptDownload(TEST_PPT_DOWNLOAD_URL);
            }
            });
            taskExecutor.execute(()->{
                while (true){
                this.convertWord(TEST_WORD_DOWNLOAD_URL);
            }
            });

        System.out.println("程序运行中.....为防止文件夹过大,每5分钟清理一次所有文件夹.....");
    }

    // 没有破解许可证的情况下会有水印
    //    public synchronized void ppt2Pdf(String filePath,String savePath){
    //        Presentation ppt = new Presentation(filePath);
    //        System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss SSS")));
    //        ppt.save(savePath, SaveFormat.Pdf);
    //        System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss SSS")));
    //    }

    // 使用jacob的示例代码
    //    @PostMapping(value = "convertByJacob")
    //    public Integer convertByJacob() {
    //        File dir = new File("D:\\jmeterPPT");
    //        File[] listFiles = dir.listFiles();
    //        Integer n = RandomUtils.nextInt();
    //
    //        File file = new File("D:\\jmeterPPT\\pdfdata");
    //        if (!file.exists()) {
    //            file.mkdir();
    //        }
    //        logger.info("第" + n + "个PPT转换开始");
    //
    //        return 1;
    //    }

}
