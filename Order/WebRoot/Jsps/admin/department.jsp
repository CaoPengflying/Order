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
<title>班组管理</title>

<link rel="stylesheet" type="text/css" href="easyUI/themes/default/easyui.css" />
<link rel="stylesheet" type="text/css" href="easyUI/themes/icon.css" />
<script type="text/javascript" src="easyUI/jquery.min.js"></script>
<script type="text/javascript" src="easyUI/jquery.easyui.min.js"></script>
<script type="text/javascript" src="easyUI/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript" src="datagrid_view/datagrid-detailview.js"></script>

<script type="text/javascript">
var editIndex = undefined;
$(function(){
	loadCompanys();
});	

function loadCompanys(){
	$.post("/Order/CompanyServlet",
		{
			method:1,
	       	timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
       },
       function(result){
	       	var data = JSON.parse(result);
	       	var companys = data.rows;
	       	$('#combo_company_dst').combobox({
	        	data:companys
	        }); 
	        var empty = new Object;//添加一个“不限”的选项
	       	empty.ID = 0;
	       	empty.name = "不限";
	       	companys.splice(0,0,empty);
	        $('#combo_company').combobox({
	        	data:companys
	        });  
	        $('#combo_company').combobox('select',companys[0].ID);//选中第一个车间     		
	            		
	  	}
	);
}
//当选择公司 加载公司所属车间
function onSelectCompany(record) {
	$.post("/Order/WorkshopServlet",
		{
			method:1,
			companyID:record.ID,
	       	timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
       },
       function(result){
	       	var data = JSON.parse(result);
	       	var workshops = data.rows;
	        $('#combo_workshop').combobox({
	        	data:workshops
	        });  
	        
	        $('#combo_workshop').combobox('select',workshops[0].ID);//选中第一个车间     	
	  	}
	);
}	
function loadWorkshops(){
	$.post("/Order/WorkshopServlet",
		{
			method:1,
			pagination:false,
			companyID:0,
	       	timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
       },
       function(result){
	       	var data = JSON.parse(result);
	       	var workshops = data.rows;
	        $('#combo_workshop').combobox({
	        	data:workshops
	        });  
	        
	        $('#combo_workshop').combobox('select',workshops[0].ID);//选中第一个车间     		
	  	}
	);
}	

function onSelectWorkshop(record){
	loadDepartments(record.ID);
}

function loadDepartments(workshopID){
	$('#datagrid_departments').datagrid({  
	    url:'/Order/DepartmentServlet',  
	    queryParams:{  
	       method:1, 
	       workshopID:workshopID,
	       timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
	    },
		method:'post'
	}); 
}	


function endEditing(){
	if (editIndex == undefined){
		return true;
	}

	if ($('#datagrid_departments').datagrid('validateRow', editIndex)){
		
		$('#datagrid_departments').datagrid('endEdit', editIndex);
		//$('#datagrid_departments').datagrid('acceptChanges');
		editIndex = undefined;
		return true;
	} else {
		return false;
	}
}

function edit(rowIndex,rowData){
	if (editIndex != rowIndex && endEditing()){
		$('#datagrid_departments').datagrid('beginEdit', rowIndex);
		editIndex = rowIndex;
	}
}

function append(){
	if (endEditing()){
		$('#datagrid_departments').datagrid('insertRow',{index:0,row:{
			name:"",
			phone:"",
			workshopID:$("#combo_workshop").combobox('getValue'),
			workshopName:$("#combo_workshop").combobox('getText')
			
		}});
		editIndex = 0;
		$('#datagrid_departments').datagrid('selectRow', editIndex).datagrid('beginEdit', 0);
		
	}
}
function remove(){
	var rows = $('#datagrid_departments').datagrid('getChecked');
	
	for(var i=0; i<rows.length; i++){
		var index = $('#datagrid_departments').datagrid('getRowIndex',rows[i]);
		$('#datagrid_departments').datagrid('deleteRow',index);
	}
}

function save(){
	if (!endEditing()){
		return;
	}
	var msg = "确定要保存修改吗？";
	if(confirm(msg) == false){
		return;
	}
	//获取变更的记录
	var rows_inserted = $('#datagrid_departments').datagrid('getChanges','inserted');
	var rows_deleted = $('#datagrid_departments').datagrid('getChanges','deleted');
	var rows_updated = $('#datagrid_departments').datagrid('getChanges','updated');
	
	//转换为json字符串
	var json_inserted = JSON.stringify(rows_inserted);
	var json_deleted = JSON.stringify(rows_deleted);
	var json_updated = JSON.stringify(rows_updated);
	
	//提交后台执行变更
	$.post("/Order/DepartmentServlet",
		{
			method:3,
			inserted:json_inserted,
			deleted:json_deleted,
			updated:json_updated,
	       	timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
       },
       function(result){
	       	//提交变更数据  
	       // $('#datagrid_departments').datagrid('acceptChanges');
	        $('#datagrid_departments').datagrid('reload');
	        $.messager.show({
				title:'编辑班组信息',
				timeout:3000,
				msg:'保存成功',
				width:200,
				showType:'slide'
			});
       		
	  }
	);	
}
function reject(){
	$('#datagrid_departments').datagrid('rejectChanges');
	editIndex = undefined;
}

//送餐点ID至name的格式化
function placeFormatter(value, rowData, rowIndex) {	
	return rowData.placeName;
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
	<div  data-options="region:'east',collapsible:true,collapsed:true,width:250" title='帮助' style="width:250px;padding:10px" >
		<%@include file="../../inc/help/admin/department.inc" %>
	</div>
	<div data-options="region:'center',title:'班组管理'" >
		<table  class="easyui-datagrid" id="datagrid_departments" data-options="toolbar:'#toolbar_department',fit:true,striped:true,checkOnSelect:true,selectOnCheck:false,singleSelect:true,onDblClickRow: edit">
			<thead>
				<tr>
					<th data-options="field:'selector',checkbox:true"></th>
					<th data-options="field:'name',width:200,editor:{type:'textbox',options:{required:true}}">班组名称</th>
					<th data-options="field:'phone',width:100,editor:{type:'textbox',options:{validType:'mobile'}}">电话</th>
					<th data-options="field:'workshopName',width:100">所属车间</th>					
				</tr>
			</thead>
		</table>
		<div id="toolbar_department" style="height:50px;line-height: 40px;">
			公司
			<input class="easyui-combobox"id="combo_company" data-options="valueField:'ID',textField:'name', onSelect:onSelectCompany" />
			&emsp;车间
			<input class="easyui-combobox" id="combo_workshop" data-options="valueField:'ID',textField:'name',onSelect:onSelectWorkshop" />
			<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-add'" onclick="append()">添加</a>
			<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-remove'" onclick="remove()">删除</a>
			<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-save'" onclick="save()">保存</a>
			<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-undo'" onclick="reject()">撤销</a>
		</div>
	</div>		
			
</body>
</html>
