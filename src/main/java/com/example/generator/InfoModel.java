package com.example.generator;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: lirisheng
 * @Date: 2021/6/10 10:23
 * @Version 1.0
 */
public class InfoModel {

    private String name1;
    private String name2;
    // 内部实体类集合
    List<FiledInfo> filedInfos = new ArrayList<>();


    // 声明内部实体类 ,用来表示 字段的名称和类型
    public class FiledInfo{
       private String upFileName;
       private String doFileName;
       private String filedType;

        public String getUpFileName() {
            return upFileName;
        }

        public void setUpFileName(String upFileName) {
            this.upFileName = upFileName;
        }

        public String getDoFileName() {
            return doFileName;
        }

        public void setDoFileName(String doFileName) {
            this.doFileName = doFileName;
        }

        public String getFiledType() {
            return filedType;
        }

        public void setFiledType(String filedType) {
            this.filedType = filedType;
        }

        @Override
        public String toString() {
            return "FiledInfo{" +
                    "upFileName='" + upFileName + '\'' +
                    ", doFileName='" + doFileName + '\'' +
                    ", filedType='" + filedType + '\'' +
                    '}';
        }
    }

    // 提供实例和添加内部对象的方法
    public void addFiledInfo(String upFiledName, String doFiledName, String filedType){
        FiledInfo filedInfo = new FiledInfo();
        filedInfo.setUpFileName(upFiledName);
        filedInfo.setDoFileName(doFiledName);
        filedInfo.setFiledType(filedType);
        filedInfos.add(filedInfo);
    }

    public String getName1() {
        return name1;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public List<FiledInfo> getFiledInfos() {
        return filedInfos;
    }

    public void setFiledInfos(List<FiledInfo> filedInfos) {
        this.filedInfos = filedInfos;
    }

    @Override
    public String toString() {
        return "InfoModel{" +
                "name1='" + name1 + '\'' +
                ", name2='" + name2 + '\'' +
                ", filedInfos=" + filedInfos +
                '}';
    }
}
