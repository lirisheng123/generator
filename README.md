使用Springboot和FreeMarker生成业务代码



# 背景

- 由于在实际的项目中,get,set,以及对字段的校验占了任务代码的很大一部分,又耗时且容易出错,又由于这些代码很多都是重复有规律可循,所以就萌发出根据实体类来生成相应的get,set,检验代码的想法.

# 技术

   Springboot , FreeMarker

# 过程

这里以生成`private String var1 = input.getVar1()`代码为例

1. ​    在set,get ,检验代码中抽取出可变的元素,集合成一个`InfoModel`实体类,用于后续代码的生成

   ```
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
       
       //提供实例和添加内部对象的方法
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
   
   ```

   

2.  从实体类中获取信息来实例出一个`InfoModel`实体类

   ```
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
   
                       //获取字段的首字母大写名称,首字母小写名称以及类型
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
   
   ```
   
   
   
3. freemark代码模板,名为`VariableCodeTemplate.ftl`

   ```
   <#list object.filedInfos as item>
       private ${item.filedType} ${item.doFileName} = ${object.name1}.get${item.upFileName}();
   </#list>
   ```

   

4. 根据`InfoModel`实体类中信息和freemark模板来生成代码

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

  

  

