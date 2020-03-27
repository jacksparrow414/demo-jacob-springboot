package com.example.jacob.springboot.demo.demoenum;

/**
 * 文件类型枚举类
 * @author duhongbo
 * @date 2020/3/12 13:40
 */
public enum FileTypeEnum {

    /**
     * ppt
     */
    PPT("ppt"),
    /**
     * pptx
     */
    PPTX("pptx"),
    /**
     * pdf
     */
    PDF("pdf"),
    /**
     * png
     */
    PNG("png"),
    /**
     * jpg
     */
    JPG("jpg"),
    /**
     * word-doc
     */
    DOC("doc"),
    /**
     * word-docx
     */
    DOCX("docx");
    private String fileExtionName;

    FileTypeEnum(String fileExtionName) {
        this.fileExtionName = fileExtionName;
    }

    public String getFileExtionName() {
        return fileExtionName;
    }}
