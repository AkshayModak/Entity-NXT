package com.next.demo;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.List;

import architecture.dashboard.Dashboard;
import architecture.QueryGenerator;
import architecture.SQLProcessor;

public class GetRecords extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String tableName = request.getParameter("tableName");
        QueryGenerator queryGenerator = new QueryGenerator();
        String query = queryGenerator.setSelectQuery(tableName, null);
        SQLProcessor sqlProcessor = new SQLProcessor();
        List<Map<String, Object>> queryResult = sqlProcessor.runQuery(query);

        Dashboard dashboard = new Dashboard();
        Map<String, Object> tableDetails = dashboard.getTableDetails(tableName);

        System.out.println(queryResult);

        request.setAttribute("tableName", tableName);
        request.setAttribute("tableDetails", tableDetails.get(tableName));
        request.setAttribute("records", queryResult);
        request.getRequestDispatcher("templates/tableDetails.jsp").forward(request, response);

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

}
