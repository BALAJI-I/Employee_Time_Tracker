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
import org.json.JSONArray;
import util.DatabaseUtil;

@WebServlet("/chartData2")
public class viewReportAdminServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String type = request.getParameter("type");
        String userIdParam = request.getParameter("userid");

        if (userIdParam == null || userIdParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing userid parameter");
            return;
        }

        int userid;
        try {
            userid = Integer.parseInt(userIdParam);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid userid parameter");
            return;
        }

        try {
            String data = "";
            if ("pie".equals(type)) {
                data = getPieChartData(userid);
            } else if ("weekly".equals(type)) {
                data = getWeeklyChartData(userid);
            } else if ("monthly".equals(type)) {
                data = getMonthlyChartData(userid);
            } else {
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

    private String getPieChartData(int userid) throws Exception {
        String query = "SELECT taskName, SUM(TIMESTAMPDIFF(MINUTE, startTime, endTime)) / 60 AS totalTime " +
                       "FROM taskRecords WHERE userid = ? GROUP BY taskName";
        return executeQuery(query, userid);
    }

    private String getWeeklyChartData(int userid) throws Exception {
        String query = "SELECT WEEK(startTime) AS week, SUM(TIMESTAMPDIFF(MINUTE, startTime, endTime)) / 60 AS totalTime " +
                       "FROM taskRecords WHERE userid = ? GROUP BY week";
        return executeQuery(query, userid, "Week ");
    }

    private String getMonthlyChartData(int userid) throws Exception {
        String query = "SELECT MONTHNAME(startTime) AS month, SUM(TIMESTAMPDIFF(MINUTE, startTime, endTime)) / 60 AS totalTime " +
                       "FROM taskRecords WHERE userid = ? GROUP BY month";
        return executeQuery(query, userid);
    }

    private String executeQuery(String query, int userid) throws Exception {
        return executeQuery(query, userid, "");
    }

    private String executeQuery(String query, int userid, String prefix) throws Exception {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userid);
            try (ResultSet rs = ps.executeQuery()) {
                JSONArray jsonArray = new JSONArray();
                while (rs.next()) {
                    JSONArray row = new JSONArray();
                    row.put(prefix + (rs.getString("week") != null ? rs.getString("week") : rs.getString("month")));
                    row.put(rs.getDouble("totalTime"));
                    jsonArray.put(row);
                }
                return jsonArray.toString();
            }
        }
    }
}
