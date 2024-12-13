async function createNewAPairKey() {
  const { value: password } = await Swal.fire({
    title: "Xác minh người dùng",
    input: "password",
    inputLabel: "Vui lòng nhập mật khẩu để xác minh danh tính",
    inputPlaceholder: "Nhập mật khẩu",
    showCancelButton: true,
    inputAttributes: {
      autocapitalize: "off",
      autocorrect: "off"
    }
  });

  if (password) {
    console.log("Password entered:", password); // Kiểm tra giá trị
    const result = await checkUser(password);
    if(result.status === "fail"){
      Swal.fire({
        icon: "error",
        title: "Xác minh thất bại",
        text: "Mật khẩu không đúng. Vui lòng nhập lại"
      });
    }else{
      window.location.href = `http://localhost:8080/page/user/create-a-key-pair-for-user`;
    }
  }
}

async function reportAPairKey(){
  const {value: password} = await Swal.fire({
    title: "Xác minh người dùng",
    input: "password",
    inputLabel: "Vui lòng nhập mật khẩu để xác minh danh tính",
    inputPlaceholder: "Nhập mật khẩu",
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
    if(result.status === "fail"){
      Swal.fire({
        icon: "error",
        title: "Xác minh thất bại",
        text: "Mật khẩu không đúng. Vui lòng nhập lại"
      });
    }else{
      window.location.href = `http://localhost:8080/page/user/report-a-key-pair-of-user`;
    }
  }

}

async function checkUser(passwordFromUser) {
  try {
    const response = await fetch("http://localhost:8080/page/user/general-key-info", {
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