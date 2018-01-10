package one;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import architecture.ReadRedirector;

public class RootFilter implements Filter{  
  
public void init(FilterConfig arg0) throws ServletException {}  
      
public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {  
 
		Map<String, Object> setupData = ReadXMLFile.getXMLData("setup.xml");

		ReadRedirector rr = new ReadRedirector();
		List<Map<String, Object>> redirectors = rr.getRedirector("Redirector.xml");

		String requestUri = null;

		if (request instanceof HttpServletRequest) {
			requestUri = ((HttpServletRequest) request).getRequestURI();
		}

		String rootPath = requestUri.replaceAll("/" + setupData.get("defaultPath") + "/", "");
		
		for (Map<String, Object> redirector : redirectors) {
			String requestName = (String) redirector.get("request-name");
			System.out.println((rootPath + ".am").equals(requestName + ".am"));
			if (rootPath.equals(requestName)) {
				chain.doFilter(request, response);// sends request to next // resource
				return;
			}
		}
    }  

    public void destroy() {}
}