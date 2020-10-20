package com.viztrend.safe.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Basic Controller which is called for unhandled errors
 */
@Controller
public class AppErrorController implements ErrorController{

	
	private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(AppErrorController.class);
    /**
     * Error Attributes in the Application
     */
    private ErrorAttributes errorAttributes;
    
    @Autowired
	private Environment env;

    private final static String ERROR_PATH = "/error";

    /**
     * Controller for the Error Controller
     * @param errorAttributes
     */
    public AppErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    /**
     * Supports the HTML Error View
     * @param request
     * @return
     */
    @RequestMapping(value = ERROR_PATH, produces = "text/html")
    public String errorHtml(HttpServletRequest request, Model model) {
    	Map<String, Object> errorsMap  = getErrorAttributes(request, false);
    	
    	/*
    	if (errorsMap.get("exception").equals("org.springframework.security.web.authentication.rememberme.CookieTheftException")) {
    		LOGGER.debug("remember me cookie failure now route to home page");
    		return "/";
    	}*/
    	if (String.valueOf(errorsMap.get("exception")).contains("org.springframework.dao.DataAccessResourceFailureException")
    		||String.valueOf(errorsMap.get("exception")).contains("org.springframework.data.mongodb.UncategorizedMongoDbException"))
    	if (env.getProperty("isLocalDeployment").equals("true"))
    		return "system";
    	else
    		return "maintenance";
    		
    	//String exception = String.valueOf(errorsMap.get("exception"));
    	//String msg = String.valueOf(errorsMap.get("message"));
    	model.addAttribute("errorsMap",errorsMap);
    	//model.addAttribute("msg",msg);
    	return "error";
    	
//    	if (KendisException.class.getName().equals(exception)
//    			&& KendisException.sso_user_not_found.equals(msg))
//    		return "sign-up.html?uid=334242";
//    	return null;
    }

    /**
     * Supports other formats like JSON, XML
     * @param request
     * @return
     */
//    @RequestMapping(value = ERROR_PATH)
//    @ResponseBody
//    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
//        Map<String, Object> body = getErrorAttributes(request, getTraceParameter(request));
//        HttpStatus status = getStatus(request);
//        return new ResponseEntity<Map<String, Object>>(body, status);
//    }

    /**
     * Returns the path of the error page.
     *
     * @return the error path
     */
    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }


    private boolean getTraceParameter(HttpServletRequest request) {
        String parameter = request.getParameter("trace");
        if (parameter == null) {
            return false;
        }
        return !"false".equals(parameter.toLowerCase());
    }

    private Map<String, Object> getErrorAttributes(HttpServletRequest request,
                                                   boolean includeStackTrace) {
        RequestAttributes requestAttributes = new ServletRequestAttributes(request);
        return this.errorAttributes.getErrorAttributes(requestAttributes,
                includeStackTrace);
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request
                .getAttribute("javax.servlet.error.status_code");
        if (statusCode != null) {
            try {
                return HttpStatus.valueOf(statusCode);
            }
            catch (Exception ex) {
            }
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}