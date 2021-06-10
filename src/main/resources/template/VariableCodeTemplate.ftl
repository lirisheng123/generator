<#--package ${object.packageStr};-->

<#--public class ${object.name} {-->

<#--<#list object.props as item>-->
    <#--private ${item.propType} ${item.propName};-->

<#--</#list>-->


<#--<#list object.props as item>-->
    <#--public void set${item.pascaPropName} (${item.propType} ${item.propName}) {-->
    <#--this.${item.propName} = ${item.propName};-->
    <#--}-->


    <#--public ${item.propType} get${item.pascaPropName} () {-->
    <#--return this.${item.propName};-->
    <#--}-->

<#--</#list>-->

<#--}-->

<#--<#list object.upFiledName as item>-->
    <#--private ${object.filedType[item_index]} ${object.doFiledName[item_index]} = ${object.name1}.get${item}();-->
<#--</#list>-->

<#list object.filedInfos as item>
    private ${item.filedType} ${item.doFileName} = ${object.name1}.get${item.upFileName}();
</#list>
