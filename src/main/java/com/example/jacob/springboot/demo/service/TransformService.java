package com.example.jacob.springboot.demo.service;

import org.springframework.stereotype.Service;

/**
 * @author duhongbo
 * @date 2020/3/12 11:23
 */
@Service
public class TransformService {

//    private static final Logger LOGGER = LoggerFactory.getLogger(TransformService.class);
//    /**
//     * PDF转换格式对应的数字
//     */
//    private static final int SAVE_AS_PDF = 32;
//    private ActiveXComponent app;
//    private Dispatch document;
//    private Dispatch pptObject;
//    public boolean convertPdf(String inputFile, String pdfFile) {
//        String suffix = getFileSuffix(inputFile);
//        File file = new File(inputFile);
//        if (!file.exists()) {
//            return false;
//        }
//        if (FileTypeEnum.PDF.getFileExtionName().equals(suffix)) {
//            return false;
//        }
//        if (FileTypeEnum.PPT.getFileExtionName().equals(suffix) || FileTypeEnum.PPTX.getFileExtionName().equals(suffix)) {
//            LOGGER.info("进入PPT转PDF方法");
//            return pptToPdf(inputFile, pdfFile+"."+FileTypeEnum.PDF.getFileExtionName());
//        }  else {
//            LOGGER.error("不是PPT或者PPTx");
//            return false;
//        }
//    }
//
//     private static String getFileSuffix(String fileName) {
//        int splitIndex = fileName.lastIndexOf(".");
//        return fileName.substring(splitIndex + 1);
//    }
//
//    private synchronized boolean pptToPdf(String inputFile, String pdfFile) {
//
//        try {
//            ComThread.InitMTA(true);
//            // 打开应用程序
//            app = new ActiveXComponent("Powerpoint.Application");
//
//           // pptObject = app.getObject();
//
//            // 返回Document对象
//            Dispatch documentObject = app.getProperty("Presentations").toDispatch();
//
//            // 打开PPT,三个Boolean参数分别代表,是否只读、Untitled指定文件是否有标题、WithWindow指定文件是否可见
//             document = Dispatch.call(documentObject, "Open", inputFile, true,
//                    true,
//                    false
//            ).toDispatch();
//            // 另存为PDF
//            Dispatch.call(document, "SaveAs", pdfFile, SAVE_AS_PDF);
//            // 关闭PPT
//            Dispatch.call(document, "Close");
//
//            return true;
//        } catch (Exception e) {
//            LOGGER.error("ppt转pdf出错",e);
//            return false;
//        }finally {
//            if (app != null) {
//                // 退出Powerpoint.Application
//                app.invoke("Quit");
//            }
//            // 释放连接
//            ComThread.Release();
//        }
//    }
}
