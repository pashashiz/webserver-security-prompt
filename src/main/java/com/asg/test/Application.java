package com.asg.test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.Principal;
import java.util.Enumeration;

public class Application extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        resp.getOutputStream().println("N: " + ((session != null) ? session.getAttribute("i") : 0));
        Principal principal = req.getUserPrincipal();
        resp.getOutputStream().println("Hello [" + principal.getName() + "], you have global access");
        if (session != null) {
            Enumeration<String> attributes = session.getAttributeNames();
            while (attributes.hasMoreElements()) {
                String name = attributes.nextElement();
                Object value = session.getAttribute(name);
                if (value instanceof Principal && name.contains("adapter-principal")) {
                    String adapter = name.replace("adapter-principal-", "");
                    resp.getOutputStream().println(
                            "You have access to [" + adapter + "] as user [" + ((Principal) value).getName() + "]");
                }
            }
        }
    }
}
