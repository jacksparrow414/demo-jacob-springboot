package com.example.jacob.springboot.demo.service;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

/**
 *
 * 破解jar包Service
 * @author duhongbo
 * @date 2020/3/18 11:10
 *
 */
public class CrackJarService {

    public static void main(String[] args) {
        try {
            CrackJarService.crackMethod();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 破解aspose-word的许可证
     *
     * @param
     * @return void
     * @author duhongbo
     */
    private static void crackMethod() throws Exception {
        ClassPool.getDefault().insertClassPath("G:\\BaiduNetdiskDownload\\aspose-words-18.6-jdk16-crack.jar");
        CtClass ctClass = ClassPool.getDefault().getCtClass("com.aspose.words.zzZLX");
        CtMethod zzX = ctClass.getDeclaredMethod("zzX");
        zzX.setBody("{this.zzYSr = new java.util.Date(Long.MAX_VALUE);this.zzYSq = 1;zzYSp=this;}");
        ctClass.writeFile();
    }
}
