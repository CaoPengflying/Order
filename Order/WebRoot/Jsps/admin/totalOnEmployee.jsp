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
	int carteenID = user.getCarteenID();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<base href="<%=basePath%>">

<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>汇总统计</title>

<link rel="stylesheet" type="text/css" href="easyUI/themes/default/easyui.css" />
<link rel="stylesheet" type="text/css" href="easyUI/themes/icon.css" />
<script type="text/javascript" src="easyUI/jquery.min.js"></script>
<script type="text/javascript" src="easyUI/jquery.easyui.min.js"></script>
<script type="text/javascript" src="easyUI/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript" src="datagrid_view/datagrid-detailview.js"></script>
<script type="text/javascript" src="js/util.js"></script>
<script type="text/javascript">
var carteenID = <%=carteenID%>;
$(function(){
	initDate();
	loadCompanys();
});

//设置日期范围为该月的第一天和最后一天
function initDate(){
    var firstDay = new Date();
    firstDay.setDate(1);
    var str1 = date2String(firstDay);
    
    var lastDay = new Date();
    lastDay.setMonth(lastDay.getMonth()+1, 0);
    var str2 = date2String(lastDay);

	$("#date1").datebox("setValue", str1);
	$("#date2").datebox("setValue", str2);
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
	               	
	       	var empty = new Object;//添加一个“不限”的选项
	       	empty.ID = 0;
	       	empty.name = "不限";
	       	workshops.splice(0,0,empty);
	       	
	        $('#combo_workshop').combobox({
	        	data:workshops
	        });  
	        
	        $('#combo_workshop').combobox('select',workshops[1].ID);//选中第一个车间     	
	  	}
	);
}

//当选择车间加载车间所属班组
function onSelectWorkshop(record){
	$.post("/Order/DepartmentServlet",
		{
			method:1, 
	       	workshopID:record.ID,
	       	timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
       },
       function(result){
	       	var data = JSON.parse(result);
	       	var departments = data.rows;	     	
	       	var empty = new Object;//添加一个“不限”的选项
	       	empty.ID = 0;
	       	empty.name = "不限";
	       	departments.splice(0,0,empty);
	        $('#combo_department').combobox({
	        	data:departments
	        });  
	        
	        $('#combo_department').combobox('select',departments[0].ID);//选中第一个车间     		
	  	}
	);
}

function loadCompanys(){
	$.post("/Order/CompanyServlet",
		{
			method:1, 
			timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
	    },
		function(result){  
       		var data = JSON.parse(result);
       		var companys = data.rows;
			$("#combo_company").combobox('loadData' , companys);
			$("#combo_company").combobox('select' , companys[0].ID);//选中第一个
	  }
	);
}

function getOrders(){
	var companyID = $("#combo_company").combobox("getValue");
	var workshopID = $("#combo_workshop").combobox("getValue");
	var departmentID = $("#combo_department").combobox("getValue");
	var date1 = $('#date1').datebox('getValue');
	var date2 = $('#date2').datebox('getValue');
	$('#datagrid_orders').datagrid({  
	    url:'/Order/OrderServlet',  
	    queryParams:{  
	       method:5, 
	       companyID:companyID,
	       workshopID:workshopID,
	       departmentID:departmentID,
	       date1:date1,
	       date2:date2,
	       carteenID:carteenID,
	       timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
	    },
		method:'post'
	}); 
}
function exportExcel(){	
	var companyID = $("#combo_company").combobox("getValue");
	var companyName = $("#combo_company").combobox("getText");
	var workshopID = $("#combo_workshop").combobox("getValue");
	var departmentID = $("#combo_department").combobox("getValue");
	var date1 = $('#date1').datebox('getValue');
	var date2 = $('#date2').datebox('getValue');
	
	$('#departform').form("submit",{
		url:"/Order/OrderServlet",
		queryParams:{
		method:11,
		companyID:companyID,
		workshopID:workshopID,
		departmentID:departmentID,
		companyName:companyName,//公司名参数是冗余的，省得后台再重新获取
	    date1:date1,
	    date2:date2,
	    carteenID:carteenID,
	    timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
		},
	});	
}


function detailFormatter(index,row){
	return 	"<div style='padding:0px'>"+
				"<table class='datagrid_detail'>"+
				"</table>"+
			"</div>";
}


//将套餐类别由byte转换为字符串
function typeFormatter(value, rowData, rowIndex) {
	switch (value){
	case <%=Price.LUNCH%>:
		return "中餐";
	case <%=Price.DINNER%>:
		return "晚餐";
	case <%=Price.MIDNIGHT%>:
		return "零点餐["+rowData.additional+"]";
	}
	return "";
}

//展开明细
function onExpandRow(index,row){
	var date1 = $('#date1').datebox('getValue');
	var date2 = $('#date2').datebox('getValue');
	
	//收拢其它行
	var n =$('#datagrid_orders').datagrid('getRows').length;
	for(var i=0; i<n; i++){
		if(index != i){
			$('#datagrid_orders').datagrid('collapseRow',i);
		}
	}
	
	//获取明细区域，注意find方法中的表名要与detailFormatter方法中定义的一致
	var datagrid_detail = $('#datagrid_orders').datagrid('getRowDetail',index).find('table.datagrid_detail');
	
	//获取明细数据
	datagrid_detail.datagrid({  
	    url:'/Order/OrderServlet',  
	    queryParams:{  
	       method:3,  
	       employeeIDs:row.employeeID,
	       type:<%=Price.ALLDAY%>,
	       date1:date1,
	       date2:date2,
	       carteenID:carteenID,
	       timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
	    },
		method:'post',
		pagination:true,
		columns:[[
			{field:'eatDate',width:100,title:'用餐日期'},
			{field:'type',width:100,title:'套餐类别',formatter:typeFormatter},
			{field:'eaterName',width:80,title:'用餐人'},
			{field:'placeName',width:160,title:'送餐点'},
			{field:'ordererName',width:80,title:'订餐人'},
			{field:'orderDate',width:100,title:'订餐日期'},
			{field:'departmentName',width:160,title:'班组'},
			{field:'price',width:80,title:'价格'}
		]]	,
				//解决展开是“+”错位问题
		onLoadSuccess:function(){      
           $('#datagrid_orders').datagrid('fixDetailRowHeight',index);      
              setTimeout(function () {  
                  var tr=datagrid_detail.closest('tr');  
                	id = tr.prev().attr('id'); //此子表格父行所在行的id  
                   id = id.replace(/-2-(\d+)$/, '-1-$1'); //detailview没有展开的前部分的id是有规则的  
                   $('#' + id).next().css('height', tr.height());//设置没展开的前部分的高度，由于启用了计时器，会闪一下  
               }, 0);  
           } 	
	}); 	
}
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
	<div data-options="region:'east',title:'帮助',collapsible:true,collapsed:true,width:160" style="padding:10px;">
		<%@include file="../../inc/help/admin/totalOnEmployee.inc" %> 
	</div>
	<div data-options="region:'center'"  style="width:100%">
		<table  class="easyui-datagrid" id="datagrid_orders" data-options="toolbar:'#toolbar_orders',fit:true,striped:true,checkOnSelect:true,showFooter:true,view: detailview,detailFormatter:detailFormatter,onExpandRow:onExpandRow">
			<thead>
				<tr>
					<th data-options="field:'departmentName',width:150">单位名称</th>
					<th data-options="field:'employeeID',width:50">工号</th>
					<th data-options="field:'name',width:50">姓名</th>
					<th data-options="field:'amountLunchNormal',width:50,align:'center'">中餐数</th>
					<th data-options="field:'moneyLunchNormal',width:60,align:'center'">中餐企补</th>
					<th data-options="field:'amountLunchHoliday',width:60,align:'center'">节日<br/>中餐数</th>
					<th data-options="field:'moneyLunchHoliday',width:60,align:'center'">节日<br/>中餐企补</th>
					<th data-options="field:'amountDinnerNormal',width:50,align:'center'">晚餐数</th>
					<th data-options="field:'moneyDinnerNormal',width:60,align:'center'">晚餐企补</th>
					<th data-options="field:'amountDinnerHoliday',width:50,align:'center'">节日<br/>晚餐数</th>
					<th data-options="field:'moneyDinnerHoliday',width:60,align:'center'">节日<br/>晚餐企补</th>
					<th data-options="field:'amountMidnightNormal',width:60,align:'center'">零点餐数</th>
					<th data-options="field:'moneyMidnightNormal',width:70,align:'center'">零点餐企补</th>
					<th data-options="field:'amountMidnightHoliday',width:60,align:'center'">节日<br/>零点餐数</th>
					<th data-options="field:'moneyMidnightHoliday',width:70,align:'center'">节日<br/>零点餐企补</th>
					<th data-options="field:'sum',width:60,align:'center'">企补总额</th>
				</tr>
			</thead>
		</table>
		<div align="center" id="toolbar_orders" style="height:50px;line-height: 50px; overflow:auto;">
		<form id="departform" method="post">
			公司<input class="easyui-combobox"id="combo_company" data-options="valueField:'ID',textField:'name', onSelect:onSelectCompany" />
			&emsp;车间<input class="easyui-combobox" id="combo_workshop"data-options="valueField:'ID',textField:'name',onSelect:onSelectWorkshop" />
			&emsp;班组<input class="easyui-combobox" id="combo_department"data-options="valueField:'ID',textField:'name'" /> &emsp;
			<input class="easyui-datebox" id="date1" data-options="width:100">-	
			<input class="easyui-datebox" id="date2" data-options="width:100">	
			&emsp;
			<a  href="javascript:getOrders()" class="easyui-linkbutton"  data-options="iconCls:'icon-search',">统计</a> 
			<a  href="javascript:exportExcel()" class="easyui-linkbutton"  data-options="iconCls:'icon-xls',">导出</a> 
			</form>
		</div>
	</div>		
			
</body>
</html>
