package architecture;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import one.DefaultObjects;
import one.ReadXMLFile;

public class Redirector extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		doPost(request, response);
		
		HttpSession session = request.getSession();
		DefaultObjects.setParamMap(request);
	}
	
    public void doPost(HttpServletRequest request, HttpServletResponse response) {

    	HttpSession session = request.getSession();
    	Map<String, Object> setupData = ReadXMLFile.getXMLData("/home/akshay/Java/RestCheck/setup.xml");
    	
    	ReadRedirector rr = new ReadRedirector();
    	List<Map<String, Object>> redirectors = rr.getRedirector("/home/akshay/Java/RestCheck/Redirector.xml");
    	Map<String, Object> paramsMap = new HashMap<String, Object>();
    	Map<String, String[]> params = request.getParameterMap();
    	
    	if (params != null) {
        	Set paramsSet = params.entrySet();
        	Iterator paramsIterator = paramsSet.iterator();

			while (paramsIterator.hasNext()) {

				Map.Entry<String, String[]> iteratedParam = (Map.Entry<String, String[]>) paramsIterator.next();

				String key = iteratedParam.getKey();
				String[] values = iteratedParam.getValue();

				if (values.length > 1) {
					for (String value : values) {
						paramsMap.put(key, value.toString());
					}
				} else {
					paramsMap.put(key, values[0].toString());
				}
			}
    	}
    	
    	String requestUri = request.getRequestURI();

    	String rootPath = requestUri.replaceAll("/" + setupData.get("defaultPath") + "/", "");
    	String redirectTo = null;
    	for (Map<String, Object> redirector : redirectors) {
    	    String requestName = (String) redirector.get(rootPath);
    		if (rootPath.equals(redirector.get("request-name"))) {
    		    ClassLoader classLoader = Redirector.class.getClassLoader();
    			try {
					if (redirector.get("method") != null && redirector.get("file-path") != null) {
						Class[] classParamsMap = new Class[1];
						classParamsMap[0] = Map.class;
						Class dynamicClass = classLoader.loadClass((String) redirector.get("file-path"));
						Object obj = dynamicClass.newInstance();
						Method method = dynamicClass.getMethod((String) redirector.get("method"), classParamsMap);
						method.invoke(obj, paramsMap);
					}
    			} catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
    				System.out.println(e);
    			}
    			redirectTo = (String) redirector.get("show");
    			break;
    		}
    	}
    	
    	try {
    		if (redirectTo != null) {
    			response.sendRedirect("#/"+redirectTo);
    		}
    	} catch (IOException e) {
    		System.out.println(e);
    	}
    }
}