<!DOCTYPE html>
<html>
<body>
This page was generated (on server-side) at:
  <%= new java.util.Date() %><BR/>

with the Model-View-Controller pattern<BR/>
Model=data, View=jsp, Controller=Spring class <BR/>
  modelMsg: ${modelMsg}
<BR/>

and the horrible jsp syntax 
(altough Velocity, FreeMaker, Thymeleaf, 
  ... are also supported) <BR/>
<%
for (int i = 0; i < 2; i++) {
%>.<%
}
out.print(".");
%>

</body>
</html>