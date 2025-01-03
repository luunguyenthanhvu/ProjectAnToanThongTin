<%--
  Created by IntelliJ IDEA.
  User: PC
  Date: 26/03/2024
  Time: 5:02 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="en">
<head>
    <%@ page isELIgnored="false" %>
    <title>Thông tin người dùng</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <link href="https://fonts.googleapis.com/css?family=Poppins:200,300,400,500,600,700,800&display=swap"
          rel="stylesheet">
    <link href="https://fonts.googleapis.com/css?family=Lora:400,400i,700,700i&display=swap"
          rel="stylesheet">
    <link href="https://fonts.googleapis.com/css?family=Amatic+SC:400,700&display=swap"
          rel="stylesheet">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/static/css/web-css/open-iconic-bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/web-css/animate.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/static/css/web-css/owl.carousel.min.css">
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/static/css/web-css/owl.theme.default.min.css">
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/static/css/web-css/magnific-popup.css">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/web-css/aos.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/static/css/web-css/ionicons.min.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/static/css/web-css/bootstrap-datepicker.css">
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/static/css/web-css/jquery.timepicker.css">


    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/static/css/web-css/flaticon.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/web-css/icomoon.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/web-css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/web-css/fix.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/static/css/user-css/tao_khoa_moi_cho_nguoi_dung.css?=9">

</head>
<body class="goto-here">
<%
    // Lấy giá trị attribute isCreateKeySuccess từ /page/user/general-key-info
    Boolean isCreateKeySuccess = (Boolean) request.getAttribute("isCreateKeySuccess");
    String publicKey = (String) request.getAttribute("publicKey");
    String privateKey = (String) request.getAttribute("privateKey");
%>
<nav class="navbar-container navbar navbar-expand-lg navbar-dark ftco_navbar bg-dark ftco-navbar-light"
     id="ftco-navbar">
    <div class="container navbar-container">
        <div class="navbar-brand">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/page/home">Cửa Hàng
                Trái Cây</a>
        </div>
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#ftco-nav"
                aria-controls="ftco-nav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="oi oi-menu"></span> Menu
        </button>

        <div class="navbar-account">
            <div class="nav-item dropdown">
                <a class="nav-link dropdown-toggle" href="#" id="dropdown05" data-toggle="dropdown"
                   aria-haspopup="true" aria-expanded="false">Thông tin người dùng</a>
                <div class="dropdown-menu account-menu" aria-labelledby="dropdown04">
                    <a class="account dropdown-item"
                       href="${pageContext.request.contextPath}/page/user/user-profile">
                        <svg xmlns="http://www.w3.org/2000/svg" height="1em" viewBox="0 0 448 512">
                            <!--! Font Awesome Free 6.4.2 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license (Commercial License) Copyright 2023 Fonticons, Inc. -->
                            <path d="M224 256A128 128 0 1 0 224 0a128 128 0 1 0 0 256zm-45.7 48C79.8 304 0 383.8 0 482.3C0 498.7 13.3 512 29.7 512H418.3c16.4 0 29.7-13.3 29.7-29.7C448 383.8 368.2 304 269.7 304H178.3z"/>
                        </svg>
                        Người Dùng
                    </a>
                    <a class="account dropdown-item"
                       href="${pageContext.request.contextPath}/page/logout">
                        <svg xmlns="http://www.w3.org/2000/svg" height="1em" viewBox="0 0 512 512">
                            <!--! Font Awesome Free 6.4.2 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license (Commercial License) Copyright 2023 Fonticons, Inc. -->
                            <path d="M377.9 105.9L500.7 228.7c7.2 7.2 11.3 17.1 11.3 27.3s-4.1 20.1-11.3 27.3L377.9 406.1c-6.4 6.4-15 9.9-24 9.9c-18.7 0-33.9-15.2-33.9-33.9l0-62.1-128 0c-17.7 0-32-14.3-32-32l0-64c0-17.7 14.3-32 32-32l128 0 0-62.1c0-18.7 15.2-33.9 33.9-33.9c9 0 17.6 3.6 24 9.9zM160 96L96 96c-17.7 0-32 14.3-32 32l0 256c0 17.7 14.3 32 32 32l64 0c17.7 0 32 14.3 32 32s-14.3 32-32 32l-64 0c-53 0-96-43-96-96L0 128C0 75 43 32 96 32l64 0c17.7 0 32 14.3 32 32s-14.3 32-32 32z"/>
                        </svg>
                        Đăng Xuất
                    </a>
                </div>
            </div>
        </div>
    </div>
</nav>

<style>
  .navbar-brand {
    margin-right: 250px;
  }

  .nav-item dropdown {
    margin-left: 250px;
  }
</style>

<!-- END nav -->

<div class="main-user-content" style="background-color: #e7e6e6; width: 100%">
    <div class="rootContainer">
        <div class="containerCreateKey">
            <a style="display: inline-block; text-align: left; float: left"
               href="${pageContext.request.contextPath}/page/user/general-key-info">Quay về</a>
            <h1 style="color: #a1b85d">Tạo khóa mới</h1>
            <form action="${pageContext.request.contextPath}/page/user/create-a-key-pair-for-user"
                  method="post">
                <div class="form-group">
                    <label for="public-key">Public key:</label>
                    <input type="text" id="public-key"
                           value="<%= publicKey != null ? publicKey : "" %>" readonly>
                </div>
                <div class="button-group">
                    <button class="generate-btn">Tạo mới</button>
                    <button id="save_private_key" type="button" class="save-btn">Lưu key về máy
                    </button>
                </div>
            </form>
        </div>
    </div>

</div>


<!-- loader -->
<div id="ftco-loader" class="show fullscreen">
    <svg class="circular" width="48px" height="48px">
        <circle class="path-bg" cx="24" cy="24" r="22" fill="none" stroke-width="4"
                stroke="#eeeeee"/>
        <circle class="path" cx="24" cy="24" r="22" fill="none" stroke-width="4"
                stroke-miterlimit="10"
                stroke="#F96D00"/>
    </svg>
</div>
<script> var context = "${pageContext.request.contextPath}";
var websocket = "localhost:8080";
</script>
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<script src="${pageContext.request.contextPath}/static/js/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/static/js/jquery-migrate-3.0.1.min.js"></script>
<script src="${pageContext.request.contextPath}/static/js/popper.min.js"></script>
<script src="${pageContext.request.contextPath}/static/js/bootstrap.min.js"></script>
<script src="${pageContext.request.contextPath}/static/js/jquery.easing.1.3.js"></script>
<script src="${pageContext.request.contextPath}/static/js/jquery.waypoints.min.js"></script>
<script src="${pageContext.request.contextPath}/static/js/jquery.stellar.min.js"></script>
<script src="${pageContext.request.contextPath}/static/js/owl.carousel.min.js"></script>
<script src="${pageContext.request.contextPath}/static/js/jquery.magnific-popup.min.js"></script>
<script src="${pageContext.request.contextPath}/static/js/aos.js"></script>
<script src="${pageContext.request.contextPath}/static/js/jquery.animateNumber.min.js"></script>
<script src="${pageContext.request.contextPath}/static/js/bootstrap-datepicker.js"></script>
<script src="${pageContext.request.contextPath}/static/js/scrollax.min.js"></script>
<script src="${pageContext.request.contextPath}/https://maps.googleapis.com/maps/api/js?key=AIzaSyBVWaKrjvy3MaE7SQ74_uJiULgl1JY0H2s&sensor=false"></script>
<script src="${pageContext.request.contextPath}/static/js/main.js"></script>
<script src="${pageContext.request.contextPath}/static/js/web-js/check-password-from-KeyPairUser.js"></script>
<script src="${pageContext.request.contextPath}/static/js/web-js/download-private-key.js?=3"></script>
<script>
  // Kiểm tra giá trị từ server và gọi SweetAlert
  <% if (isCreateKeySuccess != null && isCreateKeySuccess) { %>
  Swal.fire({
    title: 'Thành công!',
    text: 'Tạo key mới thành công !',
    icon: 'success',
    confirmButtonText: 'OK'
  })
  ;
  <% } %>
</script>
</body>
</html>
