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
import org.json.JSONArray;
import util.DatabaseUtil;

@WebServlet("/chartData")
public class viewReportsAssociateServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userid") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String type = request.getParameter("type");
        int userid = (int) session.getAttribute("userid");

        try {
            String data = getChartData(type, userid);
            if (data == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid type parameter");
                return;
            }

            response.setContentType("application/json");
            response.getWriter().write(data);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    private String getChartData(String type, int userid) throws Exception {
        String query = "";
        switch (type) {
            case "pie":
                query = "SELECT taskName, SUM(TIMESTAMPDIFF(MINUTE, startTime, endTime)) / 60 AS totalTime " +
                        "FROM taskRecords WHERE userid = ? GROUP BY taskName";
                break;
            case "weekly":
                query = "SELECT WEEK(startTime) AS week, SUM(TIMESTAMPDIFF(MINUTE, startTime, endTime)) / 60 AS totalTime " +
                        "FROM taskRecords WHERE userid = ? GROUP BY week";
                break;
            case "monthly":
                query = "SELECT MONTHNAME(startTime) AS month, SUM(TIMESTAMPDIFF(MINUTE, startTime, endTime)) / 60 AS totalTime " +
                        "FROM taskRecords WHERE userid = ? GROUP BY month";
                break;
            default:
                return null;
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userid);
            try (ResultSet rs = ps.executeQuery()) {
                JSONArray jsonArray = new JSONArray();
                while (rs.next()) {
                    JSONArray row = new JSONArray();
                    row.put(type.equals("pie") ? rs.getString("taskName") :
                            type.equals("weekly") ? "Week " + rs.getInt("week") : rs.getString("month"));
                    row.put(rs.getDouble("totalTime"));
                    jsonArray.put(row);
                }
                return jsonArray.toString();
            }
        }
    }
}
