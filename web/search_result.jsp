<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="mockey" uri="/WEB-INF/mockey.tld" %>
<%--@elvariable id="proxyInfo" type="com.mockey.ProxyServer"--%>
<c:set var="actionKey" value="conf_service" scope="request" />
<c:set var="pageTitle" value="Proxy Settings" scope="request" />
<c:set var="currentTab" value="proxy" scope="request" />
<%@include file="/WEB-INF/common/header.jsp" %>

<div id="main">
    <%@ include file="/WEB-INF/common/message.jsp"%>     
    <p><h1>Search Results</h1></p> 
    
        <c:choose>
        <c:when test="${not empty results}"> 
        <div class="parentform">       
        <ul>
       		<c:forEach var="resultItem" items="${results}"  varStatus="status">     
       		  <li> 
       		  <mockey:slug text="${resultItem.content}" maxLength="80"/>                    
       		  <c:if test="${resultItem.typeAsString eq 'service'}">
           		   <c:url value="/home" var="serviceUrl">
                    <c:param name="serviceId" value="${resultItem.serviceId}" />
                   </c:url>
                   <a href="${serviceUrl}">[link]</a>
       		  </c:if>
       		   <c:if test="${resultItem.typeAsString eq 'scenario'}">
                   <c:url value="/home" var="serviceUrl">
                    <c:param name="serviceId" value="${resultItem.serviceId}" />
                   </c:url>
                   (<b>Scenario</b>: ${resultItem.scenarioName})
                   <a href="${serviceUrl}">[link]</a>
              </c:if>
              <c:if test="${resultItem.typeAsString eq 'service_plan'}">
                   <c:url value="/home" var="servicePlanUrl">
                    <c:param name="servicePlanId" value="${resultItem.servicePlanId}" />
                   </c:url>
                   (<b>Service Plan</b>: ${resultItem.servicePlanName})
                   <a href="${servicePlanUrl}">[link]</a>
              </c:if>
       		  
       		   </li>
       		</c:forEach>
         </ul>
         </div>
         </c:when>
        <c:otherwise>
        <div class="info_message">Sorry, but no search results for the term: <strong>${term}</strong></div>
        </c:otherwise>
        </c:choose>
        
</div>
<jsp:include page="/WEB-INF/common/footer.jsp" />
