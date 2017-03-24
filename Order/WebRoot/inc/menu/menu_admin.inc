<a  data-options="plain:true,iconCls:'icon-notice'" class="easyui-linkbutton" href="/Order/Jsps/admin/index.jsp?target=0" style="background:<%if(target==0){%>#ffe48d<%}else{ %>white<%} %>" >首页</a>
<% if ((user.getPermission() & Admin.POWER_COMPANY) != 0) {%>
<a  data-options="plain:true,iconCls:'icon-company'" class="easyui-linkbutton" href="/Order/Jsps/admin/company.jsp?target=1" style="background:<%if(target==1){%>#ffe48d<%}else{ %>white<%} %>" >公司管理</a>
<%} %>
<% if ((user.getPermission() & Admin.POWER_WORKSHOP) != 0) {%>
<a  data-options="plain:true,iconCls:'icon-workshop'" class="easyui-linkbutton" href="/Order/Jsps/admin/workshop.jsp?target=2" style="background:<%if(target==2){%>#ffe48d<%}else{ %>white<%} %>"  >车间管理</a>
<%} %>
<% if ((user.getPermission() & Admin.POWER_GROUP) != 0) {%>
<a data-options="plain:true,iconCls:'icon-department'" class="easyui-linkbutton" href="/Order/Jsps/admin/department.jsp?target=3" style="background:<%if(target==3){%>#ffe48d<%}else{ %>white<%} %>" >班组管理</a>
<%} %>
<% if ((user.getPermission() & Admin.POWER_EMPLOYEE) != 0) {%>
<a data-options="plain:true,iconCls:'icon-group'" class="easyui-linkbutton" href="/Order/Jsps/admin/employee.jsp?target=4" style="background:<%if(target==4){%>#ffe48d<%}else{ %>white<%} %>" >职工管理</a>
<%} %>

<% if ((user.getPermission() & Admin.POWER_CARTEEN) != 0) {%>
<a data-options="plain:true,iconCls:'icon-group'" class="easyui-linkbutton" href="/Order/Jsps/admin/carteen.jsp?target=5" style="background:<%if(target==5){%>#ffe48d<%}else{ %>white<%} %>" >食堂管理</a>
<%} %>
<% if ((user.getPermission() & Admin.POWER_WORKTYPE) != 0) {%>
<a data-options="plain:true,iconCls:'icon-add'" class="easyui-linkbutton" href="/Order/Jsps/admin/workType.jsp?target=6" style="background:<%if(target==6){%>#ffe48d<%}else{ %>white<%} %>" >倒班管理</a>
<%} %>
<% if ((user.getPermission() & Admin.POWER_HOLIDAY) != 0) {%>
<a data-options="plain:true,iconCls:'icon-holiday'" class="easyui-linkbutton" href="/Order/Jsps/admin/holiday.jsp?target=7" style="background:<%if(target==7){%>#ffe48d<%}else{ %>white<%} %>" >假日设置</a>
<%} %>
<!--  
<% if ((user.getPermission() & Admin.POWER_HISTORY) != 0) {%>
<a data-options="plain:true,iconCls:'icon-history'" class="easyui-linkbutton" href="/Order/Jsps/admin/history.jsp?target=8" style="background:<%if(target==8){%>#ffe48d<%}else{ %>white<%} %>" >历史订单</a>
<%} %>-->
<% if ((user.getPermission() & Admin.POWER_SEARCH) != 0) {%>
<a data-options="plain:true,iconCls:'icon-search'" class="easyui-linkbutton" href="/Order/Jsps/admin/orderSearch.jsp?target=9" style="background:<%if(target==9){%>#ffe48d<%}else{ %>white<%} %>" >订单查询</a>
<%} %>
<% if ((user.getPermission() & Admin.POWER_ORDER) != 0) {%>
<a data-options="plain:true,iconCls:'icon-order'" class="easyui-linkbutton" href="/Order/Jsps/admin/order.jsp?target=10" style="background:<%if(target==10){%>#ffe48d<%}else{ %>white<%} %>">代为订餐</a>
<%} %>
<% if ((user.getPermission() & Admin.POWER_PREPARE) != 0) {%>
<a data-options="plain:true,iconCls:'icon-cart-put'" class="easyui-linkbutton" href="/Order/Jsps/admin/prepare.jsp?target=11" style="background:<%if(target==11){%>#ffe48d<%}else{ %>white<%} %>" >备餐统计</a>
<%} %>
<% if ((user.getPermission() & Admin.POWER_DISTRIBUTION) != 0) {%>
<a data-options="plain:true,iconCls:'icon-cart-go'" class="easyui-linkbutton" href="/Order/Jsps/admin/distribution.jsp?target=12" style="background:<%if(target==12){%>#ffe48d<%}else{ %>white<%} %>" >今日送餐</a>
<%} %>
<% if ((user.getPermission() & Admin.POWER_ROUTE) != 0) {%>
<a data-options="plain:true,iconCls:'icon-route'" class="easyui-linkbutton" href="/Order/Jsps/admin/route.jsp?target=13" style="background:<%if(target==13){%>#ffe48d<%}else{ %>white<%} %>" >送餐线路</a>
<%} %>
<% if ((user.getPermission() & Admin.POWER_REGULAR) != 0) {%>
<a data-options="plain:true,iconCls:'icon-regular'" class="easyui-linkbutton" href="/Order/Jsps/admin/regular.jsp?target=14" style="background:<%if(target==14){%>#ffe48d<%}else{ %>white<%} %>" >订餐规则</a>
<%} %>
<% if ((user.getPermission() & Admin.POWER_PRICE) != 0) {%>
<a data-options="plain:true,iconCls:'icon-money'" class="easyui-linkbutton" href="/Order/Jsps/admin/price.jsp?target=15" style="background:<%if(target==15){%>#ffe48d<%}else{ %>white<%} %>" >套餐价格</a>
<%} %>
<% if ((user.getPermission() & Admin.POWER_NOTICE) != 0) {%>
<a data-options="plain:true,iconCls:'icon-notice2'" class="easyui-linkbutton" href="/Order/Jsps/admin/notice.jsp?target=16" style="background:<%if(target==16){%>#ffe48d<%}else{ %>white<%} %>" >公告管理</a>
<%} %>
<% if ((user.getPermission() & Admin.POWER_TOTAL) != 0) {%>
<a data-options="plain:true,iconCls:'icon-sum'" class="easyui-linkbutton" href="/Order/Jsps/admin/totalOnEmployee.jsp?target=17" style="background:<%if(target==17){%>#ffe48d<%}else{ %>white<%} %>" >按职工汇总</a>
<a data-options="plain:true,iconCls:'icon-sum'" class="easyui-linkbutton" href="/Order/Jsps/admin/totalOnDepartment.jsp?target=18" style="background:<%if(target==18){%>#ffe48d<%}else{ %>white<%} %>" >按班组汇总</a>
<a data-options="plain:true,iconCls:'icon-sum'" class="easyui-linkbutton" href="/Order/Jsps/admin/totalOnWorkshop.jsp?target=19" style="background:<%if(target==19){%>#ffe48d<%}else{ %>white<%} %>" >按车间汇总</a>
<%} %>

<% if ((user.getPermission() & Admin.POWER_DATA) != 0) {%>
<a data-options="plain:true,iconCls:'icon-backup'" class="easyui-linkbutton" href="/Order/Jsps/admin/backup.jsp?target=20" style="background:<%if(target==20){%>#ffe48d<%}else{ %>white<%} %>" >数据备份</a>
<a data-options="plain:true,iconCls:'icon-import'" class="easyui-linkbutton" href="/Order/Jsps/admin/import.jsp?target=21" style="background:<%if(target==21){%>#ffe48d<%}else{ %>white<%} %>" >数据导入</a>
<%} %>
<a data-options="plain:true,iconCls:'icon-key'" class="easyui-linkbutton" onclick="modifyPassword()" >修改密码</a>
<a data-options="plain:true,iconCls:'icon-exit'" class="easyui-linkbutton" onclick="exit()" >安全退出</a>

<!-- 修改密码对话框 -->
<div id="dlg_modifypass" class="easyui-dialog" title="修改密码" data-options="iconCls:'icon-edit',draggable:false,closed:true,closable:true,modal:true" style="width:250px;height:200px">
	<form id="f_modifypass" style="align:'center'">
		<table cellpadding='5px'>
			<tr align='center'>
				<td>原密码:</td>
				<td><input class="easyui-textbox" type="password" id="password" name="password" data-options="required:true" /></td>
			</tr>
			<tr align='center'>
				<td>新密码:</td>
				<td><input class="easyui-textbox" type="password" id="newPassword" name="newPassword" data-options="required:true,validType:['isBlank','length[1,10]']"/></td>
			</tr>
			<tr align='center'>
				<td>确认密码:</td>
				<td><input class="easyui-textbox" type="password" id="pass2" data-options="required:true,validType:['equals[\'#newPassword\']','isBlank','length[1,10]']" /></td>
			</tr>
			<tr align='center'>
				<td colspan=2><a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-save'" onclick="savepass()">保存密码</a> 	</td>
			</tr>
		</table>
	</form>
</div>

<script>


//退出
function exit(){
	$.post("/Order/EmployeeServlet",
		{method:13},
       		function(result){  
			window.location.href="/Order/Jsps/login.jsp";
		}
	);	
}

// 自定义密码编辑器  
$.extend($.fn.validatebox.defaults.rules, {    
    equals: {    
        validator: function(value,param){    
            return value == $(param[0]).val();    
        },    
        message: '两次密码不匹配'   
    }    
}); 
$.extend($.fn.validatebox.defaults.rules, {
    isBlank: {
        validator: function (value, param) { return value.indexOf(" ")<0},
        message: '不能包含空格！！！'
    }
});

//打开修改密码对话框
function modifyPassword(){
	$('#f_modifypass').form('clear');
	$("#dlg_modifypass").dialog('open');
}
//保存
function savepass(){			
	if(!$("#f_modifypass").form('validate'))
		return;
	var password = $("#password").val();
	var newPassword = $("#newPassword").val();
	$.post("EmployeeServlet",{method:4,userType:1, employeeID:'<%=user.getID()%>',password:password,newPassword:newPassword},function(data) {
       	if(data.success){
       		$.messager.show({
				title:'修改密码',
				timeout:3000,
				msg:data.message,
				width:200,
				showType:'slide'
			});
            $('#dlg_modifypass').dialog('close');
		}else{
		  	$.messager.alert("提示", data.message);
			$('#f_modifypass').form('clear');
		}
	}, "JSON");
}
</script>