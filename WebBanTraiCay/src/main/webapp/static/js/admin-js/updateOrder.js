'use strict';

const cloudName = 'dter3mlpl';
const apiKey = '899244476586798';
const cl = cloudinary.Cloudinary.new({cloud_name: cloudName});

// Cập nhật ảnh sản phẩm
$('.table-wrapper .table-sanpham .img-product img').each((_, elements) => {
  const publicId = $(elements).data('assets');
  const imgUrl = cl.url(publicId);
  const imgDefault = `${window.context}/static/images/default-fruit.jpg`;

  $(elements).prop('src', imgUrl || imgDefault);
});

// Hàm lấy thông tin lô hàng
function getShipmentDetails(productId) {
  Swal.fire({
    title: 'Loading...',
    text: 'Fetching shipment details...',
    showConfirmButton: false,
    didOpen: () => {
      Swal.showLoading();

      $.ajax({
        url: `${window.context}/api/order-details/${productId}`,
        method: 'GET',
        success: function (data) {
          if (Array.isArray(data) && data.length > 0) {
            let itemsHtml = '';
            data.forEach(item => {
              const dateIn = new Date(
                  item.dateIn.year,
                  item.dateIn.monthValue - 1,
                  item.dateIn.dayOfMonth,
                  item.dateIn.hour,
                  item.dateIn.minute,
                  item.dateIn.second
              );
              const formattedDateIn = dateIn.toLocaleDateString() + ' '
                  + dateIn.toLocaleTimeString();

              itemsHtml += `
                <div class="item cart-item flex">
                  <input style="cursor: pointer; width: 30px; height: 30px" type="checkbox" id="${item.id}" value="${item.id}">
                  <img style="width: 60px; height: 60px" src="${window.context}/static/images/accountPicture.png" alt="profileImg">
                  <div>
                    <p>Tên sản phẩm:</p>
                    <span>${item.productName}</span>
                  </div>
                  <div>
                    <p>Ngày nhập:</p>
                    <span>${formattedDateIn}</span>
                  </div>
                  <div>
                    <span>Số lượng:</span>
                    <input type="number" min="1" style="width: 60px;">
                    <span>Tồn kho:</span>
                    <span>${item.quantity}</span>
                  </div>
                </div>`;
            });

            Swal.fire({
              title: 'Chọn lô hàng',
              html: `<div class="custom-swal-content" style="padding: 20px;">${itemsHtml}</div>`,
              confirmButtonText: 'OK'
            });
          } else {
            Swal.fire({
              icon: 'error',
              title: 'Error',
              text: 'No shipment details found.'
            });
          }
        },
        error: function () {
          Swal.fire({
            icon: 'error',
            title: 'Error',
            text: 'Failed to fetch shipment details.'
          });
        }
      });
    }
  });
}

// Kiểm tra tính hợp lệ chữ ký
function checkSignature(idBill) {
  // Hiển thị SweetAlert với loading
  Swal.fire({
    title: 'Kiểm tra tính hợp lệ',
    text: 'Hệ thống đang kiểm tra chữ ký...',
    allowOutsideClick: false, // Không cho phép click ra ngoài
    didOpen: () => {
      Swal.showLoading(); // Bắt đầu hiển thị loading
    }
  });

  // Gửi AJAX request sau 2 giây
  setTimeout(() => {
    $.ajax({
      url: `${window.context}/api/order-details/check-signature`,
      type: 'POST',
      contentType: 'application/json',
      data: JSON.stringify({idBill}),
      success: function (response) {
        console.log(response)
        Swal.close(); // Đóng loading sau khi nhận phản hồi
        // Kiểm tra phản hồi từ server
        if (response.message === "Verify success!") {
          Swal.fire({
            icon: 'success',
            title: 'Cập nhật trạng thái thành công!',
            text: 'Bạn có thể cập nhật trạng thái đơn hàng.'
          }).then(() => {
            $('#update-status-form').show();
            $('#check-signature-btn').hide();
          });
        } else {
          Swal.fire({
            icon: 'error',
            title: 'Cập nhật thất bại',
            text: 'Không thể cập nhật trạng thái. Vui lòng thử lại.'
          });
        }
      },
      error: function () {
        Swal.close(); // Đóng loading nếu xảy ra lỗi
        Swal.fire({
          icon: 'error',
          title: 'Lỗi hệ thống',
          text: 'Không thể kiểm tra chữ ký. Vui lòng thử lại.'
        });
      }
    });
  }, 2000); // Đợi 2 giây trước khi gửi request
}

