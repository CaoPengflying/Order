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
<title>数据库备份</title>

<link rel="stylesheet" type="text/css" href="easyUI/themes/default/easyui.css" />
<link rel="stylesheet" type="text/css" href="easyUI/themes/icon.css" />
<script type="text/javascript" src="easyUI/jquery.min.js"></script>
<script type="text/javascript" src="easyUI/jquery.easyui.min.js"></script>
<script type="text/javascript" src="easyUI/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript" src="datagrid_view/datagrid-detailview.js"></script>

<script type="text/javascript">
var editIndex = undefined;

$(function(){
});	

function backup(){
	var date1 = $('#date1').datebox('getValue');
	var date2 = $('#date2').datebox('getValue');
	var deleteFlag = $("#chk_flag").is(':checked');
	$.post("/Order/DataServlet",
		{
			method:1, 
			date1:date1,
			date2:date2,
			deleteFlag:deleteFlag,
			timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
	    },
		function(result){  
       		var data = JSON.parse(result);
       		var success = data.success;
       		if(success){
       			$("#msg").html("备份成功");
       			$("#link_download").attr("href",data.url);
       			$("#link_download").html("下载备份文件");
       		}else{
       			$("#msg").html("备份失败");
       		}
	  	}
	);
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
		<%@include file="../../inc/help/admin/backup.inc" %>
	</div>
	<div data-options="region:'center',title:'数据库备份'" >
		<div id="dlg_bakeupdata" class="easyui-dialog" title="数据备份" data-options="iconCls:'icon-save'," style="width:250px;height:200px">
			<form id="f_bakeupdata" style="align:'center'">
				<table cellpadding='5px'>
					<tr align='center'>
						<td>开始日期:</td>
						<td><input id="date1" class="easyui-datebox" data-options="required:true,editable:false" /></td>
					</tr>
					<tr align='center'>
						<td>结束日期:</td>
						<td><input id="date2" class="easyui-datebox" data-options="required:true,editable:false" /></td>
					</tr>
					<tr align='center'>
						<td><input type="checkbox" id="chk_flag"  /></td>
						<td>是否自动删除当前数据:</td></tr>
					<tr align='center'>
						<td colspan=2><a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-backup'" style="width:200px;" onclick="backup()">备份</a> 
						
					</tr>
					<tr>
						<td><label id="msg"></label></td>
						<td><a id="link_download" href="#"></a></td>
					</tr>
				</table>
			</form>
		</div>
	</div>		
			
</body>
</html>
