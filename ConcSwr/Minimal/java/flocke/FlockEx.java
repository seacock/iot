/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flocke;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import read.FileLockExclusiveRead;

/**
 *
 * @author andre
 */
class ThreadRead extends Thread {    
    public String thrMsg = "";
    public String thrBook = "";
    int NumberOfRun;
    
    public ThreadRead(int NumberOfRun)
    {
        this.NumberOfRun = NumberOfRun;
    } 
    
    @Override
    public void run(){  
        try {
            FileLockExclusiveRead fler = new FileLockExclusiveRead(NumberOfRun);
            fler.FlexRead();

            thrMsg = fler.message;
            thrBook = fler.book;
            Thread.sleep(100);
        } 
        catch (Exception e) {
            System.out.println(e);
        }
    }    
} 

public class FlockEx extends HttpServlet {    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet FlockEx</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet FlockEx at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
//        processRequest(request, response);
        int NumberOfRun = 0;
        int NumTr = 100, ReadTimes = 50;        
        String aggregate = "";
        
        getServletContext().setAttribute("WriterFinished", "No");
        
        for (int u = 0; u < ReadTimes; u++)
        {
            ThreadRead [] thR = new ThreadRead[NumTr]; 
            for (int i = 0; i < NumTr; i++)
                thR[i] = new ThreadRead(++NumberOfRun);     
            for (int i = 0; i < NumTr; i++)
                thR[i].start();    

            for (int i = 0; i < NumTr; i++)
            {
                try {
                    thR[i].join();
                }
                catch (Exception e) {
                    System.out.println(e);
                }
            }

            for (int i = 0; i < NumTr; i++)
                aggregate += thR[i].thrMsg + "<br>";
        }
        
        request.getSession().setAttribute("Readers", aggregate);
        
        String writerFinished = "";
        while (!writerFinished.matches("Yes"))
            writerFinished = (String)getServletContext().getAttribute("WriterFinished");  
        
        RequestDispatcher rd = request.getRequestDispatcher("fLockEC.jsp");
        rd.forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
//        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}