let notificationDropdown = document.getElementById("notification-dropdown2");
let statusNotify= 0;
let boldNotification= false;
let isFirstNotificationClicked = false;

$(document).ready(function () {
    // Xử lý sự kiện khi click vào notification-dropdown1
    $("#notification-dropdown1").click(function () {
        // Thêm nội dung vào dropdown-menu
        $("#userbox .dropdown-menu").html(`
                <ul class="list-unstyled mb-2">
                    <li class="divider"></li>
                    <li>
                        <a role="menuitem" tabindex="-1" href="/account"><i class="bx bx-user-circle"></i>Thông tin tài khoản</a>
                    </li>
                    <li>
                        <a role="menuitem" tabindex="-1" href="/api/logout"><i class="bx bx-power-off"></i> Đăng xuất</a>
                    </li>
                </ul>
            `);
    });

    $("#notification-dropdown2").click(function() {

        $.ajax({
            url: '/api/get-notification-list?status=' + statusNotify,
            type: 'GET',
            contentType: "application/json; charset=utf-8",
            success: function (data) {
                $('.nav-link.active').toggleClass("active");
                $('.notification-status[data-status=' + statusNotify + ']').toggleClass("active");
                showListNotification(data,statusNotify);
                statusNotify=0;
            },
            error: function (data) {
                toastr.warning(data.responseJSON.message);
            }
        });
        $("#orderStatusModal").modal("show");
    });


    $('.notification-status').click(function () {
        let status = $(this).data('status');
        console.log(status);
        $.ajax({
            url: '/api/get-notification-list?status=' + status,
            type: 'GET',
            contentType: "application/json; charset=utf-8",
            success: function (data) {
                $('.nav-link.active').toggleClass("active");
                $('.notification-status[data-status=' + status + ']').toggleClass("active");
                console.log(data);
                showListNotification(data,status);
            },
            error: function (data) {
                toastr.warning(data.responseJSON.message);
            }
        });
    })
    function formatTimestamp(timestamp) {
        const date = new Date(timestamp);
        const day = String(date.getDate()).padStart(2, '0');
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const year = date.getFullYear();
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        return `${day}/${month}/${year} ${hours}:${minutes}`;
    }
    function showListNotification(notifications,status) {
        let html = '';
        for (let i = 0; i < notifications.length; i++) {
            let notification = notifications[i];
            let statusClass = '';
            let statusText = '';
            let time =formatTimestamp(notification.lastChangeTime);
            let href=`/admin/orders/update/${notification.id}`

            if (boldNotification && i === 0) {
                // Nếu boldNotification là true và là notification đầu tiên, thêm class font-weight-bold
                statusClass += ' font-weight-bold';

            }
            switch (status) {
                case 0:
                    statusClass += ' list-group-item-info';
                    statusText = 'Chờ xác nhận';
                    break;
                case 1:
                    statusClass += ' list-group-item-warning';
                    statusText = 'Chờ lấy hàng';
                    break;
                case 2:
                    statusClass += ' list-group-item-success';
                    statusText = 'Đang được giao';
                    break;
                case 3:
                    statusClass += ' list-group-item-danger';
                    statusText = 'Yêu cầu trả lại';
                    time =formatTimestamp(notification.requestDate);
                    href=`/admin/orders/return/${notification.id}`
                    break;
                case 4:
                    statusClass += ' list-group-item-secondary';
                    statusText = 'Đang xử lý trả hàng';
                    href=`/admin/orders/return/${notification.id}`
                    break;
                case 5:
                    statusClass += ' list-group-item-dark';
                    statusText = 'Đã hủy đơn';
                    break;
                default:
                    statusClass += ' list-group-item-info';
                    statusText = 'Trạng thái không xác định';
            }

            html += `
              <a href="${href}" class="list-group-item ${statusClass} " style="word-wrap: break-word;" onclick="handleNotificationClick(this, ${i})">
                  <p class="mb-1">Thông báo: Đơn hàng ${notification.id} đã được đặt bởi tài khoản ${notification.createdEmail} ${statusText.toLowerCase()}</p>
                  <small>${time}</small>
                </a>
        `
        }
        if(notifications.length < 1){
            $('#list-notification').html("<div id=\"noDataNotification\" style=\"text-align: center; margin-top: 20px; font-style: italic;\">Không có dữ liệu để hiển thị</div>");
        }else{
            $('#list-notification').html(html);
        }

    }
// Hàm xử lý sự kiện click
    function handleNotificationClick(element, index) {
        // Chuyển boldNotification về false sau khi người dùng nhấn vào
        boldNotification = false;

        // Bỏ class font-weight-bold khỏi tất cả các thẻ <a>
        $('.list-group-item').removeClass('font-weight-bold');

        // Nếu là lần đầu tiên người dùng nhấn vào, không in đậm nữa
        if (!isFirstNotificationClicked) {
            isFirstNotificationClicked = true;
        } else {
            // Thêm class font-weight-bold chỉ cho thẻ được click
            $(element).addClass('font-weight-bold');
        }
    }

});


function getCookie(name) {
    let value = "; " + document.cookie;
    let parts = value.split("; " + name + "=");
    if (parts.length == 2) return parts.pop().split(";").shift();
}

const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:8080/websocket'
});
connect();
function connect() {
    let token = getCookie('JWT_TOKEN');
    console.log("token "+token);
    stompClient.connectHeaders = {
        'authorization': 'Bearer ' + token
    };// Lấy token từ cookie
    stompClient.activate();
}


stompClient.onConnect = (frame) => {

    console.log(frame);
    let name= '/'+frame.headers['user-name']
    console.log(frame.headers['user-name']);

    const isAuthenticated = frame.command === 'CONNECTED';
    console.log('Is Authenticated: ' + isAuthenticated);

    stompClient.subscribe('/queue/admin/notification', function(notification) {

        shakeBell();
        // Xử lý tin nhắn tại đây
        let messageBody = JSON.parse(notification.body);
        console.log('Received message: ', messageBody);
        statusNotify=messageBody;
        boldNotification=true;

    });

    stompClient.subscribe('/queue/notification', (connectionStatus) => {
        console.log('Received message: ', connectionStatus);
        console.log('Connection Status: ' +  connectionStatus.body);
    });



};
stompClient.onWebSocketError = (error) => {
    console.error('Error with websocket', error);
};

stompClient.onStompError = (frame) => {
    console.error('Broker reported error: ' + frame.headers['message']);
    console.error('Additional details: ' + frame.body);
};

function sendNotification(postId) {
    stompClient.publish({
        destination: "/app/notification",
        body: JSON.stringify(postId)
    });
}

// Hàm này sẽ tạo hiệu ứng rung cho biểu tượng chuông
function shakeBell() {
    // Lấy biểu tượng chuông

    // Add a class to the notification icon for visual indication
    notificationDropdown.classList.add("notification-active",'shake');


    // Xóa class 'shake' sau 0.5 giây để dừng hiệu ứng rung
    setTimeout(function() {
        notificationDropdown.classList.remove('shake','notification-active');
        showNotificationDot();
    }, 500);
}
// Gán hàm shakeBell vào sự kiện click của biểu tượng chuông
notificationDropdown.addEventListener('click',function() {
    hideNotificationDot();

});

// Hàm này sẽ hiển thị chấm đỏ khi có thông báo mới
function showNotificationDot() {
    document.getElementById('notificationDot').style.display = 'block';
}

// Hàm này sẽ ẩn chấm đỏ khi người dùng xem thông báo
function hideNotificationDot() {
    document.getElementById('notificationDot').style.display = 'none';
}

