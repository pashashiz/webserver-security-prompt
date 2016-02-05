package com.asg.test;

import com.asg.test.errors.ErrorDetail;
import com.asg.test.errors.HttpError;
import com.asg.test.errors.AdapterUnauthorizedErrorDetail;
import com.asg.test.security.NamePasswordPrincipal;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class Adapter extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        resp.getOutputStream().println("N: " + session.getAttribute("i"));
        String adapter = req.getParameter("name");
        // Validate request
        if (adapter == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getOutputStream().println("Error: Missed adapter name parameter in the request");
            return;
        }
        // Check credentials
        if (session.getAttribute("adapter-principal-" + adapter) == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ErrorDetail detail = new AdapterUnauthorizedErrorDetail("Unauthorized adapter access", adapter, "Basic");
            HttpError error = new HttpError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized", detail);
            req.setAttribute("error", error);
            return;
        }
        // Process request if authorized
        NamePasswordPrincipal principal = (NamePasswordPrincipal) req.getSession().getAttribute("adapter-principal-" + adapter);
        resp.getOutputStream().println("Hello [" + principal.getName() + "], you have an access to [" + adapter + "] adapter");
    }

}
