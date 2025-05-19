<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.RideMate.cab_service.model.Fare" %>
<html>
<head>
    <title>Fare Details</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <h1>Fare Details</h1>

    <%
        Fare fare = (Fare) request.getAttribute("fare");
        if (fare != null) {
    %>
        <p><strong>Fare ID:</strong> <%= fare.getFareId() %></p>
        <p><strong>Distance:</strong> <%= fare.getDistance() %> km</p>
        <p><strong>Time:</strong> <%= fare.getTime() %> min</p>
        <p><strong>Vehicle Type:</strong> <%= fare.getVehicleType() %></p>
        <p><strong>Amount:</strong> $<%= fare.getAmount() %></p>
        <p><strong>Discount:</strong> $<%= fare.getDiscount() %></p>

        <h2>Apply Discount</h2>
        <form action="${pageContext.request.contextPath}/fare/discount/<%= fare.getFareId() %>" method="post">
            <label>Discount Amount:</label>
            <input type="number" name="discount" step="0.01" required><br>
            <input type="submit" value="Apply Discount">
        </form>
    <%
        } else {
    %>
        <p style="color: red;">Fare details are not available.</p>
    <%
        }
    %>
</body>
</html>
