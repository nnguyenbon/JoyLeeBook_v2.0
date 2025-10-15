<%--
  Created by IntelliJ IDEA.
  User: haishelby
  Date: 10/15/25
  Time: 11:03 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Register as Author</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;700&family=Istok+Web:wght@400;700&display=swap"
          rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">

    <style>
        body {
            background-color: #f0f2f5;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            font-family: 'Inter', sans-serif;
        }

        .register-container {
            background: #FFFFFF;
            width: 700px;
            max-width: 90%;
            padding: 30px 40px;
            border-radius: 10px;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
            margin: 40px 0;
        }

        .register-title {
            font-family: 'Inter', sans-serif;
            font-weight: 700;
            font-size: 34px;
            text-align: center;
            margin-bottom: 20px;
            color: #000000;
        }

        .register-description {
            font-family: 'Inter', sans-serif;
            font-size: 16px;
            margin-bottom: 25px;
            color: #333;
        }

        .separator {
            border: 0;
            height: 1px;
            background: #000000;
            margin: 20px 0;
        }

        .terms-list {
            list-style: none;
            padding: 0;
            margin: 0;
        }

        .term-item {
            display: flex;
            align-items: flex-start;
            font-family: 'Inter', sans-serif;
            font-size: 16px;
            line-height: 1.5;
            color: #000000;
            margin-bottom: 18px;
        }

        .term-item i {
            color: #195DA9;
            font-size: 24px;
            margin-right: 15px;
            margin-top: -2px;
        }

        .agreement-section {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-top: 25px;
        }

        .agreement-text {
            font-family: 'Inter', sans-serif;
            font-weight: 700;
            font-size: 20px;
            color: #000000;
        }

        .action-buttons {
            display: flex;
            gap: 15px;
        }

        .btn-agree {
            background: #195DA9;
            color: #ECF1FE;
            font-family: 'Istok Web', sans-serif;
            font-weight: 700;
            font-size: 16px;
            border: none;
            border-radius: 10px;
            padding: 8px 30px;
            cursor: pointer;
            text-align: center;
        }

        .btn-cancel {
            background: #FFFFFF;
            color: #000000;
            font-family: 'Istok Web', sans-serif;
            font-size: 16px;
            border: 1px solid #D9D9D9;
            border-radius: 10px;
            padding: 8px 30px;
            text-decoration: none;
            text-align: center;
        }

    </style>
</head>
<body>

<div class="register-container">
    <h1 class="register-title">Register As Author</h1>
    <p class="register-description">
        Please review and accept the following terms to become an author on our platform.
    </p>
    <hr class="separator">

    <ul class="terms-list">
        <li class="term-item">
            <i class="fas fa-check-circle"></i>
            <span>You must have a verified user account before submitting your author registration request.</span>
        </li>
        <li class="term-item">
            <i class="fas fa-check-circle"></i>
            <span>You are required to provide a short biography that accurately represents you as an author.</span>
        </li>
        <li class="term-item">
            <i class="fas fa-check-circle"></i>
            <span>All information and uploaded content must follow our community guidelines and must not include any offensive, harmful, or copyrighted materials.</span>
        </li>
        <li class="term-item">
            <i class="fas fa-check-circle"></i>
            <span>Your content submissions request will be reviewed by our staff or admin team before approval. You will receive a notification regarding the result.</span>
        </li>
        <li class="term-item">
            <i class="fas fa-check-circle"></i>
            <span>You must take full responsibility for all works, stories, and materials you publish on this platform.</span>
        </li>
        <li class="term-item">
            <i class="fas fa-check-circle"></i>
            <span>As an author, you are responsible for maintaining the quality and originality of your works.</span>
        </li>
        <li class="term-item">
            <i class="fas fa-check-circle"></i>
            <span>The platform and its administrators are not liable for any legal or copyright issues resulting from your published content.</span>
        </li>
        <li class="term-item">
            <i class="fas fa-check-circle"></i>
            <span>The platform reserves the right to suspend or revoke author privileges if any rules or content policies are violated.</span>
        </li>
    </ul>

    <hr class="separator">

    <div class="agreement-section">
        <span class="agreement-text">Do you agree to these terms?</span>
        <div class="action-buttons">
            <form action="${pageContext.request.contextPath}/register-author" method="post" style="margin: 0;">
                <button type="submit" class="btn-agree">I agree</button>
            </form>
            <a href="${pageContext.request.contextPath}/user-profile" class="btn-cancel">Cancel</a>
        </div>
    </div>
</div>

</body>
</html>