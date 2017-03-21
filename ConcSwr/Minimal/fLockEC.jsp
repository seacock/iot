<%-- 
    Document   : fLockEC
    Created on : 2-mar-2017, 15.23.54
    Author     : andre
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Пожалуйста!</h1>
        
        <%
            out.println("<body bgcolor='#FFFFAA'>");
            String readers = (String)session.getAttribute("Readers");            
            out.println("<br>");
            out.println("<span style='color:#0066FF'>" + readers + "</span>");                    
            out.println("<br>");
                        
            String aggregateW = (String)getServletContext().getAttribute("Writer");
            String [] writerStringA = new String[1200];
            for (int i = 0; i < 1200; i++)
                writerStringA[i] = "";  

            String carriageReturn = "<br>";
            int start = 0, end;

            for (int i = 0; i < 1200; i++)
            {
                end = aggregateW.indexOf(carriageReturn, start);
                end += "<br>".length();
                writerStringA[i] = aggregateW.substring(start, end);
                start = end;           

                String numFailedattempts = "NumFailedattempts: ";
                int start1 = writerStringA[i].indexOf(numFailedattempts, 0);
                int end1 = writerStringA[i].indexOf(carriageReturn, 0);
                String ResultNFA = writerStringA[i].substring(start1 + numFailedattempts.length(), end1);
                int n=0;
                if (!ResultNFA.matches("0"))
                    out.println("<span style='color:#FF4433'>" + writerStringA[i] + "</span>");
                else  
                    out.println("<span style='color:#CCCCCC'>" + writerStringA[i] + "</span>");
            }
            getServletContext().setAttribute("Writer", "");
                        
            out.println("<br>");
            String book = (String)application.getAttribute("Book");
            out.println("<span style='color:#33FF00'>" + book + "</span>");
            %>
    </body>
</html>
