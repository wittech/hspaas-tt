<#if mode == "AJAX_HTML">
    <h3>权限不足......</h3>
<#else>
    <!DOCTYPE html>
        <html lang="zh-CN">
        <head>
            <meta charset="utf-8">
            <meta http-equiv="X-UA-Compatible" content="IE=edge">
            <meta name="viewport" content="width=device-width, initial-scale=1">
            <meta name="description" content="">
            <meta name="author" content="">
            <title>融合平台</title>

            <script type="text/javascript">

                setTimeout(function(){
                    window.top.location.href = '${BASE_PATH}/main';
                },3000)

            </script>

        </head>

        <body>
        <h5>权限不足......</h5>
        </body>
    </html>
</#if>

