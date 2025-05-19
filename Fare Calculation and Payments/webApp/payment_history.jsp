<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Payment History</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <h1>Payment History</h1>

    <c:if test="${not empty payments}">
        <table border="1" cellpadding="8" cellspacing="0">
            <thead>
                <tr>
                    <th>Payment ID</th>
                    <th>Fare ID</th>
                    <th>Amount</th>
                    <th>Payment Method</th>
                    <th>Status</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="payment" items="${payments}">
                    <tr>
                        <td>${payment.paymentId}</td>
                        <td>${payment.fareId}</td>
                        <td>$${payment.amount}</td>
                        <td>${payment.paymentMethod}</td>
                        <td>${payment.status}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>
    <c:if test="${empty payments}">
        <p>No payment records found.</p>
    </c:if>
</body>
</html>
