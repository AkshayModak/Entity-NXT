package com.next.demo;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

import architecture.SQLProcessor;

public class CustomQuery extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = request.getParameter("query");
        SQLProcessor sqlProcessor = new SQLProcessor();
        List<Map<String, Object>> queryResult = sqlProcessor.runQuery(query);

        if (queryResult != null && queryResult.size() > 0) {
            Map<String, Object> queryMap = queryResult.get(0);
            if ("error".equalsIgnoreCase((String) queryMap.get("status"))) {
                request.setAttribute("status", "error");
                request.setAttribute("message", queryMap.get("message"));
            } else {
                request.setAttribute("status", "success");
                request.setAttribute("message", "Query Executed successfully!!!");
            }
        }

        System.out.println("===queryResult==="+queryResult);
        request.getRequestDispatcher("templates/sql-engine.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

}
