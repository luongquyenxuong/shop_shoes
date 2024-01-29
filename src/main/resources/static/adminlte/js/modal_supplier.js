// Lắng nghe sự kiện hide.bs.modal của Bootstrap Modal
$('#modal-add-new-supplier').on('hide.bs.modal', function () {
    // Thiết lập giá trị của các trường input về chuỗi rỗng
    $('#new_name_supplier').val('');
    $('#new_address_supplier').val('');
    $('#new_phone_supplier').val('');
    $('#new_contact_person_supplier').val('');
    $('#new_email_supplier').val('');
    $('#new_note_supplier').val('');
    $('#new_tax_supplier').val('');
    $('#new_website_supplier').val('');


});
$("#formSupplier").submit(function (e) {
    e.preventDefault();
}).validate({
    rules: {
        new_name_supplier: {
            required: true,
            maxlength: 50
        },
        new_address_supplier: {
            required: true,
            maxlength: 50
        },
        new_phone_supplier: {
            required: true,
            number: true,
            rangelength: [10, 10],
        },
        new_contact_person_supplier: {
            required: true,
            maxlength: 50
        },
        new_email_supplier: {
            required: true,
            email: true,
            maxlength: 50
        },
        new_tax_supplier: {
            number: true,
            rangelength: [10, 10],
        },
        new_website_supplier: {
            url: true,
        },

    },
    messages: {
        new_name_supplier: {
            required: "Vui lòng nhập tên nhà cung cấp!",
            maxlength: "Tên danh mục có độ dài tối đa 50 ký tự!",
        },
        new_address_supplier: {
            required: "Vui lòng nhập địa chỉ nhà cung cấp!",
            maxlength: "Tên danh mục có độ dài tối đa 50 ký tự!",
        },
        new_phone_supplier: {
            required: "Vui lòng nhập số điện thoại nhà cung cấp!",
            number: "Vui lòng nhập số điện thoại đúng đinh dạng!",
            rangeLength: "Số điện thoại gồm có 10 số!",
        },
        new_contact_person_supplier: {
            required: "Vui lòng nhập tên nguời đại diện nhà cung cấp!",
            maxlength: "Tên danh mục có độ dài tối đa 50 ký tự!",
        },
        new_email_supplier: {
            required: "Vui lòng nhập email nhà cung cấp!",
            email: "Vui lòng nhập đúng định dạng email",
            maxlength: "Tên danh mục có độ dài tối đa 50 ký tự!",
        },
        new_tax_supplier: {
            required: "Vui lòng nhập tên nhà cung cấp!",
            number: "Vui lòng nhập mã số thuế đúng đinh dạng!",
            rangelength: "Mã số thuế gồm có 10 số!",
        },
        new_website_supplier: {
            url: "Vui lòng nhập đúng url nhà cung cấp!",
        },

    },
    submitHandler: function () {


        let nameSupplier = document.getElementById('new_name_supplier').value;
        let addressSupplier = document.getElementById('new_address_supplier').value;
        let phoneSupplier = document.getElementById('new_phone_supplier').value;
        let contactPersonSupplier = document.getElementById('new_contact_person_supplier').value;
        let emailSupplier = document.getElementById('new_email_supplier').value;
        let noteSupplier = document.getElementById('new_note_supplier').value;
        let taxSupplier = document.getElementById('new_tax_supplier').value;
        let websiteSupplier = document.getElementById('new_website_supplier').value;


        let req = {
            name: nameSupplier,
            address: addressSupplier,
            phoneNumber: phoneSupplier,
            contactPerson: contactPersonSupplier,
            email: emailSupplier,
            note: noteSupplier,
            taxNumber: taxSupplier,
            website: websiteSupplier,
            status: true
        }
        let myJSON = JSON.stringify(req);
        $.ajax({
            url: '/api/admin/create-supplier',
            type: 'POST',
            data: myJSON,
            contentType: "application/json; charset=utf-8",
            success: function () {
                toastr.success("Tạo nhà cung cấp thành công");
                $('.modal').modal('hide');
                setTimeout(() => location.reload(), 500);
            },
            error: function (data) {
                toastr.warning(data.responseJSON.message);
            }
        });


    }
})
