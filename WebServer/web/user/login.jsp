<!DOCTYPE html>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>DropMusic</title>
  </head>

  <body>
    <h1>Login</h1>

    <s:form action="user_login_post">
      <s:textfield name="user.username" label="Username" />
      <s:textfield name="user.password" label="Password" />
      <s:submit value="Login"/>
    </s:form>
  </body>
</html>