package com.example.generator;


import freemarker.template.Configuration;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Generated;
import java.io.*;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @Author: lirisheng
 * @Date: 2021/6/10 10:22
 * @Version 1.0
 */

public class Generator {

    private static final Logger logger = LoggerFactory.getLogger(Generated.class);


    private static final String TEMPLATE_PATH = "src\\main\\resources\\template";
    private static final String CLASS_PATH = "src\\main\\java\\com\\example\\generator\\model";
    private static final String PACKAGE = "com.mrh.spring.generator.model";


    public static void generateVariableCode(String className,String name1){
        generateCode(className,name1,null,1);
    }

    public static void generateCheckerCode(String className,String name1){
        generateCode(className,name1,null,2);
    }

    public static void generateSetVariableCode(String className,String name1){
        generateCode(className,name1,null,3);
    }

    public static void generateSetGetCode(String className,String name1,String name2){
        generateCode(className,name1,name2,4);
    }



    /**
     * 描述:根据类来生成相应的业务代码
     * type的值:
     *     1: 表示  String var1 =  input.getVar1()
     *     2: 表示  生成校验
     *     3: 表示  output.setVar1(var1)
     *     4: 表示  output.setVar1(input.getVar1())
     * @param className
     * @param name1
     * @param name2
     * @param type
     */
    public static void generateCode(String className,String name1,String name2,int type){

        //获取数据
        InfoModel infoModel = getInfoMode(className,name1,name2);

        //生成模板
        if(type==1){
            generate(infoModel,"VariableCode.java","VariableCodeTemplate.ftl");
        }else if(type==2){
            generate(infoModel,"CheckerCode.java","CheckerCodeTemplate.ftl");
        }else if(type==3){
            generate(infoModel,"SetVariableCode.java","SetVariableCodeTemplate.ftl");
        }else if (type==4){
            generate(infoModel,"SetGetCode.java","SetGetCodeTemplate.ftl");
        }else{
            System.out.println("type 范围值不对");
            throw  new RuntimeException("type 范围值不对");
        }

    }


    /**
     * 根据类来收集数据,并转化为infoMode实体类
     * @param className
     * @param name1
     * @param name2
     * @return
     */
    public static InfoModel getInfoMode(String className,String name1,String name2){

        InfoModel infoMode = new InfoModel();
        infoMode.setName1(name1);
        infoMode.setName2(name2);

        try{
            Class object = Class.forName(className);
            Method[] methodList = object.getDeclaredMethods();
            String pattern  = "get.*";

            for(Method item : methodList){
                String methodName = item.getName();
                if(Pattern.matches(pattern,methodName)){

                    //获取返回值类型
                    String [] typeList = item.getReturnType().getName().split("\\.");

                    infoMode.addFiledInfo(methodName.substring(3),lowerFirstCase(methodName.substring(3)),typeList[typeList.length-1]);
                }
            }
        }catch (ClassNotFoundException exception){
            System.out.println("class not found ");
            throw  new RuntimeException("class not found ");

        }catch (Exception exception){

            System.out.println("unRecognized error :"+exception.getCause());
            throw  new RuntimeException("unRecognized error :"+exception.getCause());
        }

        System.out.println("infoMode:"+infoMode.toString());
        return infoMode;
    }


    private static void generate(Object o, String fileName, String templateName) {
        Configuration configuration = new Configuration();
        Writer out = null;
        // String path = Thread.currentThread().getContextClassLoader().getResource("template").getPath();

        try {

            // step2 获取模版路径
            configuration.setDirectoryForTemplateLoading(new File(TEMPLATE_PATH));
            // step3 创建数据模型
            Map<String, Object> dataMap = new HashMap<String, Object>();
            dataMap.put("object", o);
            // step4 加载模版文件
            Template template = configuration.getTemplate(templateName);
            // step5 生成数据
            File docFile = new File(CLASS_PATH + "/" + fileName);
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(docFile)));
            // step6 输出文件
            template.process(dataMap, out);
            System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^"+fileName+" 文件创建成功 !");
        } catch (Exception e) {

            System.out.println("生成模板代码失败");
            e.printStackTrace();
            return;
        } finally {

            try {
                if (null != out) {
                    out.flush();
                }
            } catch (Exception e2) {
                System.out.println("关闭文件失败");
                e2.printStackTrace();
            }
        }
    }


    /**
     * 首字母小写
     * @param str
     * @return
     */
    public static String lowerFirstCase(String str){

        char[] chars = str.toCharArray();
        //首字母小写方法，大写会变成小写，如果小写首字母会消失
        chars[0] +=32;
        return String.valueOf(chars);
    }

    /**
     * 首字母大写
     * @param str
     * @return
     */
    public static String upperFirstCase(String str){
        char[] chars = str.toCharArray();
        //首字母小写方法，大写会变成小写，如果小写首字母会消失
        chars[0] -=32;
        return String.valueOf(chars);
    }

    public static void main(String arg[]){

        generateVariableCode("com.example.generator.Account","input");

    }

}
