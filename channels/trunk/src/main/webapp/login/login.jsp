<html>
<head>
	<title>Channels - Login</title>
	<link rel="stylesheet" href="../css/channels.css" type="text/css" />
	<link rel="icon" href="../images/favicon.ico" type="image/x-icon" />
</head>
<body>
	<h1><span>Mind-Alliance Channels</span></h1>
	<div id="contents">
<% if ( "1".equals( request.getParameter("login_error") ) ) { %>
		<div id="errors">
			<p>Login incorrect. Please try again.</p>
		</div>
		<div id="instructions">
			<p>If you are having problems logging in, send an email to 
			<a href="mailto:<% application.getInitParameter( "adminEmail" ); %>">the administrator</a>.</p>
			
			<p>If you forgot your password, you're out of luck until next release...</p>
		</div>
<% } else { %>
		<div id="instructions">
			<p>Welcome to Channels! Please login:</p>
		</div>
<% } %>
		<form method="post" action="../j_acegi_security_check.jsp">
			<table>
				<tr>
					<th>Username:</th>
					<td><input name="j_username" type="text"></input></td>
				</tr>
				<tr>
					<th>Password:</th>
					<td><input name="j_password" type="password"></input></td>
				</tr>
				<tr><td align="right" colspan="2">
					<input value="Login" type="submit"></input></td></tr>
				<tr><td align="right" colspan="2">
					Remember me <input value="1" name="j_remember_me" type="checkbox"></input></td></tr>
			</table>
		</form>
		
		<div id="disclaimers">
			<p>To request an account, send an email to 
			<a href="mailto:<% application.getInitParameter( "adminEmail" ); %>">the administrator</a>.</p>
			<p>Our favourite disclaimer will be here...</p>
		</div>
	</div>
</body>
</html>