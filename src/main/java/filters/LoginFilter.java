package filters;

/**
 * Created by vlad on 27.03.2017.
 */
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//
// @WebFilter("/*")
public class LoginFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//        HttpServletRequest request = (HttpServletRequest) servletRequest;
//        HttpServletResponse response = (HttpServletResponse) servletResponse;
//
//        HttpSession session = request.getSession();
//
//        System.out.println(request.getRequestURI());
//        System.out.println(request.getRequestURI().endsWith("login"));
//        if (!request.getRequestURI().endsWith("login") &&
//                session.getAttribute("connector") == null) {
//            request.getRequestDispatcher(request.getContextPath() + "/views/login.html").forward(request, response);
//        }
    }

    @Override
    public void destroy() {

    }
}