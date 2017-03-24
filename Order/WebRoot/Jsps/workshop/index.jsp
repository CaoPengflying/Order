<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="bean.*" %>
<%@ page import="View.*" %>
<%@ page import="DAO.*" %> 
<%@ page import="java.sql.Connection" %>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	Employee user = (Employee) request.getSession().getAttribute("user");
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
var notices;

$(function(){
	loadNotices();
});	

function getDetail(index){
	$('#dlg_addNotice').dialog('open');
	$("#title").html(notices[index].title);
	$("#date").html(notices[index].date);
	$("#author").html(notices[index].authorName);
	$("#carteen").html(notices[index].carteenName);
	$("#txtContent").html(notices[index].content);
}
function loadNotices(){
	$.ajax({
		type: "post",
		url:"/Order/NoticeServlet",
		data:{ 
			method:1, 
			page:1,
			rows:15,
			carteenID:0,
			timestamp:(new Date()).valueOf()
		},
		async:false,
        success: function(result){
        	var data = JSON.parse(result);
       		notices = data.rows;
       		var str="";
       		for(var i=0; i<notices.length; i++){
       			str += "<p onclick=getDetail("+i+")><a href='javascript:void(0)' style='font-size:14px;'>"+notices[i].title+"["+notices[i].date+"]</a></p>";
       		}
       		$("#notices").html(str);
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
		<%@include file="../../inc/menu/menu_workshop.inc"%> 	
	</div>			
	<div data-options="region:'south'" style="height:50px;background-color:#D6E6DE" align="center">
		<%@include file="../../inc/footer.inc" %> 	
	</div>				
	<div data-options="region:'center',title:'最新公告'" style="padding:20px;">
		<table align="center">
			<tr>
				<td>
				<marquee id="notices" style="WIDTH: 400px; HEIGHT: 300px;border:solid 2px #ccc;padding:10px;" scrollamount="2" direction="up" onmouseover=this.stop() onmouseout=this.start() >	
			
				</marquee >
				</td>
				<td width="20px;"></td>
				<td>
					<div id="dlg_addNotice" class="easyui-dialog" title="公告" data-options="buttons: '#dlg-buttons',iconCls:'icon-notice2',draggable:false,closed:true,closable:true,modal:true" style="width:450px;height:400px;padding:10px;">
						<h2 id="title" align="center"></h2>
						<p align="center">发布日期：<span id="date"></span>&emsp;发布人：<span id="author"></span>&emsp;食堂：<span id="carteen"></span></p>
						<div id="txtContent" style="width: 100%; height: 200px;"></div>
					</div>
					<div id="dlg-buttons">
						<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-exit'"onclick="javascript:$('#dlg_addNotice').dialog('close')">关闭</a>
					</div>		
				</td>
			</tr>	
		</table>
	</div>	
	
</body>
</html>
