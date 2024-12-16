<%--
  Created by IntelliJ IDEA.
  User: PC
  Date: 26/03/2024
  Time: 5:18 PM
  To change this template use File | Settings | File Templates.
--%>

<html lang="en" dir="ltr">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<head>
    <fmt:setLocale value="vi_VN"/>
    <%@ page isELIgnored="false" %>
    <meta charset="UTF-8">
    <title>Quản lý cửa hàng</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/static/css/admin-css/style.css?v=2">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/admin-css/dssp.css">

    <!-- Boxiocns CDN Link -->
    <link href='https://unpkg.com/boxicons@2.0.7/css/boxicons.min.css' rel='stylesheet'>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/static/css/admin-css/update-order.css">
    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.min.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.all.min.js"></script>

</head>
<style>
  div:where(.swal2-container).swal2-center > .swal2-popup {
    grid-column: 2;
    grid-row: 2;
    place-self: center center;
    width: 1000px;
  }

  .flex {
    display: flex;
  }

  .status svg {
    width: 30px;
    height: 30px;
  }

  .table-wrapper {
    max-height: 400px; /* Chiều cao tối đa của bảng */
    overflow-y: auto; /* Kích hoạt cuộn theo chiều dọc */
    margin: 20px 0;
  }

  .table-sanpham {
    width: 100%;
    border-collapse: collapse;
  }

  .table-sanpham th, .table-sanpham td {
    padding: 8px;
    text-align: left;
    border-bottom: 1px solid #ddd;
  }

  .table-sanpham thead th {
    position: sticky;
    top: 0;
    background-color: #E4E9F7;
  }

  /* The Modal (background) */
  .modal {
    display: none; /* Hidden by default */
    position: fixed; /* Stay in place */
    z-index: 1; /* Sit on top */
    left: 0;
    top: 0;
    width: 100%; /* Full width */
    height: 100%; /* Full height */
    overflow: auto; /* Enable scroll if needed */
    background-color: rgb(0, 0, 0); /* Fallback color */
    background-color: rgba(0, 0, 0, 0.4); /* Black w/ opacity */
  }


  .cart-item {
    border: 1px solid;
    padding: 5px 10px;
    margin-bottom: 10px;
  }

  .item.cart-item.flex {
    gap: 20px;
  }

  /* Container Form */
  #update-status-form {
    width: 100%;
    max-width: 400px; /* Giới hạn chiều rộng */
    margin: 20px auto; /* Căn giữa theo chiều ngang */
    padding: 20px;
    background-color: #f9f9f9; /* Màu nền sáng */
    border-radius: 8px; /* Bo góc */
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); /* Hiệu ứng đổ bóng */
    font-family: Arial, sans-serif; /* Font chữ dễ nhìn */
  }

  /* Tiêu đề */
  #update-status-form h3 {
    margin-bottom: 15px;
    font-size: 1.5rem;
    color: #333; /* Màu chữ */
    text-align: center;
    font-weight: bold;
  }

  /* Select Box */
  .option-status {
    width: 100%; /* Chiều rộng đầy đủ */
    padding: 10px;
    margin-bottom: 15px;
    border: 1px solid #ddd;
    border-radius: 5px;
    font-size: 1rem;
    color: #555;
    background-color: #fff;
    box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.1); /* Đổ bóng nhẹ bên trong */
    transition: border-color 0.3s, box-shadow 0.3s;
  }

  .option-status:focus {
    border-color: #007bff; /* Đổi màu viền khi focus */
    box-shadow: 0 0 5px rgba(0, 123, 255, 0.5); /* Hiệu ứng focus */
  }

  /* Nút Submit */
  #update-status-form button {
    width: 100%; /* Nút chiếm toàn bộ chiều rộng */
    padding: 10px;
    font-size: 1rem;
    color: #fff;
    background-color: #007bff; /* Màu xanh */
    border: none;
    border-radius: 5px;
    cursor: pointer;
    transition: background-color 0.3s, box-shadow 0.3s;
  }

  #update-status-form button:hover {
    background-color: #0056b3; /* Màu xanh đậm hơn khi hover */
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.2); /* Hiệu ứng hover */
  }

  #update-status-form button:active {
    background-color: #004080; /* Màu đậm hơn khi nhấn */
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  }

  /* Ẩn Input ẩn */
  input[type="hidden"] {
    display: none;
  }

</style>
<body onload="myFunction()" style="margin:0;">
<div id="loader"></div>
<div style="display:none;" id="myDiv" class="animate-bottom">
    <div class="sidebar close">
        <div class="logo-details">
            <i>
                <svg xmlns="http://www.w3.org/2000/svg" height="1em" viewBox="0 0 640 512">
                    <path d="M36.8 192H603.2c20.3 0 36.8-16.5 36.8-36.8c0-7.3-2.2-14.4-6.2-20.4L558.2 21.4C549.3 8 534.4 0 518.3 0H121.7c-16 0-31 8-39.9 21.4L6.2 134.7c-4 6.1-6.2 13.2-6.2 20.4C0 175.5 16.5 192 36.8 192zM64 224V384v80c0 26.5 21.5 48 48 48H336c26.5 0 48-21.5 48-48V384 224H320V384H128V224H64zm448 0V480c0 17.7 14.3 32 32 32s32-14.3 32-32V224H512z"/>
                </svg>
            </i>
            <span class="logo_name" style="font-size: 19px;">Quản trị viên</span>
            <i class="btn-close-home" style="padding-left: 13px;padding-top: 10px; display: none;">
                <svg xmlns="http://www.w3.org/2000/svg" height="0.8em" viewBox="0 0 384 512">
                    <!--!Font Awesome Free 6.5.2 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2024 Fonticons, Inc.-->
                    <path d="M376.6 84.5c11.3-13.6 9.5-33.8-4.1-45.1s-33.8-9.5-45.1 4.1L192 206 56.6 43.5C45.3 29.9 25.1 28.1 11.5 39.4S-3.9 70.9 7.4 84.5L150.3 256 7.4 427.5c-11.3 13.6-9.5 33.8 4.1 45.1s33.8 9.5 45.1-4.1L192 306 327.4 468.5c11.3 13.6 31.5 15.4 45.1 4.1s15.4-31.5 4.1-45.1L233.7 256 376.6 84.5z"/>
                </svg>
            </i>
        </div>
        <ul class="nav-links">
            <li>
                <div class="iocn-link">
                    <a href="#">
                        <i>
                            <svg xmlns="http://www.w3.org/2000/svg" height="1em"
                                 viewBox="0 0 512 512">
                                <path d="M406.5 399.6C387.4 352.9 341.5 320 288 320H224c-53.5 0-99.4 32.9-118.5 79.6C69.9 362.2 48 311.7 48 256C48 141.1 141.1 48 256 48s208 93.1 208 208c0 55.7-21.9 106.2-57.5 143.6zm-40.1 32.7C334.4 452.4 296.6 464 256 464s-78.4-11.6-110.5-31.7c7.3-36.7 39.7-64.3 78.5-64.3h64c38.8 0 71.2 27.6 78.5 64.3zM256 512A256 256 0 1 0 256 0a256 256 0 1 0 0 512zm0-272a40 40 0 1 1 0-80 40 40 0 1 1 0 80zm-88-40a88 88 0 1 0 176 0 88 88 0 1 0 -176 0z"/>
                            </svg>
                        </i>
                        <span class="link_name">Tài khoản</span>
                    </a>
                    <i class='bx bxs-chevron-down arrow'></i>
                </div>
                <ul class="sub-menu">
                    <li><a class="link_name" href="#">Tài khoản</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/profile">Thông tin tài
                        khoản</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/profile/update-pass">Đổi
                        mật khẩu</a></li>
                </ul>
            </li>
            <li>
                <div class="iocn-link">
                    <a href="#">
                        <i>
                            <svg xmlns="http://www.w3.org/2000/svg" height="1em"
                                 viewBox="0 0 512 512">
                                <path d="M121 32C91.6 32 66 52 58.9 80.5L1.9 308.4C.6 313.5 0 318.7 0 323.9V416c0 35.3 28.7 64 64 64H448c35.3 0 64-28.7 64-64V323.9c0-5.2-.6-10.4-1.9-15.5l-57-227.9C446 52 420.4 32 391 32H121zm0 64H391l48 192H387.8c-12.1 0-23.2 6.8-28.6 17.7l-14.3 28.6c-5.4 10.8-16.5 17.7-28.6 17.7H195.8c-12.1 0-23.2-6.8-28.6-17.7l-14.3-28.6c-5.4-10.8-16.5-17.7-28.6-17.7H73L121 96z"/>
                            </svg>
                        </i>
                        <span class="link_name">Sản phẩm</span>
                    </a>
                    <i class='bx bxs-chevron-down arrow'></i>
                </div>
                <ul class="sub-menu">
                    <li><a class="link_name" href="#">Chức năng</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/product/product-list">Danh
                        sách sản phẩm</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/product/add-new-product">Thêm
                        sản phẩm</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/product/manage-expired">Sản
                        phẩm hết hạn</a></li>
                </ul>
            </li>
            <li>
                <div class="iocn-link">
                    <a href="#">
                        <i>
                            <svg xmlns="http://www.w3.org/2000/svg" height="1em"
                                 viewBox="0 0 576 512">
                                <path d="M0 24C0 10.7 10.7 0 24 0H69.5c22 0 41.5 12.8 50.6 32h411c26.3 0 45.5 25 38.6 50.4l-41 152.3c-8.5 31.4-37 53.3-69.5 53.3H170.7l5.4 28.5c2.2 11.3 12.1 19.5 23.6 19.5H488c13.3 0 24 10.7 24 24s-10.7 24-24 24H199.7c-34.6 0-64.3-24.6-70.7-58.5L77.4 54.5c-.7-3.8-4-6.5-7.9-6.5H24C10.7 48 0 37.3 0 24zM128 464a48 48 0 1 1 96 0 48 48 0 1 1 -96 0zm336-48a48 48 0 1 1 0 96 48 48 0 1 1 0-96z"/>
                            </svg>
                        </i>
                        <span class="link_name">Đơn hàng</span>
                    </a>
                    <i class='bx bxs-chevron-down arrow'></i>
                </div>
                <ul class="sub-menu">
                    <li><a class="link_name" href="#">Đơn hàng</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/order/order-list">Quản lý
                        đơn hàng</a></li>
                </ul>
            </li>
            <li>
                <div class="iocn-link">
                    <a href="#">
                        <i>
                            <svg xmlns="http://www.w3.org/2000/svg" height="1em"
                                 viewBox="0 0 448 512">
                                <path d="M160 80c0-26.5 21.5-48 48-48h32c26.5 0 48 21.5 48 48V432c0 26.5-21.5 48-48 48H208c-26.5 0-48-21.5-48-48V80zM0 272c0-26.5 21.5-48 48-48H80c26.5 0 48 21.5 48 48V432c0 26.5-21.5 48-48 48H48c-26.5 0-48-21.5-48-48V272zM368 96h32c26.5 0 48 21.5 48 48V432c0 26.5-21.5 48-48 48H368c-26.5 0-48-21.5-48-48V144c0-26.5 21.5-48 48-48z"/>
                            </svg>
                        </i>
                        <span class="link_name">Doanh thu</span>
                    </a>
                    <i class='bx bxs-chevron-down arrow'></i>
                </div>
                <ul class="sub-menu">
                    <li><a class="link_name" href="#">Doanh thu</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/monthly-revenue"> Doanh
                        thu cửa hàng</a></li>
                </ul>
            </li>
            <li>
                <div class="iocn-link">
                    <a href="#">
                        <i>
                            <svg xmlns="http://www.w3.org/2000/svg" height="1em"
                                 viewBox="0 0 640 512">
                                <path d="M144 160A80 80 0 1 0 144 0a80 80 0 1 0 0 160zm368 0A80 80 0 1 0 512 0a80 80 0 1 0 0 160zM0 298.7C0 310.4 9.6 320 21.3 320H234.7c.2 0 .4 0 .7 0c-26.6-23.5-43.3-57.8-43.3-96c0-7.6 .7-15 1.9-22.3c-13.6-6.3-28.7-9.7-44.6-9.7H106.7C47.8 192 0 239.8 0 298.7zM320 320c24 0 45.9-8.8 62.7-23.3c2.5-3.7 5.2-7.3 8-10.7c2.7-3.3 5.7-6.1 9-8.3C410 262.3 416 243.9 416 224c0-53-43-96-96-96s-96 43-96 96s43 96 96 96zm65.4 60.2c-10.3-5.9-18.1-16.2-20.8-28.2H261.3C187.7 352 128 411.7 128 485.3c0 14.7 11.9 26.7 26.7 26.7H455.2c-2.1-5.2-3.2-10.9-3.2-16.4v-3c-1.3-.7-2.7-1.5-4-2.3l-2.6 1.5c-16.8 9.7-40.5 8-54.7-9.7c-4.5-5.6-8.6-11.5-12.4-17.6l-.1-.2-.1-.2-2.4-4.1-.1-.2-.1-.2c-3.4-6.2-6.4-12.6-9-19.3c-8.2-21.2 2.2-42.6 19-52.3l2.7-1.5c0-.8 0-1.5 0-2.3s0-1.5 0-2.3l-2.7-1.5zM533.3 192H490.7c-15.9 0-31 3.5-44.6 9.7c1.3 7.2 1.9 14.7 1.9 22.3c0 17.4-3.5 33.9-9.7 49c2.5 .9 4.9 2 7.1 3.3l2.6 1.5c1.3-.8 2.6-1.6 4-2.3v-3c0-19.4 13.3-39.1 35.8-42.6c7.9-1.2 16-1.9 24.2-1.9s16.3 .6 24.2 1.9c22.5 3.5 35.8 23.2 35.8 42.6v3c1.3 .7 2.7 1.5 4 2.3l2.6-1.5c16.8-9.7 40.5-8 54.7 9.7c2.3 2.8 4.5 5.8 6.6 8.7c-2.1-57.1-49-102.7-106.6-102.7zm91.3 163.9c6.3-3.6 9.5-11.1 6.8-18c-2.1-5.5-4.6-10.8-7.4-15.9l-2.3-4c-3.1-5.1-6.5-9.9-10.2-14.5c-4.6-5.7-12.7-6.7-19-3L574.4 311c-8.9-7.6-19.1-13.6-30.4-17.6v-21c0-7.3-4.9-13.8-12.1-14.9c-6.5-1-13.1-1.5-19.9-1.5s-13.4 .5-19.9 1.5c-7.2 1.1-12.1 7.6-12.1 14.9v21c-11.2 4-21.5 10-30.4 17.6l-18.2-10.5c-6.3-3.6-14.4-2.6-19 3c-3.7 4.6-7.1 9.5-10.2 14.6l-2.3 3.9c-2.8 5.1-5.3 10.4-7.4 15.9c-2.6 6.8 .5 14.3 6.8 17.9l18.2 10.5c-1 5.7-1.6 11.6-1.6 17.6s.6 11.9 1.6 17.5l-18.2 10.5c-6.3 3.6-9.5 11.1-6.8 17.9c2.1 5.5 4.6 10.7 7.4 15.8l2.4 4.1c3 5.1 6.4 9.9 10.1 14.5c4.6 5.7 12.7 6.7 19 3L449.6 457c8.9 7.6 19.2 13.6 30.4 17.6v21c0 7.3 4.9 13.8 12.1 14.9c6.5 1 13.1 1.5 19.9 1.5s13.4-.5 19.9-1.5c7.2-1.1 12.1-7.6 12.1-14.9v-21c11.2-4 21.5-10 30.4-17.6l18.2 10.5c6.3 3.6 14.4 2.6 19-3c3.7-4.6 7.1-9.4 10.1-14.5l2.4-4.2c2.8-5.1 5.3-10.3 7.4-15.8c2.6-6.8-.5-14.3-6.8-17.9l-18.2-10.5c1-5.7 1.6-11.6 1.6-17.5s-.6-11.9-1.6-17.6l18.2-10.5zM472 384a40 40 0 1 1 80 0 40 40 0 1 1 -80 0z"/>
                            </svg>
                        </i>
                        <span class="link_name">Người dùng</span>
                    </a>
                    <i class='bx bxs-chevron-down arrow'></i>
                </div>
                <ul class="sub-menu">
                    <li><a class="link_name" href="#">Người dùng</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/user/user-list">Danh sách
                        người dùng</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/user/add-user">Thêm người
                        dùng</a></li>
                </ul>
            </li>
            <li>
                <div class="iocn-link">
                    <a href="#">
                        <i>
                            <svg xmlns="http://www.w3.org/2000/svg" height="16" width="18"
                                 viewBox="0 0 576 512">
                                <!--!Font Awesome Free 6.5.1 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2023 Fonticons, Inc.-->
                                <path d="M64 32C46.3 32 32 46.3 32 64V304v48 80c0 26.5 21.5 48 48 48H496c26.5 0 48-21.5 48-48V304 152.2c0-18.2-19.4-29.7-35.4-21.1L352 215.4V152.2c0-18.2-19.4-29.7-35.4-21.1L160 215.4V64c0-17.7-14.3-32-32-32H64z"/>
                            </svg>
                        </i>
                        <span class="link_name">Nhà cung cấp</span>
                    </a>
                    <i class='bx bxs-chevron-down arrow'></i>
                </div>
                <ul class="sub-menu">
                    <li><a class="link_name" href="#">Nhà cung cấp</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/provider/provider-list">Danh
                        sách nhà cung cấp</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/provider/add-provider">Thêm
                        nhà cung cấp</a></li>
                </ul>
            </li>

            <li>
                <div class="iocn-link">
                    <a href="#">
                        <i>
                            <svg xmlns="http://www.w3.org/2000/svg" height="16" width="18"
                                 viewBox="0 0 512 512">
                                <!--!Font Awesome Free 6.5.1 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2024 Fonticons, Inc.-->
                                <path d="M384 48c8.8 0 16 7.2 16 16V448c0 8.8-7.2 16-16 16H96c-8.8 0-16-7.2-16-16V64c0-8.8 7.2-16 16-16H384zM96 0C60.7 0 32 28.7 32 64V448c0 35.3 28.7 64 64 64H384c35.3 0 64-28.7 64-64V64c0-35.3-28.7-64-64-64H96zM240 256a64 64 0 1 0 0-128 64 64 0 1 0 0 128zm-32 32c-44.2 0-80 35.8-80 80c0 8.8 7.2 16 16 16H336c8.8 0 16-7.2 16-16c0-44.2-35.8-80-80-80H208zM512 80c0-8.8-7.2-16-16-16s-16 7.2-16 16v64c0 8.8 7.2 16 16 16s16-7.2 16-16V80zM496 192c-8.8 0-16 7.2-16 16v64c0 8.8 7.2 16 16 16s16-7.2 16-16V208c0-8.8-7.2-16-16-16zm16 144c0-8.8-7.2-16-16-16s-16 7.2-16 16v64c0 8.8 7.2 16 16 16s16-7.2 16-16V336z"/>
                            </svg>
                        </i>
                        <span class="link_name">Liên hệ</span>
                    </a>
                    <i class='bx bxs-chevron-down arrow'></i>
                </div>
                <ul class="sub-menu">
                    <li><a href="${pageContext.request.contextPath}/admin/contact-form">Danh sách
                        liên hệ của khách hàng</a></li>
                </ul>
            </li>
            <li>
                <div class="iocn-link">
                    <a href="#">
                        <i>
                            <svg xmlns="http://www.w3.org/2000/svg" height="16" width="18"
                                 viewBox="0 0 384 512">
                                <!--!Font Awesome Free 6.5.2 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2024 Fonticons, Inc.-->
                                <path fill="#e9ecf1"
                                      d="M320 464c8.8 0 16-7.2 16-16V160H256c-17.7 0-32-14.3-32-32V48H64c-8.8 0-16 7.2-16 16V448c0 8.8 7.2 16 16 16H320zM0 64C0 28.7 28.7 0 64 0H229.5c17 0 33.3 6.7 45.3 18.7l90.5 90.5c12 12 18.7 28.3 18.7 45.3V448c0 35.3-28.7 64-64 64H64c-35.3 0-64-28.7-64-64V64z"/>
                            </svg>
                        </i>
                        <span class="link_name">Quản lý log</span>
                    </a>
                    <i class='bx bxs-chevron-down arrow'></i>
                </div>
                <ul class="sub-menu">
                    <li><a href="${pageContext.request.contextPath}/admin/log/log-center">Theo dõi
                        và điều chỉnh log</a></li>
                </ul>
            </li>

            <li>
                <div class="profile-details">
                    <div class="profile-content">
                        <c:choose>
                            <c:when test="${not empty admin.getImg()}">
                                <!-- Ảnh mới từ sau khi đổi ảnh -->
                                <img src="${admin.getImg()}" alt="profileImg">
                            </c:when>
                            <c:otherwise>
                                <!-- Ảnh mặc định khi mới đăng ký -->
                                <img src="${pageContext.request.contextPath}/static/images/accountPicture.png"
                                     alt="profileImg">
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="name-job">
                        <div class="profile_name">${loginedUser.getUsername()}</div>
                        <div class="job">${role}</div>
                    </div>
                    <a href="${pageContext.request.contextPath}/page/logout">
                        <i style="transform: rotate(180deg); ">
                            <svg xmlns="http://www.w3.org/2000/svg" height="1em"
                                 viewBox="0 0 512 512">
                                <path d="M502.6 278.6c12.5-12.5 12.5-32.8 0-45.3l-128-128c-12.5-12.5-32.8-12.5-45.3 0s-12.5 32.8 0 45.3L402.7 224 192 224c-17.7 0-32 14.3-32 32s14.3 32 32 32l210.7 0-73.4 73.4c-12.5 12.5-12.5 32.8 0 45.3s32.8 12.5 45.3 0l128-128zM160 96c17.7 0 32-14.3 32-32s-14.3-32-32-32L96 32C43 32 0 75 0 128L0 384c0 53 43 96 96 96l64 0c17.7 0 32-14.3 32-32s-14.3-32-32-32l-64 0c-17.7 0-32-14.3-32-32l0-256c0-17.7 14.3-32 32-32l64 0z"/>
                            </svg>
                        </i>
                    </a>
                </div>
            </li>
        </ul>
    </div>
    <div class="home-section">
        <div class="home-content">
            <svg class='bx-menu' xmlns="http://www.w3.org/2000/svg" height="1em"
                 viewBox="0 0 448 512">
                <path d="M0 96C0 78.3 14.3 64 32 64H416c17.7 0 32 14.3 32 32s-14.3 32-32 32H32C14.3 128 0 113.7 0 96zM0 256c0-17.7 14.3-32 32-32H416c17.7 0 32 14.3 32 32s-14.3 32-32 32H32c-17.7 0-32-14.3-32-32zM448 416c0 17.7-14.3 32-32 32H32c-17.7 0-32-14.3-32-32s14.3-32 32-32H416c17.7 0 32 14.3 32 32z"/>
            </svg>
            <span class="text">Chi tiết đơn hàng</span>
        </div>
        <div class="container" style="margin: 30px 30px 0 30px">
            <div class="main-container">
                <div class="left-container">
                    <h4>Thông tin người đặt hàng</h4>
                    <div class="user-name-img">
                        <c:if test="${not empty user.getImg() }">
                            <div class="img-user">
                                <img src="${user.getImg()}" alt="profileImg">
                            </div>
                        </c:if>
                        <c:if test="${empty user.getImg() }">
                            <div class="img-user">
                                <img src="${pageContext.request.contextPath}/static/images/accountPicture.png"
                                     alt="profileImg">
                            </div>
                        </c:if>

                        <div class="user-name">
                            <span>${bill.getLastName()} ${bill.getFirstName()}</span>
                        </div>
                    </div>

                    <div class="user-info">
                        <div class="simple-info">
                            <span>Số điện thoại:</span>
                            <span>${bill.getPhoneNumber()}</span>
                        </div>
                        <div class="simple-info">
                            <span>Email:</span>
                            <span>${bill.getEmail()}</span>
                        </div>
                        <div class="simple-info">
                            <span>Địa chỉ:</span>
                            <span>${bill.getStreetAddress()}.</span>
                        </div>
                    </div>

                </div>
                <div class="right-container">
                    <div>
                        <div id="update-status-form">
                            <c:choose>
                                <c:when test="${bill.getStatus() == 'Đã hủy'}">
                                    <h3>Đơn hàng đã bị hủy bạn ko thể cập nhật trạng thái</h3>
                                </c:when>
                                <c:otherwise>
                                    <form action="${pageContext.request.contextPath}/admin/provider/updateOrder"
                                          method="post">
                                        <h3>Cập nhập trạng thái</h3>
                                        <input type="hidden" name="idBill" value="${idBill}">
                                        <select id="status-select" class="option-status"
                                                name="selectedStatus"
                                                data-current-status="${bill.getStatus()}">
                                            <option value="Đang chuẩn bị">Đang chuẩn bị</option>
                                            <option value="Đang giao">Đang giao</option>
                                            <option value="Đã giao">Đã giao</option>
                                        </select>
                                        <button type="submit">Cập nhập trạng thái</button>
                                    </form>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="table-wrapper">
                            <table class="table-sanpham">
                                <h4 style="padding: 10px">Thông tin đơn hàng</h4>
                                <tr style="background-color: #E4E9F7 ">
                                    <th style="width: 250px;">Tên sản phẩm</th>
                                    <th style="width: 100px;">Hình ảnh</th>
                                    <th style="width: 150px;">Doanh mục</th>
                                    <th style="width: 100px;">Giá tiền</th>
                                    <th style="width: 100px;">Số lượng</th>
                                    <th style="width: 100px;">Tổng tiền</th>
                                </tr>

                                <c:forEach items="${detailList}" var="billDetail"
                                           varStatus="loopStatus">

                                    <tr>
                                        <td>${billDetail.getProducts().getNameOfProduct()}</td>

                                        <td class="img-product" style="width: 80px; height: 80px"
                                            data-assets="${billDetail.getProducts().getImgPublicId()}">
                                            <img style="width: 80px; height: 80px"
                                                 data-assets="${billDetail.getProducts().getImgPublicId()}"
                                                 src="${pageContext.request.contextPath}/static/images/loading-cat.gif">
                                        </td>
                                        <td>${billDetail.getProducts().getCategory()}</td>
                                        <td class="gia-thanh"><fmt:formatNumber pattern="#,##0 ₫"
                                                                                value="${billDetail.getProducts().getPrice()}"/></td>
                                        <td class="so-luong"
                                            value="${billDetail.getQuantity()}">${billDetail.getQuantity()}</td>
                                        <td class="so-luong">
                                            <fmt:formatNumber pattern="#,##0 đ"
                                                              value="${billDetail.getQuantity() * billDetail.getProducts().getPrice()}"/>
                                        </td>
                                        <input class="tong-tien" type="hidden"
                                               value="${billDetail.getProducts().getPrice() *billDetail.getQuantity()}">
                                    </tr>
                                </c:forEach>

                            </table>
                        </div>
                        <div class="total-product">
                            <div class="grid-container">
                                <div class="grid-item">Tổng số lượng:</div>
                                <div class="grid-item total-amount">
                                    <c:set var="totalQuantity" value="0"/>
                                    <c:forEach var="detail" items="${detailList}">
                                        <c:set var="totalQuantity"
                                               value="${totalQuantity + detail.quantity}"/>
                                    </c:forEach>
                                    ${totalQuantity}
                                </div>

                                <div class="grid-item">Tổng phụ:</div>
                                <div class="grid-item total-sup"></div>
                                <div class="grid-item">Giá vận chuyển:</div>
                                <div class="grid-item ship-price"></div>
                                <div class="grid-item total-text">Tổng tiền:</div>
                                <div class="grid-item total-money"></div>
                            </div>
                        </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<script>

  let arrow = document.querySelectorAll(".arrow");
  let closeSideBarBtn = document.querySelector(".btn-close-home");

  for (var i = 0; i < arrow.length; i++) {
    arrow[i].addEventListener("click", (e) => {
      let arrowParent = e.target.parentElement.parentElement; // Trở về phần tử cha của mũi tên
      arrowParent.classList.toggle("showMenu");
    });
  }

  let sidebar = document.querySelector(".sidebar");
  let sidebarBtn = document.querySelector(".bx-menu");

  sidebarBtn.addEventListener("click", () => {
    sidebar.classList.toggle("close");

    // Sau khi toggle sidebar, kiểm tra và điều chỉnh hiển thị nút đóng sidebar
    if (!sidebar.classList.contains("close")) {
      closeSideBarBtn.style.display = "inline-block"; // Hiển thị nút đóng
    } else {
      closeSideBarBtn.style.display = "none"; // Ẩn nút đóng
    }
  });

  closeSideBarBtn.addEventListener("click", () => {
    sidebar.classList.toggle("close");
    closeSideBarBtn.style.display = "none"; // Luôn ẩn nút đóng khi click để đóng sidebar
  });

  var myVar;

  function myFunction() {
    myVar = setTimeout(showPage, 800);
  }

  function showPage() {
    document.getElementById("loader").style.display = "none";
    document.getElementById("myDiv").style.display = "block";
  }

  let totalAmountHTMl = document.querySelector(".total-amount");
  let totalSub = document.querySelector(".total-sup");
  let finalPriceHTML = document.querySelector(".total-money");
  let shipPriceHTML = document.querySelector(".ship-price");
  let totalAmount = 0;
  let totalPrice = 0;
  let finalPrice = 0;
  let shipPrice = 30000;

  document.addEventListener('DOMContentLoaded', function () {
    shipPriceHTML.innerHTML = shipPrice.toLocaleString('vi-VN',
        {style: 'currency', currency: 'VND'});
    let getAllGT = document.querySelectorAll(".tong-tien");
    getAllGT.forEach(function (getAll) {
      totalPrice += Number(getAll.value);
    });

    totalSub.innerHTML = totalPrice.toLocaleString('vi-VN',
        {style: 'currency', currency: 'VND'});
    finalPrice = totalPrice + shipPrice;
    finalPriceHTML.innerHTML = finalPrice.toLocaleString('vi-VN',
        {style: 'currency', currency: 'VND'});
  });

  document.addEventListener("DOMContentLoaded", function () {
    const statusSelect = document.getElementById("status-select");

    // Lấy trạng thái hiện tại từ thuộc tính data-current-status
    const currentStatus = statusSelect.dataset.currentStatus;

    // Map các trạng thái hợp lệ tiếp theo
    const validTransitions = {
      "Đang chuẩn bị": ["Đang giao"],
      "Đang giao": ["Đã giao"],
      "Đã giao": [] // Không có trạng thái tiếp theo
    };

    // Xử lý các tùy chọn trong <select>
    // Đặt trạng thái hiện tại làm giá trị mặc định trong <select>
    Array.from(statusSelect.options).forEach(option => {
      if (option.value === currentStatus) {
        option.selected = true; // Đặt trạng thái hiện tại là được chọn
      }

      // Vô hiệu hóa các trạng thái không hợp lệ
      if (!validTransitions[currentStatus]?.includes(option.value) && option.value
          !== currentStatus) {
        option.disabled = true;
      }
    });
  });

</script>
</body>
<script src="https://kit.fontawesome.com/4c38acb8c6.js" crossorigin="anonymous"></script>
<script> var context = "${pageContext.request.contextPath}";
</script>
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
<script src="${pageContext.request.contextPath}/static/js/google-map.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/cloudinary-core/2.11.2/cloudinary-core-shrinkwrap.min.js"></script>
<script src="${pageContext.request.contextPath}/static/js/main.js"></script>
<script src="${pageContext.request.contextPath}/static/js/admin-js/updateOrder.js"></script>
</html>
