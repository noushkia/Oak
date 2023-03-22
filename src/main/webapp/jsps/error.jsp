<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Error!</title>
    <style>
        h1 {
            color: rgb(207, 3, 3);
        }
    </style>
</head>
<body>
<a href="/">Home</a>
<h1>
    Error:

</h1>
<br>
<h3>
    <%=exception.getMessage()%>
</h3>
</body>
</html>