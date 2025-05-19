<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.RideMate.cab_service.model.Payment" %>
<html>
<head>
    <title>Payment Confirmation</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <h1>Payment Confirmation</h1>

    <%
        Payment payment = (Payment) request.getAttribute("payment");
        if (payment != null) {
    %>
        <p><strong>Payment ID:</strong> <%= payment.getPaymentId() %></p>
        <p><strong>Fare ID:</strong> <%= payment.getFareId() %></p>
        <p><strong>Amount:</strong> $<%= payment.getAmount() %></p>
        <p><strong>Payment Method:</strong> <%= payment.getPaymentMethod() %></p>
        <p><strong>Status:</strong> <%= payment.getStatus() %></p>

        <%
            if ("completed".equalsIgnoreCase(payment.getStatus())) {
        %>
            <form action="${pageContext.request.contextPath}/payment/refund/<%= payment.getPaymentId() %>" method="post">
                <input type="submit" value="Refund Payment">
            </form>
        <%
            }
        %>

    <%
        } else {
            String fareId = (String) request.getAttribute("fareId");
    %>
        <form action="${pageContext.request.contextPath}/payment/process" method="post">
            <input type="hidden" name="fareId" value="<%= fareId != null ? fareId : "" %>">
            
            <label>Amount: </label>
            <input type="number" name="amount" step="0.01" required><br>

            <label>Payment Method: </label>
            <select name="paymentMethod" required>
                <option value="cash">Cash</option>
                <option value="credit_card">Credit Card</option>
                <option value="digital_wallet">Digital Wallet</option>
            </select><br>

            <input type="submit" value="Process Payment">
        </form>
    <%
        }
    %>
</body>
</html>
