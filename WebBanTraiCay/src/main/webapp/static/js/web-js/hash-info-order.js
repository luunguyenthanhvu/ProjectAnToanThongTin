async function hashInfoOrder() {
    fetch('http://localhost:8080/page/order/hash-info-order', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            ho_nguoi_dung: document.getElementById("lastname").value,
            ten_nguoi_dung: document.getElementById("firstname").value,
            dia_chi_nguoi_dung: document.getElementById("street-address").value,
            provinceName: document.getElementById("provinces").value,
            districtName: document.getElementById("districts").value,
            sdt_nguoi_dung: document.getElementById("phone").value,
            email_nguoi_dung: document.getElementById("email").value,
            delivery_fee: document.getElementById("hidden_delivery_fee").value,
            note_nguoi_dung: document.getElementById("note_user").value,
        }),
    })
        .then((response) => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json(); // Parse JSON từ response
        })
        .then((data) => {
            console.log('JSON data:', data); // Dữ liệu JSON nhận được từ server
            if (data.invalidInfo === "true") {
                alert("Thông tin không hợp lệ!");
            } else {
                console.log('Hash info order:', data.data); // Hash của đơn hàng
            }
        })
        .catch((error) => {
            console.error('There has been a problem with your fetch operation:', error);
        });

}
document.getElementById("buttonHashInfo").addEventListener("click", hashInfoOrder);
