package architecture;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class DriverServlet extends HttpServlet {
    public void doPost(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {

        // Setting up the content type of web page
        NextEngine ne = new NextEngine();
        String result = ne.runEngine();

        response.getWriter().write("Success Data");
    }
}