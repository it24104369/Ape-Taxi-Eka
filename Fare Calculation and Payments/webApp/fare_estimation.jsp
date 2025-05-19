<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.RideMate.cab_service.model.Fare" %>
<html>
<head>
    <title>Fare Estimation</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <meta name="viewport" content="width=device-width, initial-scale=1">
</head>
<body>
    <h1>Fare Estimation</h1>

    <form action="${pageContext.request.contextPath}/fare/estimate" method="post">
        <label for="distance">Distance (km):</label>
        <input type="number" name="distance" id="distance" step="0.1" min="0" required>

        <label for="time">Time (min):</label>
        <input type="number" name="time" id="time" step="0.1" min="0" required>

        <label for="vehicleType">Vehicle Type:</label>
        <select name="vehicleType" id="vehicleType" required>
            <option value="🚗 Car">🚗 Car</option>
            <option value="🏍️ Bike">🏍️ Bike</option>
            <option value="🚕 Taxi">🚕 Taxi</option>
        </select>

        <div class="centered-row" style="margin-bottom: 10px;">
            <input type="checkbox" name="isSurge" id="isSurge">
            <label for="isSurge" class="checkbox-label">Surge Pricing</label>
        </div>

        <input type="submit" value="Calculate Fare">
    </form>

    <% Fare fare = (Fare) request.getAttribute("fare");
       if (fare != null) { %>
        <div class="card">
            <h2>Fare Details</h2>
            <p><strong>Fare ID:</strong> <%= fare.getFareId() %></p>
            <p><strong>Distance:</strong> <%= fare.getDistance() %> km</p>
            <p><strong>Time:</strong> <%= fare.getTime() %> min</p>
            <p><strong>Vehicle Type:</strong> <%= fare.getVehicleType() %></p>
            <p><strong>Amount:</strong> $<%= fare.getAmount() %></p>
            <a href="${pageContext.request.contextPath}/payment/process/<%= fare.getFareId() %>" style="display:inline-block;margin-top:12px;">Proceed to Payment</a>
        </div>
    <% } %>
</body>
</html>
