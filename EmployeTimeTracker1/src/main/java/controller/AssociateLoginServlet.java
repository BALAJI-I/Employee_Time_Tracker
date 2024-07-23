package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import util.DatabaseUtil;

@WebServlet("/AssociateLoginServlet")
public class AssociateLoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try (Connection con = DatabaseUtil.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM Associate WHERE username=? AND password=?")) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int userid = rs.getInt("id");
                    HttpSession session = request.getSession();
                    session.setAttribute("userid", userid);
                    session.setAttribute("username", username);
                    response.sendRedirect("AssociateDashboard.jsp");
                } else {
                    response.sendRedirect("AssociateLogin.jsp?error=1");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
