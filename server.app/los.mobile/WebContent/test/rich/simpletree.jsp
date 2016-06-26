<%-- 
    Document   : echo
    Created on : 09.09.2008, 15:14:39
    Author     : artur
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib uri="http://richfaces.org/a4j" prefix="a4j"%>
<%@ taglib uri="http://richfaces.org/rich" prefix="rich"%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>        
        <h2>Tree</h2>
        <f:view>
    <ui:composition xmlns="http://www.w3.org/1999/xhtml"
      xmlns:ui="http://java.sun.com/jsf/facelets"
      xmlns:h="http://java.sun.com/jsf/html"
      xmlns:f="http://java.sun.com/jsf/core"
      xmlns:a4j="http://richfaces.org/a4j"
      xmlns:rich="http://richfaces.org/rich">

    <style>
        .col, .col2 {
            width:50%;
            vertical-align:top;
        }
    </style>


    <h:form>    
        <h:panelGrid columns="2" width="100%" columnClasses="col1,col2">
        
            <rich:tree style="width:300px" nodeSelectListener="#{SimpleTreeBean.processSelection}" 
                reRender="selectedNode" ajaxSubmitSelection="true"  switchType="client"
                value="#{SimpleTreeBean.treeNode}" var="item">
            </rich:tree>
            
            <h:outputText escape="false" value="Selected Node: #{SimpleTreeBean.nodeTitle}" id="selectedNode" />
        
        </h:panelGrid>

    </h:form>

</ui:composition>                       
        </f:view>             
    </body>
</html>
