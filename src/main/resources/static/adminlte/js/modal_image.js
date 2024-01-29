let options = {
    valueNames: [
        {name: 'grid-item-img', attr: 'src'}
    ],
    item: '<div class="grid-item" onclick="chooseImg(this)"><div class="img-container"><div class="img-div"><img class="grid-item-img" src="" alt=""></div></div></div>',
    pagination: true,
    page: 10
};

let values = [];
let imgList = new List('list-user-img', options);

function initListImg(arr) {
    values = arr;
    imgList.add(values);
}

let chosenImages = [];

function closeChooseImgModal() {
    $('#choose-img-modal').modal('hide');
    $('#list-user-img .grid-item.choosen').removeClass('choosen');
    chosenImages = [];
    $('.btn-delete-img').prop('disabled', true);
    $('.btn-choose-img').prop('disabled', true);
}


function chooseImg(img) {

    // Thêm hình ảnh vào danh sách hình ảnh được chọn
    let imageElement = img.querySelector('.grid-item-img');
    if ($(img).hasClass('choosen')) {
        $(img).removeClass('choosen');
        // Loại bỏ hình ảnh khỏi danh sách hình ảnh được chọn
        let index = chosenImages.indexOf(imageElement.src);
        if (index !== -1) {
            chosenImages.splice(index, 1);
        }
        if (chosenImages.length === 0) {
            $('.btn-delete-img').prop('disabled', true);
            $('.btn-choose-img').prop('disabled', true);
        }

    } else {
        $(img).addClass('choosen');
        // Lấy đường dẫn của hình ảnh (src)
        let imageUrl = imageElement.src;
        chosenImages.push(imageUrl);
        $('.btn-delete-img').prop('disabled', false);
        $('.btn-choose-img').prop('disabled', false);
    }

    // if ($(img).hasClass('choosen')) {
    //     $(img).toggleClass('choosen');
    //     $('.btn-delete-img').prop('disabled', true);
    //     $('.btn-choose-img').prop('disabled', true);
    // } else {
    //     $('.grid-item.choosen').toggleClass('choosen');
    //     $(img).toggleClass('choosen');
    //     $('.btn-delete-img').prop('disabled', false);
    //     $('.btn-choose-img').prop('disabled', false);
    // }
}

$("#upload-thumbnail").change(function () {
    let fd = new FormData();
    let file = $(this)[0].files[0];

    let fileName = file.name;
    let extension = fileName.substr((fileName.lastIndexOf('.') + 1));
    if (extension !== "jpg" && extension !== "png" && extension !== "svg" && extension !== "jpeg" && extension !== "gif") {
        $(this).val('');
        toastr.warning("Chỉ hỗ trợ các định dạng ảnh: jpg, png, svg, jpeg");
        return;
    }
    if (file.size > 10000000) {
        toastr.warning("Chỉ hỗ trợ file ảnh có kích thước lớn nhất 10MB");
        $(this).val('');
        return;
    }
    fd.append('file', file);

    $.ajax({
        url: '/api/upload/files',
        type: 'post',
        data: fd,
        contentType: false,
        processData: false,
        success: function (data) {
            values.unshift({"grid-item-img": data})
            imgList.clear();
            imgList.add(values);
        },
        error: function (data) {
            toastr.warning(data.responseJSON.message);
        }
    });
});

$('.btn-delete-img').click(function () {
    let url = $('.grid-item.choosen .grid-item-img').attr('src');
    if (url === "" || url == null) {
        toastr.warning("Vui lòng chọn ảnh cần xóa");
        return;
    }
    // Confirm
    let click = confirm("Bạn chắc chắn muốn xóa ảnh này?");
    if (click === true) {
        // Send api delete

        let fileNames = chosenImages.map(function (imageUrl) {
            // Sử dụng hàm split để tách chuỗi dựa trên dấu '/'
            let parts = imageUrl.split('/');
            // Lấy phần tử cuối cùng trong mảng parts, chứa tên file
            let fileNameWithExtension = parts[parts.length - 1];
            // Sử dụng hàm split lại để tách tên file từ tên file kèm extension
             // Loại bỏ các tham số truy vấn nếu có
            return fileNameWithExtension.split('?')[0];
        });
        let myJSON = JSON.stringify(fileNames);
        $.ajax({
            url: '/api/delete-image',
            type: 'DELETE',
            contentType: "application/json; charset=utf-8",
            data: myJSON,
            success: function () {
                // Remove from list
                for (let i = 0; i < chosenImages.length; i++) {

                    let newUrl = chosenImages[i].replace("http://localhost:8080", "");

                    let index = -1;
                    for (let j = 0; j < values.length; j++) {
                        if (JSON.stringify(values[j]) === JSON.stringify({"grid-item-img": newUrl})) {
                            index = j;
                            break;
                        }
                    }
                    if (index !== -1) {
                        values.splice(index, 1);
                    }
                    console.log(newUrl)
                    console.log(url)
                    imgList.remove('grid-item-img', newUrl);
                }
                chosenImages = [];
            },
            error: function (data) {
                toastr.warning(data.responseJSON.message);
            }
        });
    }
})