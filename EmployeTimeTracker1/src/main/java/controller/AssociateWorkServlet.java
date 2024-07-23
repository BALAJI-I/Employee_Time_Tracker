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

@WebServlet("/AssociateWorkServlet")
public class AssociateWorkServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userid") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        Integer userId = (Integer) session.getAttribute("userid");
        String taskIdStr = request.getParameter("taskId");
        if (taskIdStr == null) {
            response.sendRedirect("work.jsp");
            return;
        }

        int taskId = Integer.parseInt(taskIdStr);
        long startTime = (Long) session.getAttribute("startTime");
        long endTime = Long.parseLong(request.getParameter("endTime"));

        // Calculate total time in hours
        double totalTime = (endTime - startTime) / (1000.0 * 60 * 60);

        try (Connection con = DatabaseUtil.getConnection()) {
            String taskName = null;

            // Fetch taskName based on taskId
            try (PreparedStatement ps = con.prepareStatement("SELECT taskName FROM tasksManagement WHERE id = ?")) {
                ps.setInt(1, taskId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        taskName = rs.getString("taskName");
                    }
                }
            }

            // Insert record into taskRecords
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO taskRecords (userid, associate, taskName, startTime, endTime, totalTime) VALUES (?, ?, ?, ?, ?, ?)")) {
                ps.setInt(1, userId);
                ps.setString(2, "associate"); // Replace with actual associate name if needed
                ps.setString(3, taskName);
                ps.setTimestamp(4, new java.sql.Timestamp(startTime));
                ps.setTimestamp(5, new java.sql.Timestamp(endTime));
                ps.setDouble(6, totalTime);
                ps.executeUpdate();
            }

            // Clear the startTime from the session
            session.removeAttribute("startTime");
        } catch (Exception e) {
            e.printStackTrace();
        }

        response.sendRedirect("work.jsp");
    }
}
