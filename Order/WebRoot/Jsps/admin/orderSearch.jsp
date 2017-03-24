<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="bean.*" %>
<%@ page import="DAO.*" %>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	int target = Integer.parseInt(request.getParameter("target"));
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<base href="<%=basePath%>">
<%	
	//获取登录职工
	Admin user = (Admin) request.getSession().getAttribute("user");
	int carteenID = user.getCarteenID();
%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>订单查询</title>

<link rel="stylesheet" type="text/css" href="easyUI/themes/default/easyui.css" />
<link rel="stylesheet" type="text/css" href="easyUI/themes/icon.css" />
<script type="text/javascript" src="easyUI/jquery.min.js"></script>
<script type="text/javascript" src="easyUI/jquery.easyui.min.js"></script>
<script type="text/javascript" src="easyUI/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript" src="datagrid_view/datagrid-detailview.js"></script>
<script type="text/javascript" src="js/util.js"></script>
<script type="text/javascript">
var editRow = undefined;
var allPlaces; //记录所有的送餐点信息，用于ID到name的转换
var carteenID =<%=carteenID%>
$(function(){
    loadCompanys();
	loadCarteenPlaces();
	initDate();
	loadType();
});
//套餐类别
function loadType() {
	var type = [{'ID':'0','name':'套餐不限'},{'ID':'<%=Price.LUNCH%>','name':'中餐'},{'ID':'<%=Price.DINNER%>','name':'晚餐'},{'ID':'<%=Price.MIDNIGHT%>','name':'零点餐'}];
	
	$('#combo_type').combobox({
			data:type
	});

	$('#combo_type').combobox('select',type[0].ID);
	
}

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
	       	empty.name = "班组不限";
	       	departments.splice(0,0,empty);
	        $('#combo_department').combobox({
	        	data:departments
	        });  
	        
	        $('#combo_department').combobox('select',departments[0].ID);//选中第一个班组     		
	  	}
	);
}
function onSelectCompany(record){
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
	       	empty.name = "车间不限";
	       	workshops.splice(0,0,empty);
	        $('#combo_workshop').combobox({
	        	data:workshops
	        });  
	        $('#combo_workshop_dst').combobox({
	        	data:workshops
	        });  
	        $('#combo_workshop').combobox('select',workshops[0].ID);//选中第一个车间   	
	  	}
	);
}
function initDate() {
	var firstDay = new Date();
    firstDay.setDate(1);
    var str1 = date2String(firstDay);
    $("#date1").datebox("setValue",str1);
	$("#date2").datebox("setValue", new Date().toDateString());	//设置当前日期
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
	       	var empty = new Object;//添加一个“不限”的选项
	       	empty.ID = 0;
	       	empty.name = "公司不限";
	       	companys.splice(0,0,empty);
	        $('#combo_company').combobox({
	        	data:companys
	        });  
	        
	        $('#combo_company').combobox('select',companys[0].ID);//选中第一个车间     		
	  	}
	);
}	

//加载食堂送餐点
function loadCarteenPlaces()
{ 
       $.post("/Order/PlaceServlet",
		{
	       method:7, 
	       carteenID:carteenID,
	       timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
	    },
		function(result) {
			var data = JSON.parse(result);
			var places = data.rows;
			var empty = new Object;
			empty.ID = 0;
			empty.name="送餐点不限";
			places.splice(0,0,empty);
			$('#combo_place').combobox({
				data:places
			});
			$('#combo_place').combobox('select',places[0].ID);
		}
	); 	
}
//获取订单列表
function getOrderOfEmployee(){
	var date1 = $('#date1').datebox('getValue');
	var date2 = $('#date2').datebox('getValue');
	var companyID = $("#combo_company").combobox("getValue");
	var workshopID = $("#combo_workshop").combobox("getValue");
	var departmentID =  $("#combo_department").combobox("getValue");
	var idOrName = $("#txt_id_name").textbox("getValue");	
	var type = $("#combo_type").combobox("getValue");
	var placeID = $("#combo_place").combobox("getValue");
	$('#datagrid_orders').datagrid({  
	    url:'/Order/OrderServlet', 
	    pagination:true,
		pageSize:20,
		pageList:[10,20,50], 
	    queryParams:{  
	       method:18, 
	       date1:date1, 
	       date2:date2,
	       type:type,
	       companyID:companyID,
	       workshopID:workshopID,
	       departmentID:departmentID,
	       idOrName:idOrName,
	       placeID:placeID,
	       carteenID:carteenID,
	       pagination:true,
	       timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
	    },
		method:'post'
	}); 	
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
	return value;
}
//打印
function doPrint() {
	bodyHtml = window.document.body.innerHTML;
	startFlag = "<!--startprint-->";
	endFlag = "<!--endprint-->";
	printHtml = bodyHtml.substr(bodyHtml.indexOf(startFlag) + startFlag.length);
	printHtml = printHtml.substring(0, printHtml.indexOf(endFlag));
	window.document.body.innerHTML = printHtml;
	
	window.print();
	window.location.reload();
	//window.document.body.innerHTML=bodyHtml;
}

//根据数据生成表格
function generateTable(rows){
	var table = "";
	var table = "<table  cellspacing='0' cellpadding='3' padding='10' border='2' style='font-size:14px;align:center;margin:0 auto;border-collapse:collapse;'>";
	table += "<caption><h3>订餐记录</h3>";
	table += "<thead><tr >";
	table += "<th width='80px';>用餐日期</th>"+"<th width='120px';>用餐人</th>"+"<th width='80px';>套餐类别</th>"+"<th width='120px';>送餐点</th>"+"<th width='70px';>订餐人</th>"+"<th width='80px';>订餐日期</th>"+"<th width='120px';>班组</th>";
	table += "</tr></thead>";
	table += "<tbody>";
	
	for(var i=0; i<rows.length; i++){		
		//添加当前这条明细
		table += "<tr>";
		table += "<td>"+rows[i].eatDate+"</td>";
		table += "<td>"+rows[i].eaterName+"["+rows[i].eaterID+"]</td>";
		table += "<td>"+typeFormatter(rows[i].type,rows[i],0)+"</td>";
		table += "<td>"+rows[i].placeName+"</td>";
		table += "<td>"+rows[i].ordererID+"</td>";
		table += "<td>"+rows[i].orderDate+"</td>";		
		table += "<td>"+rows[i].departmentName+"</td>";
		table += "</tr>";
	}	
	
	//添加总计数据
	table += "</tbody>";	
	table += "<tfooter>";
	table += "<tr>";
	table += "<td>本页共计"+rows.length+"条订餐记录</td>";
	table += "</tr>";
	table += "</tfooter>";
	table += "</table>";
	
	return table;
}

function CreateFormPage(printDatagrid) {
    var tableString = '<table cellspacing="0" class="pb"><caption><h2>订餐记录</h2></caption>';
    var frozenColumns = printDatagrid.datagrid("options").frozenColumns;  // 得到frozenColumns对象
    var columns = printDatagrid.datagrid("options").columns;    // 得到columns对象
    var nameList = '';

    // 载入title
    if (typeof columns != 'undefined' && columns != '') {
        $(columns).each(function (index) {
            tableString += '\n<tr>';
            tableString += "<th>序号</th>"
            if (typeof frozenColumns != 'undefined' && typeof frozenColumns[index] != 'undefined') {
                for (var i = 0; i < frozenColumns[index].length; ++i) {
                    if (!frozenColumns[index][i].hidden) {
                        tableString += '\n<th width="' + frozenColumns[index][i].width + '"';
                        if (typeof frozenColumns[index][i].rowspan != 'undefined' && frozenColumns[index][i].rowspan > 1) {
                            tableString += ' rowspan="' + frozenColumns[index][i].rowspan + '"';
                        }
                        if (typeof frozenColumns[index][i].colspan != 'undefined' && frozenColumns[index][i].colspan > 1) {
                            tableString += ' colspan="' + frozenColumns[index][i].colspan + '"';
                        }
                        if (typeof frozenColumns[index][i].field != 'undefined' && frozenColumns[index][i].field != '') {
                            nameList += ',{"f":"' + frozenColumns[index][i].field + '", "a":"' + frozenColumns[index][i].align + '"}';
                        }
                        tableString += '>' + frozenColumns[0][i].title + '</th>';
                    }
                }
            }
            for (var i = 0; i < columns[index].length; ++i) {
                if (!columns[index][i].hidden) {
                    tableString += '\n<th width="' + columns[index][i].width + '"';
                    if (typeof columns[index][i].rowspan != 'undefined' && columns[index][i].rowspan > 1) {
                        tableString += ' rowspan="' + columns[index][i].rowspan + '"';
                    }
                    if (typeof columns[index][i].colspan != 'undefined' && columns[index][i].colspan > 1) {
                        tableString += ' colspan="' + columns[index][i].colspan + '"';
                    }
                    if (typeof columns[index][i].field != 'undefined' && columns[index][i].field != '') {
                        nameList += ',{"f":"' + columns[index][i].field + '", "a":"' + columns[index][i].align + '"}';
                    }
                    tableString += '>' + columns[index][i].title + '</th>';
                }
            }
            tableString += '\n</tr>';
        });
    }
    // 载入内容
    var rows1 = printDatagrid.datagrid("getRows"); // 这段代码是获取当前页的所有行
    var rows = rows1.slice(0);
    var nl = eval('([' + nameList.substring(1) + '])');
    for (var i = 0; i < rows.length; ++i) {
    	rows[i].type = typeFormatter(rows[i].type,rows[i],0);//转换套餐名称
        tableString += '\n<tr>';
        tableString += "<td>"+(i+1)+"</td>";
        $(nl).each(function (j) {
            var e = nl[j].f.lastIndexOf('_0');

            tableString += '\n<td';
            if (nl[j].a != 'undefined' && nl[j].a != '') {
                tableString += ' style="text-align:' + nl[j].a + ';"';
            }
            tableString += '>';
            if (e + 2 == nl[j].f.length) {
                tableString += rows[i][nl[j].f.substring(0, e)];
            }
            else
                tableString += rows[i][nl[j].f];
            tableString += '</td>';
        });
        tableString += '\n</tr>';
    }
    tableString += '\n</table>';

    window.showModalDialog("/Order/Jsps/admin/print.html?t="+(new Date()).valueOf(), tableString,  "location:No;status:No;help:No;dialogWidth:800px;dialogHeight:600px;scroll:auto;");
}
//导出excel
function exportExcel() {
	var rows = $('#datagrid_orders').datagrid('getRows');
	try{          
        var app = new ActiveXObject('Excel.Application');    }catch(e)   
    {   
        alert(e+', 原因分析: 浏览器安全级别较高导致不能创建Excel对象或者客户端没有安装Excel软件');   
          return;   
    }     
	app.visible = true;
	var book = app.Workbooks.Add();
	var sheet = book.ActiveSheet;
	
	sheet.Cells(1,1).value = "九江石化食堂订单查询结果"; 
	sheet.Range("A1", "H1").MergeCells = true; 
	sheet.Cells(1,1).HorizontalAlignment = 3;
	sheet.Cells(1,1).Font.Size = 18;    
	sheet.Rows(1).RowHeight = 40;
	
	//sheet.Cells(2,1).value = "用餐日期："+route+"                  套餐类型:"+type+"                         日期："+date; 

	
	sheet.Cells(2,1).value = "用餐日期"; 	
	sheet.Cells(2,2).value = "工号";
	sheet.Cells(2,3).value = "用餐人";
	sheet.Cells(2,4).value = "套餐类别";
	sheet.Cells(2,5).value = "送餐点";
	sheet.Cells(2,6).value = "订餐人";
	sheet.Cells(2,7).value = "订餐日期";
	sheet.Cells(2,8).value = "班组";
	sheet.Columns(1).ColumnWidth = 18;
	sheet.Columns(2).ColumnWidth = 5;
	sheet.Columns(3).ColumnWidth = 10;
	sheet.Columns(4).ColumnWidth = 25;
	sheet.Columns(5).ColumnWidth = 20;	
	sheet.Columns(6).ColumnWidth = 10;	
	sheet.Columns(7).ColumnWidth = 18;	
	sheet.Columns(8).ColumnWidth = 30;	
	sheet.Rows(2).RowHeight = 25;
	sheet.Range("A3","H3").HorizontalAlignment = 3;
	//sheet.Range("A3","E3").Interior.ColorIndex = 8; 
	for(var i=0; i<rows.length; i++){	
		var type = "";
		switch (Number(rows[i].type)){
		case <%=Price.LUNCH%>:
			type = "中餐";
		case <%=Price.DINNER%>:
			type = "晚餐";
		case <%=Price.MIDNIGHT%>:
			type = "零点餐";
	}
		sheet.Cells(i+3,1).value = rows[i].eatDate;		
		sheet.Cells(i+3,2).value = rows[i].eaterID;	
		sheet.Cells(i+3,3).value = rows[i].eaterName;
		sheet.Cells(i+3,4).value = type;
		sheet.Cells(i+3,5).value = rows[i].placeName;	
		sheet.Cells(i+3,6).value = rows[i].ordererName;	
		sheet.Cells(i+3,7).value = rows[i].orderDate;
		sheet.Cells(i+3,8).value = rows[i].departmentName;
		sheet.Cells(i+3,1).WrapText=true;
		sheet.Cells(i+3,2).WrapText=true;
		sheet.Cells(i+3,3).WrapText=true;
		sheet.Cells(i+3,4).WrapText=true;
		sheet.Cells(i+3,5).WrapText=true;
		sheet.Cells(i+3,6).WrapText=true;
		sheet.Cells(i+3,7).WrapText=true;
		sheet.Cells(i+3,8).WrapText=true;
	}
	//sheet.Cells(rows.length+4,2).value = "总计";
	//sheet.Cells(rows.length+4,3).value = footers[0].amount;
	//sheet.Cells(rows.length+5,2).value = footers[1].departmentName;
	//sheet.Cells(rows.length+5,3).value = footers[1].amount;
	sheet.Rows("3:"+(i+5)).RowHeight = 20;
	sheet.Range("A3", "H"+(i+5)).Borders.Weight = 2;//设置网格线
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
		<%@include file="../../inc/help/admin/orderSearch.inc" %> 
	</div>
	<div data-options="region:'center',title:'订单查询'" >	
		
		<!--startprint-->
		<table  class="easyui-datagrid" id="datagrid_orders" data-options="toolbar:'#toolbar_employee',fit:true,striped:true,pagination:true,rownumbers:true" >
			<thead>
				<tr>						
					<th data-options="field:'eatDate',width:80">用餐日期</th>
					<th data-options="field:'eaterID',width:55">工号</th>
					<th data-options="field:'eaterName',width:60">用餐人</th>
					<th data-options="field:'type',width:80,formatter:typeFormatter">套餐类别</th>
					<th data-options="field:'placeName',width:140">送餐点</th>
					<th data-options="field:'ordererName',width:60">订餐人</th>
					<th data-options="field:'orderDate',width:80">订餐日期</th>							
					<th data-options="field:'departmentName',width:200">班组</th>
				</tr>
			</thead>
		</table>
		<!--endprint-->
		<div  id="toolbar_employee" >
			<label>用餐时段</label>
			<input class="easyui-datebox" id="date1" data-options="width:95">-
			<input class="easyui-datebox" id="date2" data-options="width:95">
			&emsp;<input class="easyui-combobox" id="combo_company" data-options="valueField:'ID',textField:'name',onSelect:onSelectCompany" />
			&emsp;<input class="easyui-combobox" id="combo_workshop" data-options="valueField:'ID',textField:'name',onSelect:onSelectWorkshop" />
			&emsp;<input class="easyui-combobox" id="combo_department" data-options="valueField:'ID',textField:'name'" />	
			&emsp;<input class="easyui-combobox" id="combo_place" data-options="valueField:'ID',textField:'name'" />			
			&emsp;工号或姓名<input class="easyui-textbox" id="txt_id_name">
			&emsp;
			&emsp;<input class="easyui-combobox" id="combo_type" data-options="valueField:'ID',textField:'name'" />
			&emsp;<a class="easyui-linkbutton" data-options="iconCls:'icon-search'" onclick="getOrderOfEmployee()">查询</a>
			<a class="easyui-linkbutton" data-options="iconCls:'icon-xls'" onclick="exportExcel()">导出</a>
			<a class="easyui-linkbutton" data-options="iconCls:'icon-print'" onclick="CreateFormPage($('#datagrid_orders'));">打印</a>		
		</div>
	</div>
			
</body>
</html>
