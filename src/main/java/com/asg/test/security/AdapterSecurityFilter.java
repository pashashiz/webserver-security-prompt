package com.asg.test.security;

import com.asg.test.errors.AdapterUnauthorizedErrorDetail;
import com.asg.test.errors.ErrorDetail;
import com.asg.test.errors.HttpError;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import sun.misc.BASE64Decoder;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URLEncoder;
import java.util.Enumeration;

public class AdapterSecurityFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        // Redirect if we use not valid context path without trailing slash
        // I we do not have it, could cause a lot of issues when user mistakenly forget to add "/" at the and
        if (req.getRequestURI().endsWith(req.getContextPath())) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }
        HttpSession session = req.getSession();
        // Counter for testing
        Integer i = (Integer) session.getAttribute("i");
        if (i == null) i = 0;
        session.setAttribute("i", ++i);
        // Do login for adapter for stateful browser client
        if (req.getRequestURI().contains((req.getContextPath() + "/login/adapter"))) {
            String adapter = req.getParameter("name");
            if (adapter == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getOutputStream().println("Error: Missed adapter name parameter in the request");
                return;
            }
            NamePasswordPrincipal principal = retrieveBasicPrincipals(req, null);
            if (!login(adapter, principal)) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.addHeader("WWW-Authenticate", "Basic realm=\"Adapter [" + adapter + "]" + " authorization\"");
                resp.getOutputStream().println("Adapter requires user specific credentials");
                return;
            }
            if (principal != null) {
                req.getSession().setAttribute("adapter-principal-" + adapter, principal);
            }
            String redirectedFrom = req.getParameter("redirected");
            if (redirectedFrom != null && !redirectedFrom.isEmpty())
                resp.sendRedirect(redirectedFrom);
            return;
        }
        // Check for authorization headers, in case stateless native client passes them
        else if (req.getRequestURI().contains(req.getContextPath() + "/adapter")) {
            String adapter = req.getParameter("name");
            if (adapter != null) {
                Enumeration<String> headers = req.getHeaderNames();
                while (headers.hasMoreElements()) {
                    String name = headers.nextElement();
                    if (name.contains("authorization-adapter-")) {
                        String adapterName = name.replace("authorization-adapter-", "");
                        NamePasswordPrincipal principal = retrieveBasicPrincipals(req, adapterName);
                        req.getSession().setAttribute("adapter-principal-" + adapterName, principal);
                    }
                }
            }
        }
        chain.doFilter(request, response);
        // Handle adapter unauthorized response
        if (resp.getStatus() == HttpServletResponse.SC_UNAUTHORIZED) {
            HttpError error = (HttpError) req.getAttribute("error");
            if (error != null) {
                // For browser clients - redirect to login page
                if (req.getHeader("Accept").contains("text/html")) {
                    for (ErrorDetail detail : error.getDetails()) {
                        if (detail instanceof AdapterUnauthorizedErrorDetail) {
                            if (((AdapterUnauthorizedErrorDetail) detail).getAuthorization().equals("Basic")) {
                                String redirectedUrl = req.getRequestURL() + "?" + req.getQueryString();
                                String loginUrl = req.getContextPath() + "/login/adapter"
                                        + "?name=" + ((AdapterUnauthorizedErrorDetail) detail).getAdapter()
                                        + "&redirected=" + URLEncoder.encode(redirectedUrl, "UTF-8");
                                resp.sendRedirect(loginUrl);
                                return;
                            }
                            else {
                                throw new RuntimeException(((AdapterUnauthorizedErrorDetail) detail).getAuthorization()
                                        + "authorization is not supported by web browser");
                            }
                        }
                    }
                }
                // For native clients - just response a detail error message in JSON format
                else {
                    // 401 status + JSON error
                    getJsonMapper().writeValue(resp.getOutputStream(), error);
                }
            }
        }
    }

    private NamePasswordPrincipal retrieveBasicPrincipals(HttpServletRequest req, String adapter) throws IOException {
        String authHeader = (adapter == null) ? req.getHeader("authorization") : req.getHeader("authorization-adapter-" + adapter);
        if (authHeader != null && !authHeader.isEmpty()) {
            String[] tokens = authHeader.split(" ");
            if (tokens.length == 2) {
                String authMethod = tokens[0];
                String data = tokens[1];
                if (authMethod.equalsIgnoreCase("Basic")) {
                    byte[] decoded = new BASE64Decoder().decodeBuffer(data);
                    String principalString = new String(decoded);
                    String[] principalPair = principalString.split("\\s*:\\s*", 2);
                    if (principalPair.length == 2)
                        return new NamePasswordPrincipal(principalPair[0], principalPair[1]);
                }
            }
        }
        return null;
    }

    private boolean login(String adapter, NamePasswordPrincipal principal) {
        return principal.getName().equals(adapter) && principal.getPassword().equals(adapter);
    }

    private ObjectMapper getJsonMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return mapper;
    }

    @Override
    public void destroy() {}

}
