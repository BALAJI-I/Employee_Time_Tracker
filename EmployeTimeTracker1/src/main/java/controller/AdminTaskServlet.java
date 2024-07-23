package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import util.DatabaseUtil;

@WebServlet("/AdminTaskServlet")
public class AdminTaskServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        try (Connection con = DatabaseUtil.getConnection()) {
            if ("add".equals(action)) {
                String taskName = request.getParameter("taskName");
                try (PreparedStatement ps = con.prepareStatement("INSERT INTO tasksManagement (taskName) VALUES (?)")) {
                    ps.setString(1, taskName);
                    ps.executeUpdate();
                    HttpSession session = request.getSession();
                    session.setAttribute("taskName", taskName);
                }
            } else if ("delete".equals(action)) {
                int taskId = Integer.parseInt(request.getParameter("taskId"));
                try (PreparedStatement ps = con.prepareStatement("DELETE FROM tasksManagement WHERE id = ?")) {
                    ps.setInt(1, taskId);
                    ps.executeUpdate();
                    HttpSession session = request.getSession();
                    session.setAttribute("taskid", taskId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        response.sendRedirect("taskEdit.jsp");
    }
}
