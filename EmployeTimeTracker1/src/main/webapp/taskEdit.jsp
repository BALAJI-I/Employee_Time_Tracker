<%@ page import="java.sql.Connection, java.sql.DriverManager, java.sql.PreparedStatement, java.sql.ResultSet" %>
<!DOCTYPE html>
<html>
<head>
    <title>Task Management</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f9;
            margin: 0;
            padding: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            color: #333;
        }
        .container {
            background-color: #fff;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
            width: 80%;
            max-width: 800px;
            margin: 0 auto;
        }
        h2 {
            text-align: center;
            margin-top: 0;
            font-size: 24px;
            color: #007bff;
        }
        .task-form {
            margin: 20px 0;
            text-align: center;
        }
        .task-form input, .task-form button {
            margin: 5px 0;
            padding: 10px;
            width: calc(100% - 22px);
            max-width: 300px;
            border: 1px solid #ccc;
            border-radius: 5px;
        }
        .task-form button {
            background-color: #007bff;
            color: #fff;
            border: none;
            cursor: pointer;
            transition: background-color 0.3s ease;
        }
        .task-form button:hover {
            background-color: #0056b3;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        table, th, td {
            border: 1px solid #ddd;
        }
        th, td {
            padding: 10px;
            text-align: left;
        }
        th {
            background-color: #f2f2f2;
        }
        .actions {
            display: flex;
            justify-content: center;
        }
        .actions form {
            margin: 0 5px;
        }
        .actions button {
            background-color: #dc3545;
            color: #fff;
            border: none;
            padding: 5px 10px;
            border-radius: 5px;
            cursor: pointer;
            transition: background-color 0.3s ease;
        }
        .actions button:hover {
            background-color: #c82333;
        }
    </style>
</head>
<body>
    <div class="container">
        <h2>Task Management</h2>
        <form class="task-form" action="AdminTaskServlet" method="post">
            <input type="hidden" name="action" value="add">
            <input type="text" name="taskName" placeholder="Task Name" required>
            <button type="submit">Add Task</button>
        </form>
        <table>
            <tr>
                <th>ID</th>
                <th>Task Name</th>
                <th>Actions</th>
            </tr>
            <%
                Connection con = null;
                PreparedStatement ps = null;
                ResultSet rs = null;
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    con = DriverManager.getConnection("jdbc:mysql://localhost:3306/EmployeeTimeTracker", "root", "1924");
                    ps = con.prepareStatement("SELECT * FROM tasksManagement");
                    rs = ps.executeQuery();
                    if (!rs.isBeforeFirst()) { // If no data is returned
                        out.println("<tr><td colspan='3'>No tasks found.</td></tr>");
                    } else {
                        while(rs.next()) {
            %>
            <tr>
                <td><%= rs.getInt("id") %></td>
                <td><%= rs.getString("taskName") %></td>
                <td class="actions">
                    <form action="AdminTaskServlet" method="post">
                        <input type="hidden" name="action" value="delete">
                        <input type="hidden" name="taskId" value="<%= rs.getInt("id") %>">
                        <button type="submit">Delete</button>
                    </form>
                </td>
            </tr>
            <%
                        }
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                    out.println("<tr><td colspan='3'>Error: " + e.getMessage() + "</td></tr>");
                } finally {
                    try {
                        if(rs != null) rs.close();
                        if(ps != null) ps.close();
                        if(con != null) con.close();
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            %>
        </table>
    </div>
</body>
</html>
