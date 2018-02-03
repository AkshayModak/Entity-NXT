package com.next.demo;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import architecture.dashboard.Dashboard;

public class ShowTableDetails extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String tableName = request.getParameter("tableName");
        System.out.println("Table Name: "+tableName);
        Dashboard dashboard = new Dashboard();
        Map<String, Object> tableDetails = dashboard.getTableDetails(tableName);
        request.setAttribute("tableName", tableName);
        request.setAttribute("tableDetails", tableDetails.get(tableName));
        request.getRequestDispatcher("templates/tableDetails.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

}
