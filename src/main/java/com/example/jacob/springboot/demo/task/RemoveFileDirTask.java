package com.example.jacob.springboot.demo.task;

import cn.hutool.core.io.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * 简单定时任务:定时清理文件夹
 * 此方法可能会出现异常:当线程池里的四个线程正在向文件夹里写的时候,此时删除文件,写流异常,因为文件被当前定时任务删了
 * @author duhongbo
 * @date 2020/3/27 14:07
 */
@Component
public class RemoveFileDirTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoveFileDirTask.class);

    @Value("${save-dir.ppt-pdf-save-dir}")
    private String pptSaveDir;
    @Value("${save-dir.word-pdf-save-dir}")
    private String wordSaveDir;
    @Value("${save-dir.word-picture-save-dir}")
    private String picSaveDir;
    @Value("${save-dir.pdf-picture-save-dir}")
    private String pdfPicSaveDir;

    @Scheduled(initialDelay = 2 * 60 * 1000,fixedDelay = 5 * 60 * 1000)
    public void removeFile() {
        LOGGER.info("开始清理文件夹下的内容,当前时间----->{}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        FileUtil.del(pptSaveDir);
        FileUtil.del(pptSaveDir);
        FileUtil.del(wordSaveDir);
        FileUtil.del(picSaveDir);
        FileUtil.del(pdfPicSaveDir);
        LOGGER.info("结束清理文件夹下的内容,当前时间----->{}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
}
