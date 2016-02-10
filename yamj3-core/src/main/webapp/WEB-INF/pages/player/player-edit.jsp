<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
    <head>
        <title>YAMJ v3</title>
        <!--Import the header details-->
        <c:import url="../template.jsp">
            <c:param name="sectionName" value="HEAD" />
        </c:import>
    </head>
    <body>
        <!--Import the navigation header-->
        <c:import url="../template.jsp">
            <c:param name="sectionName" value="NAV" />
        </c:import>

        <div id="logo">
            <h2>Edit Player</h2>
        </div>
        <p id="messageInfo">Enter the new values required</p>
        <form:form method="POST" commandName="player" action="${pageContext.request.contextPath}/player/edit/${player.id}.html">
        	<table id="headertable" class="hero-unit" style="width: 50%; margin: auto;">
                <tr>
                    <td class="right">Player Name:</td>
                    <td>&nbsp;</td>
                    <td colspan="2" class="left">${player.name}</td>
                </tr>
                <tr>
                    <td class="right">Device Type:</td>
                    <td>&nbsp;</td>
                    <td>${player.deviceType}</td>
                    <td><form:input path="deviceType" size="100"></form:input></td>
                 </tr>
                 <tr>
                 	<td class="right">IP Address:</td>
					<td>&nbsp;</td>
                    <td>${player.ipAddress}</td>
                 	<td><form:input path="ipAddress" size="50"></form:input></td>
                 </tr>
                 <tr>
                    <td colspan="3">&nbsp;</td>
                 	<td class="left">
    	                <input type="submit" name="update" class="btn info" value="Update" >  
                        <a href="${pageContext.request.contextPath}/player/list.html" class="btn info">Cancel</a>
                    </td>
				</tr>
			</table>
        </form:form>

        <!-- Import the footer -->
        <c:import url="../template.jsp">
            <c:param name="sectionName" value="FOOTER" />
        </c:import>
    </body>
</html>
