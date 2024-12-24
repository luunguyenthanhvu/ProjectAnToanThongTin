async function downloadPrivateKey() {
    try {
        const response = await fetch("https://localhost:8443/download/private-key", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded' // Dữ liệu được gửi dạng URL-encoded
            },
        });

        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        // Lấy blob (dữ liệu nhị phân) từ phản hồi
        const blob = await response.blob();

        // Tạo URL tạm thời từ blob
        const url = window.URL.createObjectURL(blob);

        // Tạo thẻ <a> để kích hoạt tải xuống
        const a = document.createElement('a');
        a.href = url;
        a.download = 'private_key.pem'; // Tên file tải xuống
        document.body.appendChild(a);

        // Kích hoạt hành động tải file và sau đó gỡ bỏ thẻ <a>
        a.click();
        a.remove();

        // Hủy URL tạm thời để giải phóng bộ nhớ
        window.URL.revokeObjectURL(url);

        console.log("Tải file thành công!");
    } catch (error) {
        console.error('Error:', error);
    }
}

// Gắn sự kiện vào nút bấm
document.getElementById('save_private_key').addEventListener("click", downloadPrivateKey);
