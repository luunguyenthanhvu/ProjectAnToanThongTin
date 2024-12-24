async function hashInfoOrder() {
    console.log("admin")
    console.log(document.getElementById("lastname").value)
    const params = new URLSearchParams({
        ho_nguoi_dung: document.getElementById("lastname").value,
        ten_nguoi_dung: document.getElementById("firstname").value,
        dia_chi_nguoi_dung: document.getElementById("street-address").value,
        provinceName: document.getElementById("hiddenProvinceName").value,
        districtName: document.getElementById("hiddenDistrictName").value,
        sdt_nguoi_dung: document.getElementById("phone").value,
        email_nguoi_dung: document.getElementById("email").value,
        delivery_fee: document.getElementById("hidden_delivery_fee").value,
        note_nguoi_dung: document.getElementById("note_user").value,
    });

    fetch('https://localhost:8443/page/order/hash-info-order', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded', // Đổi content-type
        },
        body: params.toString(), // Chuỗi được mã hóa URL
    })
        .then((response) => {
            if (!response.ok) {
                return response.text().then((text) => {
                    throw new Error(`Error ${response.status}: ${text}`);
                });
            }
            return response.json(); // Parse JSON từ response nếu thành công
        })
        .then((data) => {
            console.log('JSON data:', data);
            if (data.invalidInfo === "true") {
                alert("Thông tin không hợp lệ!");
            } else {
                console.log('Hash info order:', data.data);
                document.getElementById("info_hash_for_user").value = data.data;
            }
        })
        .catch((error) => {
            console.error('There has been a problem with your fetch operation:', error);
        });
}

document.getElementById("buttonHashInfo").addEventListener("click", hashInfoOrder);
