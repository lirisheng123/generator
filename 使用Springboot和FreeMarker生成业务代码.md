使用Springboot和FreeMarker生成业务代码



# 背景

- 由于在实际的项目中,get,set,以及对字段的校验占了任务代码的很大一部分,又耗时且容易出错,又由于这些代码很多都是重复有规律可循,所以就萌发出根据实体类来生成相应的get,set,检验代码的想法.

# 技术

   Springboot , FreeMarker

# 过程

这里以生成`private String var1 = input.getVar1()`代码为例

1. ​    在set,get ,检验代码中抽取出可变的元素,集合成一个`InfoModel`实体类,用于后续代码的生成

   ```
   package com.lirisheng.javagrammar.reflect;
   
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
       private List<String>  upFiledName = new ArrayList<>();
       private List<String>  doFiledName = new ArrayList<>();
       private List<String>  filedType = new ArrayList<>();
   
       public class filedInfo{
   
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
   
       public List<String> getUpFiledName() {
           return upFiledName;
       }
   
       public void setUpFiledName(List<String> upFiledName) {
           this.upFiledName = upFiledName;
       }
   
       public List<String> getDoFiledName() {
           return doFiledName;
       }
   
       public void setDoFiledName(List<String> doFiledName) {
           this.doFiledName = doFiledName;
       }
   
       public List<String> getFiledType() {
           return filedType;
       }
   
       public void setFiledType(List<String> filedType) {
           this.filedType = filedType;
       }
   
       @Override
       public String toString() {
           return "InfoModel{" +
                   "name1='" + name1 + '\'' +
                   ", name2='" + name2 + '\'' +
                   ", upFiledName=" + upFiledName +
                   ", doFiledName=" + doFiledName +
                   ", filedType=" + filedType +
                   '}';
       }
   }
   
   ```

   

2.  从实体类中获取信息来实例出一个`InfoModel`实体类

   ```
    public static InfoModel getInfoMode(String className,String name1,String name2){
   
           InfoModel infoMode = new InfoModel();
           infoMode.setName1(name1);
           infoMode.setName2(name2);
   
           try{
               Class object = Class.forName(className);
               Method [] methodList = object.getDeclaredMethods();
               String pattern  = "get.*";
   
               List<String>  upFiledName = new ArrayList<>();
               List<String>  doFiledName = new ArrayList<>();
               List<String>  filedType = new ArrayList<>();
   
               for(Method item : methodList){
                   String methodName = item.getName();
                   if(Pattern.matches(pattern,methodName)){
   
                       //获取大写字段名
                       upFiledName.add(methodName.substring(3));
   
                       //获取小写字段名
                       doFiledName.add(lowerFirstCase(methodName.substring(3)));
   
                       //获取返回值类型
                       String [] typeList = item.getReturnType().getName().split("\\.");
                       filedType.add(typeList[typeList.length-1]);
                   }
               }
               infoMode.setUpFiledName(upFiledName);
               infoMode.setDoFiledName(doFiledName);
               infoMode.setFiledType(filedType);
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
   ```

   

3. freemark代码模板,名为`VariableCodeTemplate.ftl`

   ```
   <#list object.upFiledName as item>
       private ${object.filedType[item_index]} ${object.doFiledName[item_index]} =    ${object.name1}.get${item}();
   </#list>
   ```

   

4.  根据`InfoModel`实体类中信息和freemark模板来生成代码

   ```
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
       
    private static void generate(Object o, String fileName, String templateName) {
           Configuration configuration = new Configuration();
           Writer out = null;
           logger.debug("开始获取path路劲");
           String path = Thread.currentThread().getContextClassLoader().getResource("template").getPath();
           logger.debug("path:"+path);
           try {
               // step2 获取模版路径
   //            configuration.setDirectoryForTemplateLoading(new File(path));
               configuration.setDirectoryForTemplateLoading(new File("E:\\课程相关内容\\spring-boot\\JavaGrammar\\src\\main\\resources\\template"));
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
   ```

5. 测试

   ```
    public static void main(String arg[]){
             generateVariableCode("com.lirisheng.javagrammar.reflect.Account","input");
             
       }
   ```

   结果:

   ![](https://raw.githubusercontent.com/lirisheng123/images/master/img/20210610174335.png)

   
   
   

# 代码仓库:

- https://github.com/lirisheng123/generator.git


# 参考:

- 【JAVA实战】手写代码生成器-根据mysql表结构生成实体类https://blog.csdn.net/sinat_38232376/article/details/88851526

- FreeMarker的ftl语法https://blog.csdn.net/guohao_1/article/details/103223868

  

  

