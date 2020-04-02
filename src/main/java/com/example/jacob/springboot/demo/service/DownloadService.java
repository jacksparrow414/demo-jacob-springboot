package com.example.jacob.springboot.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * 根据url读取输入流
 * @author duhongbo
 * @date 2020/3/27 11:39
 */
@Service
public class DownloadService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadService.class);

    @Autowired
    RestTemplate restTemplate;
    /**
     *
     * 默认重试3次,第一次距离上次调用间隔5秒再调用,以后每次距离上次调用间隔 5秒 * multiplier
     * 读取文件流
     * @author duhongbo
     * @param downloadUrl 下载地址
     * @return java.io.InputStream
     */
    @Retryable(value ={ RuntimeException.class},maxAttempts = 3,backoff = @Backoff(delay =5000,multiplier = 2))
    public InputStream downLoadFromPath(String downloadUrl) {
        ResponseEntity<Resource> forEntity = restTemplate.getForEntity(downloadUrl, Resource.class);
        if (HttpStatus.OK.equals(forEntity.getStatusCode()) || HttpStatus.PARTIAL_CONTENT.equals(forEntity.getStatusCode())) {
            try(InputStream inputStream = Objects.requireNonNull(forEntity.getBody()).getInputStream()) {
                return inputStream;
            } catch (IOException e) {
                LOGGER.warn("下载地址 {} 进入重试",downloadUrl);
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    /**
     *
     * @author duhongbo
     * @param e 具体异常,放在第一个参数的位置
     * @param downloadUrl 和@Retryable注解标注的方法【参数】保持一致
     * @return java.io.InputStream 返回值和@Retryable注解标注的方法的【返回值】保持一致
     */
    @Recover
    public InputStream downLoadRecover(RuntimeException e,String downloadUrl){
        LOGGER.warn("执行补偿方法-------------");
        LOGGER.error("{} 下载url达到重试最大次数,无法下载",downloadUrl);
        LOGGER.error("重试异常",e);
        throw new RuntimeException(e);
    }
}
