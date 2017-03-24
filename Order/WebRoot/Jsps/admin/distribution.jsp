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
<title>送餐</title>
<link rel="stylesheet" type="text/css" href="easyUI/themes/default/easyui.css" />
<link rel="stylesheet" type="text/css" href="easyUI/themes/icon.css" />
<script type="text/javascript" src="easyUI/jquery.min.js"></script>
<script type="text/javascript" src="easyUI/jquery.easyui.min.js"></script>
<script type="text/javascript" src="easyUI/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript" src="datagrid_view/datagrid-detailview.js"></script>
<script type="text/javascript" src="js/util.js"></script>
<script type="text/javascript">

var placeIDs = "";
$(function(){
	init();
	loadRoutes();
});

//初始化默认日期和默认套餐类别
function init(){
	var today = new Date();
	$("#date").datebox("setValue", date2String(today));	//设置当前日期
	
	var hour = today.getHours();
	if(hour >= 13){
		 $("input[name='type']")[1].checked = true;
	}else if(hour >20){
		$("input[name='type']")[2].checked = true;
	}else{
		$("input[name='type']")[0].checked = true;
	}
}

function onSelectRoute(record){
	placeIDs = record.placeIDs;
}

function loadRoutes(){
	$.ajax({
		async:false,
		url:"/Order/RouteServlet",
		type:"post",
		data:{
			method:4,
			carteenID:<%=user.getCarteenID()%>, 
			timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题
		},
		success:function(result){
		var data = JSON.parse(result);
       		var routes = data.routes;
			$("#combo_route").combobox('loadData' , routes);
			$("#combo_route").combobox('select' , routes[0].ID);//选中第一个
		}
	});
}
function getDistributions(){
	if(placeIDs==""){
		alert("该线路没有任何送餐点");
		return;
	}
	var type = $("input[name='type']:checked").val();
	var date = $('#date').datebox('getValue');
	
	
	$.post("/Order/OrderServlet",
		{
	       method:8,  
	       type:type,
	       placeIDs:placeIDs,
	       date:date,
	       timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
	    },
		function(result){
			var data = JSON.parse(result);
			var str = generateTable(data.rows,data.footer);
			document.getElementById("datagrid_orders").innerHTML = str;
		}
	); 
}	

//根据数据生成表格
function generateTable(rows,footer){
	var table = "";
	var typeName="";
	var type = $("input[name='type']:checked").val();
	switch(parseInt(type)) {
		case 1:
			 typeName = "中餐";
			break;
		case 2:
			 typeName = "晚餐";
			break;
		case 3:
			 typeName = "零点餐";
			break;
	}
	var date = $('#date').datebox('getValue');
	var routeName = $('#combo_route').combobox('getText');
	var table = "<table  cellspacing='0' cellpadding='3' padding='10' border='2' style='font-size:14px;align:center;margin:0 auto;border-collapse:collapse;'>";
	table += "<caption><h3>送餐单</h3>";
	table += "<span align='left'>线路："+routeName+"</span>&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;";
	table += "<span align='center'>套餐："+typeName+"</span>&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;";
	table += "<span align='right'>日期："+date+"</span></caption>";
	table += "<thead><tr >";
	table += "<th width='200px';>送餐点</th>"+"<th width='220px';>班组</th>"+"<th width='40px';>份数</th>"+"<th width='180px';>备注</th>"+"<th width='50px';>签收</th>";
	table += "</tr></thead>";
	table += "<tbody>";
	
	var insertIndex = table.length;//插入汇总数据的位置
	var placeID = -1;
	var placeName = "";
	var total = 0;
	var remark = "";
	var strTotal = "";
	for(var i=0; i<rows.length; i++){				
		if(rows[i].placeID != placeID){
			//遇到新的送餐点，需要插入上一个送餐点的汇总数据
			if(placeID != -1){
				strTotal = "<tr>";
				strTotal += "<td style='font-size:16px;font-weight:bold;'>"+placeName+"合计</td>";
				strTotal += "<td></td>";
				strTotal += "<td style='font-size:16px;font-weight:bold;text-align:center;'>("+total+")</td>";
				strTotal += "<td style='font-size:16px;font-weight:bold;'>"+remarkFormat(remark)+"</td>";
				strTotal += "<td></td>";
				strTotal += "</tr>";
				table = table.substring(0,insertIndex)+strTotal+table.substring(insertIndex);
			}
			insertIndex = table.length;
			placeID = rows[i].placeID;
			placeName = rows[i].placeName;
			total = 0;
			remark = "";
		}
		//添加当前这条明细
		table += "<tr>";
		table += "<td></td>";
		table += "<td>"+rows[i].departmentName+"</td>";
		table += "<td style='text-align:center;'>"+rows[i].amount+"</td>";
		table += "<td>"+remarkFormat(rows[i].remark)+"</td>";
		table += "<td></td>";
		table += "</tr>";
		
		//汇总数据
		total += rows[i].amount;
		if(rows[i].remark != "") {
			remark = addRemark(remark,rows[i].remark);
		}
	}
	
	//增加最后一条汇总数据
	if(total>0){
		strTotal = "<tr>";
		strTotal += "<td style='font-size:16px;font-weight:bold;'>"+placeName+"合计</td>";
		strTotal += "<td></td>";
		strTotal += "<td style='font-size:16px;font-weight:bold;text-align:center;'>("+total+")</td>";
		strTotal += "<td style='font-size:16px;font-weight:bold;'>"+remarkFormat(remark)+"</td>";
		strTotal += "<td></td>";
		strTotal += "</tr>";
		table = table.substring(0,insertIndex)+strTotal+table.substring(insertIndex);
	}
	
	//添加总计数据
	table += "<tr>";
	table += "<td></td>";
	table += "<td style='font-weight:bold'>"+footer.departmentName+"</td>";
	table += "<td style='font-size:16px;font-weight:bold;text-align:center;'>"+footer.amount+"</td>";
	table += "<td></td>";
	table += "<td></td>";
	table += "</tr>";
	table += "</tbody>";
	table += "</table>";
	
	return table;
}

function addRemark(remark1,remark2) {

	if(remark1 == "") {
		remark1 = remark2;
	}else{	
		var data1="",data2="";
		data1 = remark1.split(",");
		data2 = remark2.split(",");
		var num1 = parseInt(data1[0])+parseInt(data2[0]);
		var num2 = parseInt(data1[1])+parseInt(data2[1]);
		var num3 = parseInt(data1[2])+parseInt(data2[2]);
		remark1 = num1+","+num2+","+num3;
	}
	return remark1;
}
function remarkFormat(remark) {
	
	var data ="";
		if(remark != ""){
			var result = remark.split(",");
			if(parseInt(result[0]) != 0) {
				data += "A餐: "+result[0]+" 份</br>";
			}
			if(parseInt(result[1]) != 0) {
				data += "B餐: "+result[1]+" 份</br>";
			}
			if(parseInt(result[2]) != 0) {
				data += "C餐: "+result[2]+" 份</br>";
			}
		}
	return data;
}

function exportExcel() {
	var date = $('#date').datebox('getValue');
	var route = $('#combo_route').combobox('getText');
	var rows = $('#datagrid_orders').datagrid('getRows');
	var footers = $('#datagrid_orders').datagrid('getFooterRows');  
	var type = $("input[name='type']:checked").val();
	if(parseInt(type) == 1) {
		type = "中餐";
	}else if (parseInt(type) == 2) {
		type = "晚餐";
	}else {
		type = "零点餐";
	}
	try{          
        var app = new ActiveXObject('Excel.Application');    }catch(e)   
    {   
        alert(e+', 原因分析: 浏览器安全级别较高导致不能创建Excel对象或者客户端没有安装Excel软件');   
          return;   
    }     
	app.visible = true;
	var book = app.Workbooks.Add();
	var sheet = book.ActiveSheet;
	
	sheet.Cells(1,1).value = "九江石化食堂送餐单"; 
	sheet.Range("A1", "E1").MergeCells = true; 
	sheet.Cells(1,1).HorizontalAlignment = 3;
	sheet.Cells(1,1).Font.Size = 18;    
	sheet.Rows(1).RowHeight = 40;
	
	sheet.Cells(2,1).value = "线路："+route+"                  套餐类型:"+type+"                         日期："+date; 
	sheet.Range("A2", "E2").MergeCells = true; 
	sheet.Rows(2).RowHeight = 30;
	
	sheet.Cells(3,1).value = "送餐点"; 	
	sheet.Cells(3,2).value = "班组";
	sheet.Cells(3,3).value = "份数";
	sheet.Cells(3,4).value = "备注";
	sheet.Cells(3,5).value = "签收";
	sheet.Columns(1).ColumnWidth = 20;
	sheet.Columns(2).ColumnWidth = 30;
	sheet.Columns(3).ColumnWidth = 8;
	sheet.Columns(4).ColumnWidth = 15;
	sheet.Columns(5).ColumnWidth = 10;	
	sheet.Rows(2).RowHeight = 25;
	sheet.Range("A3","E3").HorizontalAlignment = 3;
	//sheet.Range("A3","E3").Interior.ColorIndex = 8; 
	
	for(var i=0; i<rows.length; i++){		
		sheet.Cells(i+4,1).value = rows[i].placeName;	
		sheet.Cells(i+4,2).value = rows[i].departmentName;
		sheet.Cells(i+4,3).value = rows[i].amount;
		sheet.Cells(i+4,4).value = rows[i].remark;	
		sheet.Cells(i+4,1).WrapText=true;
		sheet.Cells(i+4,2).WrapText=true;
		sheet.Cells(i+4,3).WrapText=true;
		sheet.Cells(i+4,4).WrapText=true;
	}
	sheet.Cells(rows.length+4,2).value = "总计";
	sheet.Cells(rows.length+4,3).value = footers[0].amount;
	sheet.Cells(rows.length+5,2).value = footers[1].departmentName;
	sheet.Cells(rows.length+5,3).value = footers[1].amount;
	sheet.Rows("3:"+(i+5)).RowHeight = 20;
	sheet.Range("A3", "E"+(i+5)).Borders.Weight = 2;//设置网格线
	//var d = new Date();
	//var date = (d.getMonth()+1)+"月"+d.getDate()+"日";
	//sheet.PrintPreview;
	/*
	try{
	book.saveAs(footers[1].departmentName + date+"订餐统计");
	}catch(e){
		return;
	}
	*/
	
	//app.Quit;
}
function doPrint() {
	bodyHtml = window.document.body.innerHTML;
	startFlag = "<!--startprint-->";
	endFlag = "<!--endprint-->";
	printHtml = bodyHtml.substr(bodyHtml.indexOf(startFlag) + startFlag.length);
	printHtml = printHtml.substring(0, printHtml.indexOf(endFlag));
	window.showModalDialog("/Order/Jsps/admin/print.html?t="+Math.random(), printHtml,  "location:No;status:No;help:No;dialogWidth:800px;dialogHeight:600px;scroll:auto;");
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
	<div data-options="region:'east',title:'帮助',collapsible:true,collapsed:true,width:250" style="padding:10px;">
		<%@include file="../../inc/help/admin/distribution.inc" %> 
	</div>
	<div data-options="region:'center',title:'今日送餐'" >
	<div align="center" id="toolbar_orders" style="height:50px;line-height: 50px;">
			<label>送餐线路</label>
			<input class="easyui-combobox" id="combo_route" data-options="valueField:'ID',textField:'name',onSelect:onSelectRoute,width:100">
			&emsp;
			<input type="radio" name="type" value="<%=Price.LUNCH%>"  checked="checked">中餐 
			<input type="radio" name="type" value="<%=Price.DINNER%>" >晚餐
			<input type="radio" name="type" value="<%=Price.MIDNIGHT%>" >零点餐
			&emsp;
			<input class="easyui-datebox" id="date" data-options="width:100">	
			&emsp;
			<a class="easyui-linkbutton" data-options="iconCls:'icon-search'" onclick="getDistributions()">查询</a>		
			<a class="easyui-linkbutton" data-options="iconCls:'icon-xls'" onclick="exportExcel()">导出</a>
			<a class="easyui-linkbutton" data-options="iconCls:'icon-print'" onclick="doPrint()">打印</a>
		</div>
	<!--startprint-->
			<div id="datagrid_orders">
				
			</div>
	<!--endprint-->
		
	</div>		
			
</body>
</html>
