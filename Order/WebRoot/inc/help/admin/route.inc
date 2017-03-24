
<h4>界面介绍</h4>
<p>从左至右依次是送餐线路列表、线路送餐点列表和备选送餐点列表</p>

<h4>送餐线路编辑</h4>
<p>添加：单击送餐线路中的<a class="easyui-linkbutton" data-options="iconCls:'icon-add'">添加</a>，可在列表中增加一个送餐线路，输入送餐线路名称</p>
<p>删除：选中一个或多个线路，然后单击<a class="easyui-linkbutton" data-options="iconCls:'icon-remove'">删除</a></p>
<p>修改：双击送餐线路列表中的某个线路，即可进入编辑模式修改</p>
<p>保存：添加、删除和修改操作只有<a class="easyui-linkbutton" data-options="iconCls:'icon-save'">保存</a>后才能生效，未保存前可以单击<a class="easyui-linkbutton" data-options="iconCls:'icon-redo'">撤销</a>予以撤销</p>

<h4>线路送餐点编辑</h4>
<p>添加：选中备选送餐点中的一个或多个，单击<a class="easyui-linkbutton" data-options="iconCls:'icon-back'" onclick="addPlaceToRoute()">添加到线路</a></p>
<p>删除：选中一个或多个线路送餐点，然后单击<a class="easyui-linkbutton" data-options="iconCls:'icon-remove'">删除</a></p>
<p>调整顺序单击<a class="easyui-linkbutton" data-options="iconCls:'icon-move_up'" onclick="moveup()">上移</a><a class="easyui-linkbutton" data-options="iconCls:'icon-move_down'" onclick="movedown()">下移</a>可以调整线路送餐顺序</p>

<h4>送餐点编辑</h4>
<p>添加：单击备选送餐点中的<a class="easyui-linkbutton" data-options="iconCls:'icon-add'">添加</a>，可在列表中增加一个送餐点，输入送餐点名称</p>
<p>删除：选中一个或多个备选送餐点，然后单击<a class="easyui-linkbutton" data-options="iconCls:'icon-remove'">删除</a></p>
<p>修改：双击送餐点，即可进入编辑模式修改</p>
<p>保存：添加、删除和修改操作只有<a class="easyui-linkbutton" data-options="iconCls:'icon-save'">保存</a>后才能生效，未保存前可以单击<a class="easyui-linkbutton" data-options="iconCls:'icon-redo'">撤销</a>予以撤销</p>
