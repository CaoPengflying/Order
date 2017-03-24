<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="bean.*" %>
<%@ page import="DAO.*" %>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

	Admin user = (Admin) request.getSession().getAttribute("user");
	int target = Integer.parseInt(request.getParameter("target"));
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<base href="<%=basePath%>">

<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>车间管理</title>

<link rel="stylesheet" type="text/css" href="easyUI/themes/default/easyui.css" />
<link rel="stylesheet" type="text/css" href="easyUI/themes/icon.css" />
<script type="text/javascript" src="easyUI/jquery.min.js"></script>
<script type="text/javascript" src="easyUI/jquery.easyui.min.js"></script>
<script type="text/javascript" src="easyUI/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript" src="datagrid_view/datagrid-detailview.js"></script>

<script type="text/javascript">
var editIndex = undefined;
var companys = undefined;
initCompanys();
$(function(){
	loadCompanys();
	
});	

function initCompanys(){
	$.ajaxSetup({ async: false }); 
	$.post("/Order/CompanyServlet",
		{
			method:1,
	       	timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
       },
       function(result){
	       	var data = JSON.parse(result);
	       	companys = data.rows;
	  	}
	);
	$.ajaxSetup({ async: true }); 
}	

function loadCompanys(){
	$.post("/Order/CompanyServlet",
		{
			method:1,
	       	timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
       },
       function(result){
	       	var data = JSON.parse(result);
	       	var temp = data.rows;
	       	
       		var empty = new Object;//添加一个“不限”的选项
	       	empty.ID = 0;
	       	empty.name = "不限";
	       	temp.splice(0,0,empty);
	       	
	        $('#combo_company').combobox({
	        	data:temp
	        });  
	        
	        $('#combo_company').combobox('select',temp[0].ID);//选中第一个车间     		
	  	}
	);
}	

function loadWorkshops(companyID){
	$('#datagrid_workshops').datagrid({  
	    url:'/Order/WorkshopServlet', 
	    pagination:true,
		pageSize:15,
		pageList:[15,20],
	    queryParams:{  
	       method:1, 
	       pagination:true,
	       companyID:companyID,
	       timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
	    },
		method:'post'
	}); 
}	
//当选择公司 加载公司所属车间
function onSelectCompany(record) {
	loadWorkshops(record.ID);
}

function endEditing(){
	if (editIndex == undefined){
		return true;
	}

	if ($('#datagrid_workshops').datagrid('validateRow', editIndex)){
		$('#datagrid_workshops').datagrid('endEdit', editIndex);
		editIndex = undefined;
		return true;
	} else {
		return false;
	}
}

//编辑车间信息
function edit(rowIndex,rowData){
	if (editIndex != rowIndex && endEditing()){
		$('#datagrid_workshops').datagrid('beginEdit', rowIndex);
		editIndex = rowIndex;
	}
	// 得到单元格对象,index指哪一行,field跟定义列的那个一样
	var cellEdit = $('#datagrid_workshops').datagrid('getEditor', {index:rowIndex,field:'companyID'});
	//var $input = cellEdit.target; // 得到文本框对象
	//$input.prop('readonly',true); // 设值只读
	$(cellEdit.target).combobox('readonly',true);
}
//添加车间
function append(){
	if (endEditing()){
		$('#datagrid_workshops').datagrid('insertRow',{index:0,row:{
			name:"",
			phone:""}});
		editIndex = 0;
		$('#datagrid_workshops').datagrid('selectRow', editIndex).datagrid('beginEdit', editIndex);
	}
}
//删除车间
function remove(){
	var rows = $('#datagrid_workshops').datagrid('getChecked');
	for(var i=0; i<rows.length; i++){
		var index = $('#datagrid_workshops').datagrid('getRowIndex',rows[i]);
		$('#datagrid_workshops').datagrid('deleteRow',index);
	}
}

//保存变更（包括添加、删除、修改）
function save(){
	if (!endEditing()){
		return;
	}
	
	var msg = "确定要保存修改吗？";
	if(confirm(msg) == false){
		return;
	}
	//获取变更的记录
	var rows_inserted = $('#datagrid_workshops').datagrid('getChanges','inserted');
	var rows_deleted = $('#datagrid_workshops').datagrid('getChanges','deleted');
	var rows_updated = $('#datagrid_workshops').datagrid('getChanges','updated');
	
	//转换为json字符串
	var json_inserted = JSON.stringify(rows_inserted);
	var json_deleted = JSON.stringify(rows_deleted);
	var json_updated = JSON.stringify(rows_updated);
	
	//提交后台执行变更
	$.post("/Order/WorkshopServlet",
		{
			method:2,
			inserted:json_inserted,
			deleted:json_deleted,
			updated:json_updated,
	       	timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
       },
       function(result){
	       	//提交变更数据  
	        $('#datagrid_workshops').datagrid('acceptChanges');
       		//提示
       		$.messager.show({
				title:'编辑公司信息',
				timeout:3000,
				msg:'保存成功',
				width:200,
				showType:'slide'
			});
	  }
	);	
}
//公司ID至name的格式化
function companyFormatter(value, rowData, rowIndex) {	
	for(var i=0; i<companys.length; i++){
		if(value == companys[i].ID){
			return companys[i].name;
		}
	}
	return "";
}

//放弃变更（未保存的变更操作）
function reject(){
	$('#datagrid_workshops').datagrid('rejectChanges');
	editIndex = undefined;
}

//自定义验证(扩展easyui验证)
$.extend($.fn.validatebox.defaults.rules, {
 //移动手机号码验证  
    mobile: {//value值为文本框中的值  
        validator: function (value) {  
            //var reg = /^1[3|4|5|8|9]\d{9}$/;  
            return /^((\d2,3)|(\d{3}\-))?(0\d2,3|0\d{2,3}-)?[1-9]\d{6,7}(\-\d{1,4})?$/i.test(value) || /^(13|15|18)\d{9}$/i.test(value); 
        },  
        message: '输入电话或手机号码格式不准确.'  
        
    }
    });

</script>
		
</head>

<body class="easyui-layout">
	<div align="center"  data-options="region:'north',collapsible:false" title='' style="height:100px;background-color:#D6E6DE">
		<%@include file="../../inc/header.inc" %> 	
	</div>
	<div data-options="region:'west',collapsible:true" title='功能导航' style="width:120px;">
		<%@include file="../../inc/menu/menu_admin.inc" %> 	
	</div>			
	<div data-options="region:'south'" style="height:50px;background-color:#D6E6DE" align="center">
		<%@include file="../../inc/footer.inc" %> 	
	</div>				
	<div data-options="region:'east',title:'帮助',collapsible:true,collapsed:true,width:250" style="padding:10px;">
		<%@include file="../../inc/help/admin/workshop.inc" %>
	</div>
	<div data-options="region:'center',title:'车间管理'" >
		<table  class="easyui-datagrid" id="datagrid_workshops" 
		data-options="toolbar:'#toolbar_workshop',fit:true,striped:true,checkOnSelect:true,selectOnCheck:false,singleSelect:true,onDblClickRow: edit" >
			<thead>
				<tr>
					<th data-options="field:'selector',checkbox:true"></th>
					<th data-options="field:'ID',width:200">车间编号</th>
					<th data-options="field:'name',width:200,editor:{type:'textbox',options:{required:true}}">车间名称</th>
					<th data-options="field:'phone',width:100,editor:{type:'textbox',options:{validType:'mobile'}}">电话</th>
					<th data-options="field:'companyID',width:100,halign:'center',formatter:companyFormatter,editor:{
							type:'combobox',
							options:{
								required:true,
								editable:false,
								valueField:'ID',
								textField:'name',
								data:companys
							}}">所属公司</th>
				</tr>
			</thead>
		</table>
		<div id="toolbar_workshop" style="height:30px;line-height: 30px;">
			<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-add'" onclick="append()">添加</a>
			<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-remove'" onclick="remove()">删除</a>
			<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-save'" onclick="save()">保存</a>
			<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-undo'" onclick="reject()">撤销</a>
			公司<input class="easyui-combobox"id="combo_company" data-options="valueField:'ID',textField:'name', onSelect:onSelectCompany" />
		</div>
	</div>		
			
</body>
</html>
