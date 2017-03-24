<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="bean.*" %>
<%@ page import="DAO.*" %> 
<%@ page import="java.sql.Connection" %>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	Employee user = (Employee)session.getAttribute("user");
	if(user==null ){
		response.sendRedirect(request.getContextPath()+"/Jsps/login.jsp");
	}
	int target = Integer.parseInt(request.getParameter("target"));
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<base href="<%=basePath%>">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>公告</title>

<link rel="stylesheet" type="text/css" href="easyUI/themes/default/easyui.css" />
<link rel="stylesheet" type="text/css" href="easyUI/themes/icon.css" />
<link href="editor/themes/default/default.css" rel="stylesheet" />
<script type="text/javascript" src="easyUI/jquery.min.js"></script>
<script type="text/javascript" src="easyUI/jquery.easyui.min.js"></script>
<script type="text/javascript" src="easyUI/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript" src="datagrid_view/datagrid-detailview.js"></script>
<script type="text/javascript" src="js/util.js"></script>
<script src="editor/kindeditor-min.js"></script>
<script src="editor/lang/zh_CN.js"></script>


<script type="text/javascript">	

$(function(){
	loadNotices();
});	
function getDetail(rowIndex,rowData){
	$('#dlg_addNotice').dialog('open');
	$("#title").html(rowData.title);
	$("#date").html(rowData.date);
	$("#author").html(rowData.authorName);
	$("#carteen").html(rowData.carteenName);
	$("#txtContent").html(rowData.content);
}
function loadNotices(){
	$('#datagrid_notices').datagrid({  
	   url:'/Order/NoticeServlet',  
	    pagination:true,
		pageSize:15,
		pageList:[10,15,20],
	    queryParams:{  
	       method:1, 
	       pagination:true,
	       carteenID:0,
	       timestamp:(new Date()).valueOf() //加时间戳，解决缓存问题    
	    },
		method:'post'
	}); 
}	
 
</script>
		
</head>

<body class="easyui-layout">
	<div align="center"  data-options="region:'north',collapsible:false" title='' style="height:100px;background-color:#D6E6DE">
		<%@include file="../../inc/header.inc" %> 	
	</div>
	<div data-options="region:'west',collapsible:true" title='功能导航' style="width:120px;">
		<%@include file="../../inc/menu/menu_common.inc"%> 	
	</div>			
	<div data-options="region:'south'" style="height:50px;background-color:#D6E6DE" align="center">
		<%@include file="../../inc/footer.inc" %> 	
	</div>
	<div data-options="region:'center',title:'公告列表'">
		<table  class="easyui-datagrid" id="datagrid_notices" data-options="striped:true,fit:true,pagination:true,onDblClickRow: getDetail">
			<thead>
				<tr>
					<th data-options="field:'title',width:200,editor:{type:'textbox'}">公告标题</th>
					<th data-options="field:'authorName',width:80">发布者</th>
					<th data-options="field:'date',width:100">日期</th>
					<th data-options="field:'carteenName',width:100">食堂</th>
				</tr>
			</thead>
		</table>
	</div>		
			<!-- 添加公告对话框 -->

<div id="dlg_addNotice" class="easyui-dialog" title="公告" data-options="buttons: '#dlg-buttons',iconCls:'icon-notice2',draggable:false,closed:true,closable:true,modal:true" style="width:450px;height:400px;padding:10px;">
	<h2 id="title" align="center"></h2>
	<p align="center">发布日期：<span id="date"></span>&emsp;发布人：<span id="author"></span>&emsp;食堂：<span id="carteen"></span></p>
	<div id="txtContent" style="width: 100%; height: 200px;"></div>
</div>
		<div id="dlg-buttons">
		<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-exit'"onclick="javascript:$('#dlg_addNotice').dialog('close')">关闭</a>
	</div>				
</body>
</html>