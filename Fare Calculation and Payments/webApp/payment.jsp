<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Payment</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <script src="${pageContext.request.contextPath}/js/script.js"></script>
</head>
<body>
    <h1>Make a Payment</h1>

    <%-- Error Message --%>
    <c:if test="${not empty error}">
        <p style="color: red;">${error}</p>
    </c:if>

    <%-- Payment Form --%>
    <form action="${pageContext.request.contextPath}/payment/process" method="post" id="paymentForm">
        <input type="hidden" name="fareId" value="${fareId}">

        <label for="amount">Amount ($):</label>
        <input type="number" id="amount" name="amount" step="0.01" required><br>

        <label for="paymentMethod">Payment Method:</label>
        <select name="paymentMethod" id="paymentMethod" required onchange="toggleCardFields()">
            <option value="cash">Cash</option>
            <option value="credit_card">Credit Card</option>
            <option value="digital_wallet">Digital Wallet</option>
        </select><br>

        <%-- Credit Card Fields (conditionally shown) --%>
        <div id="creditCardFields" style="display: none;">
            <label for="cardNumber">Card Number:</label>
            <input type="text" id="cardNumber" name="cardNumber" placeholder="1234-5678-9012-3456"><br>

            <label for="expiryDate">Expiry Date:</label>
            <input type="text" id="expiryDate" name="expiryDate" placeholder="MM/YY"><br>

            <label for="cvv">CVV:</label>
            <input type="text" id="cvv" name="cvv" placeholder="123"><br>
        </div>

        <input type="submit" value="Submit Payment">
    </form>

    <br>
    <div class="centered-link">
        <a href="${pageContext.request.contextPath}/fare/estimate">Back to Fare Estimation</a>
    </div>

    <script>
        function toggleCardFields() {
            const paymentMethod = document.getElementById('paymentMethod').value;
            const creditCardFields = document.getElementById('creditCardFields');
            creditCardFields.style.display = (paymentMethod === 'credit_card') ? 'block' : 'none';
        }

        // Trigger correct visibility on page load
        window.onload = toggleCardFields;
    </script>
</body>
</html>
