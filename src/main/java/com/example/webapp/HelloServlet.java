package com.example.webapp;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/hello")
public class HelloServlet extends HttpServlet {

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("text/html");
        final PrintWriter out = resp.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>Styled WebApp</title>");
        out.println("<link rel='stylesheet' href='css/style.css'>");
        out.println("</head><body>");
        out.println("<div class='container'>");
        out.println("<h1>Welcome to Styled WebApp!</h1>");
        out.println("<p>This is a modern-looking web page served via Tomcat.</p>");
        out.println("<button onclick=\"alert('Hello from Java Servlet!')\">Click Me</button>");
        out.println("</div></body></html>");
    }
}
