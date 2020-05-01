$(function(){
	$("#publishBtn").click(publish);
});

/*
function publish() {
	$("#publishModal").modal("hide");
	$("#hintModal").modal("show");
	setTimeout(function(){
		$("#hintModal").modal("hide");
	}, 2000);
}*/
function publish() {
	//隐藏发布的提示框
    $("#publishModal").modal("hide");

    // 获取标题和内容
    var title = $("#recipient-name").val();
    var content = $("#message-text").val();
    // 发送异步请求(POST)
    $.post(
    	//访问的路径
        CONTEXT_PATH + "/discuss/add",
		//发送的参数
        {"title":title,"content":content},
		//回调
        function(data) {
            data = $.parseJSON(data);
            // 在提示框中显示返回消息
            $("#hintBody").text(data.msg);
            // 显示提示框
            $("#hintModal").modal("show");
            // 2秒后,自动隐藏提示框
            setTimeout(function(){
                $("#hintModal").modal("hide");
                // 刷新页面
                if(data.code == 1) {
                	//重新刷新页面
                    window.location.reload();
                }
            }, 2000);
        }
    );

}