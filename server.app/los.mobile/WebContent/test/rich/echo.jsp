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
        <h2>Echo Demo (ReRendert die übergebene id)</h2>
        <f:view>
            <h:form id="Form" styleClass="form">
            <rich:panel header="Simple Echo">
                <h:inputText size="50" value="#{DemoBean.text}" > 
                  <a4j:support event="onkeyup" reRender="rep"/>
                </h:inputText>
                <h:outputText value="#{DemoBean.text}"  id="rep"/>
              </rich:panel>                               
            </h:form>            
        </f:view>             
    </body>
</html>
