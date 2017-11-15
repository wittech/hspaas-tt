<div class="modal fade" id="userModal">
    <div class="modal-dialog" style="width:850px">
        <div class="modal-content" style="width:850px">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">选择用户</h4>
            </div>
            <div class="modal-body" id="userModelBody">

            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button type="button" class="btn btn-success" onclick="clearUser();">清空</button>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">

    function closeModal() {
        $('#userModal').modal('hide');
    }

    function openUserList() {
        var userId = $('#userId').val();
        $.ajax({
            url: '${BASE_PATH}/base/customer/commonUserList',
            dataType: 'html',
            type: 'POST',
            data: {userId: userId},
            success: function (data) {
                $('#userModelBody').html(data);
                $('#userModal').modal('show');
            }, error: function (data) {
                Boss.alert('请求用户列表异常！');
            }
        });
    }

    function selectUser(userId, fullName, mobile) {
        $('#userId').val(userId);
        $('#username').val(fullName);
        $('#userModal').modal('hide');
    }
    
    function userJumpPage(p){
        $('#userpn').val(p);
        var userId = $('#userId').val();
        $.ajax({
            url:'${BASE_PATH}/base/customer/commonUserList?userId='+userId,
            dataType:'html',
            data:$('#userform').serialize(),
            type:'POST',
            success:function(data){
                $('#userModelBody').html(data);
            },error:function(data){
                Boss.alert('请求用户列表异常！');
            }
        });
    }

    function clearUser() {
        $('#userId').val("");
        $('#username').val("");
        $('#userModal').modal('hide');
    }
</script>
</html>