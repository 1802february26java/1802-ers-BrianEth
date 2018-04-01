package com.revature.request;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The MasterServlet class is the only existing Servlet within the
 * application, it acts as a Front Controller, that receives every single 
 * request and gives away every response using a RequestHelper to find out
 * which Controller needs to be called when a specific URI is received.
 * 
 * @author Revature LLC
 */
public class MasterServlet extends HttpServlet {

	private static final long serialVersionUID = 1159764852861289598L;

	
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    System.out.println("contacted server:" + request.getQueryString());
		response.setHeader("Access-Control-Allow-Origin", "http://localhost:4200");
	    response.setHeader("Access-Control-Allow-Methods", "GET, PUT, POST, OPTIONS, DELETE");
	    response.setHeader("Access-Control-Allow-Credentials", "true");
	    response.setHeader("Access-Control-Allow-Headers", "JSESSIONID"); 
	   // getHeadersInfo(request);
	    super.service(request, response);
	}
	
	 private Map<String, String> getHeadersInfo(HttpServletRequest request) {

	        Map<String, String> map = new HashMap<String, String>();

	        Enumeration headerNames = request.getHeaderNames();
	        while (headerNames.hasMoreElements()) {
	            String key = (String) headerNames.nextElement();
	            String value = request.getHeader(key);
	            map.put(key, value);
	            System.out.println("key: " + key + ": " + value);
	        }

	        return map;
	    }
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		Object data = RequestHelper.getRequestHelper().process(request);

		// We forward if we receive a URI (String).
		if(data instanceof String) {
			String URI = (String) data;
			request.getRequestDispatcher(URI).forward(request, response);
		} 
		else {
			// We send data to the client if we receive any kind of object that is not a String.
			response.getWriter().write(
					new ObjectMapper().writeValueAsString(data));
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		// We can do this because we check if something is get or post inside the Controllers.
		doGet(request, response);
	}	
}
