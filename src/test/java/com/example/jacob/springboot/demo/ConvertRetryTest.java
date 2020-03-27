package com.example.jacob.springboot.demo;

import com.example.jacob.springboot.demo.service.DownloadService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author duhongbo
 * @date 2020/3/27 11:57
 */
@SpringBootTest
public class ConvertRetryTest {

    @Autowired
    DownloadService downloadService;

    @Test
    public void TestConvertRetry(){
        downloadService.downLoadFromPath("http://47.114.184.94:80");
    }
}
