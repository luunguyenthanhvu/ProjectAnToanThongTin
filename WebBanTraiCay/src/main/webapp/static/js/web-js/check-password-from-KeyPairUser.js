async function createNewAPairKey() {
  const {value: password} = await Swal.fire({
    title: "Xac minh nguoi dung",
    input: "password",
    inputLabel: "Vui long nhap mat khau de xac minh danh tinh",
    inputPlaceholder: "Nhap mat khau",
    showCancelButton: true,
    inputAttributes: {
      autocapitalize: "off",
      autocorrect: "off"
    }
  });

  if (password) {
    console.log("Password entered:", password); // Kiểm tra giá trị
    const result = await checkUser(password);
    if (result.status === "fail") {
      Swal.fire({
        icon: "error",
        title: "Xac minh that bai",
        text: "Mat khau khong dung. Vui long nhap lai"
      });
    } else {
      window.location.href = `${window.context}/page/user/create-a-key-pair-for-user`;
    }
  }
}

async function reportAPairKey() {
  const {value: password} = await Swal.fire({
    title: "Xac minh nguoi dung",
    input: "password",
    inputLabel: "Vui long nhap mat khau de xac minh danh tinh",
    inputPlaceholder: "Nhap mat khau",
    showCancelButton: true,
    inputAttributes: {
      maxlength: "10",
      autocapitalize: "off",
      autocorrect: "off"
    }
  });
  if (password) {
    console.log("Password entered:", password); // Kiểm tra giá trị
    const result = await checkUser(password);
    if (result.status === "fail") {
      Swal.fire({
        icon: "error",
        title: "Xac minh that bai",
        text: "Mat khau khong dung. Vui long nhap lai"
      });
    } else if (result.status === "inValidKey") {
      Swal.fire({
        icon: "error",
        title: "Khong co key",
        text: "Ban khong co key de report."
      });
    } else {
      window.location.href = `${window.context}/page/user/report-a-key-pair-of-user`;
    }
  }

}

async function checkUser(passwordFromUser) {
  try {
    const response = await fetch(
        `${window.context}/page/user/general-key-info`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded' // Dữ liệu được gửi dạng URL-encoded
          },
          body: `password=${encodeURIComponent(passwordFromUser)}` // Dữ liệu dưới dạng chuỗi
        });

    if (!response.ok) {
      throw new Error(`HTTP error! Status: ${response.status}`);
    }

    const data = await response.json(); // Parse dữ liệu JSON từ response
    console.log(data);
    return data;

  } catch (error) {
    console.error('Error:', error);
  }
}
