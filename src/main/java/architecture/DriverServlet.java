package architecture;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class DriverServlet extends HttpServlet {
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {

        // Setting up the content type of web page
        response.setContentType("text/html");

        NextEngine ne = new NextEngine();
        String result = ne.runEngine();


        // Writing the message on the web page
        PrintWriter out = response.getWriter();
        out.println("<h1> Next Engine </h1>");
        out.println("<p>" + "Result : !" + "</p>");
        if (result == null) {
            result = "success";
        }
        out.println("<p>" + result + "</p>");
    }
}