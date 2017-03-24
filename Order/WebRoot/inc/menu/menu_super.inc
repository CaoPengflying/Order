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
	$.post("EmployeeServlet",{method:4, employeeID:'<%=user.getID()%>',password:password,newPassword:newPassword},function(data) {
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