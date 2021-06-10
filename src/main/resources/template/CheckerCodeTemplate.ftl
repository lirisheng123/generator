

<#list object.upFiledName as item>

    private ${object.filedType[item_index]} ${object.doFiledName[item_index]} = ${object.name1}.get${item}();
</#list>