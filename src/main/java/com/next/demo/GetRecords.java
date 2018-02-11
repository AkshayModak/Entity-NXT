package com.next.demo;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebServlet;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

import architecture.dashboard.Dashboard;
import architecture.QueryGenerator;
import architecture.SQLProcessor;
import architecture.utils.Utility;

import java.util.Iterator;

@WebServlet("/getRecords")
public class GetRecords extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String tableName = request.getParameter("tableName");
        QueryGenerator queryGenerator = new QueryGenerator();
        Map<String, String[]> requestMap = new HashMap<>(request.getParameterMap());

        List<String> keyList = new ArrayList<>();
        keyList.add("tableName");
        keyList.add("viewRecords");

        Map<String, String[]> filteredRequestMap = Utility.removeKeyValueFromRequestMap(requestMap, keyList);
        System.out.println("====filteredRequestMap==="+filteredRequestMap);

        String query = "";
        String keywordSearchQuery = queryGenerator.getSearchQuery(tableName, filteredRequestMap);
        if (!("error").equalsIgnoreCase(keywordSearchQuery)) {
            query = keywordSearchQuery;
        } else {
            query = queryGenerator.setSelectQuery(tableName, null);
        }
        System.out.println("====keywordSearchQuery===="+keywordSearchQuery);

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
