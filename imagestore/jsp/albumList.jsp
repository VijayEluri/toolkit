<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN">
<!--
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tm" uri="http://trailmagic.com/taglibs/image" %>

View to list Albums.

Model Requirements:
user: currently logged in user
albums: List of albums (ImageGroups of type album)
owner: the current albums' owner

-->
<html>
  <head>
    <title>Albums for User: <c:out value="${owner.screenName}"/></title>
  </head>

  <body>
    <h1>Albums for User: <c:out value="${owner.screenName}"/></h1>
    <ul>
      <c:forEach var="snapper" items="${albums}">
        <li><a href="./<c:out value="${snapper.name}"/>"><c:out value="${snapper.displayName}"/></a></li>
      </c:forEach>
    </ul>
  </body>
</html>