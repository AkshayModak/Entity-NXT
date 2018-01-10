package architecture;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import one.DatabaseUtils;

public class EntityServlet extends HttpServlet {

	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		doGet(request, response);
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) {

		try {
		PrintWriter writer = response.getWriter();
		ReadEntityDefinition red = new ReadEntityDefinition();
		String result = red.createTable();
		writer.write(result);
		} catch(IOException e) {
			System.out.println(e);
		}
	}
}