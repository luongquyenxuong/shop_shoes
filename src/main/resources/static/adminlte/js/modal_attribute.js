// JavaScript code
document.getElementById("showInputBtn").addEventListener("click", function () {


    let inputContainer = document.getElementById("inputContainer");

    let inputGroup = document.createElement("div");
    inputGroup.className = "input-group";
    inputGroup.name = "new_attribute";

    let attributeNameSelect = document.createElement("select");
    let optionAttribute = document.createElement("option");
    optionAttribute.text = "Vui lòng chọn thuộc tính"
    optionAttribute.value = ""
    attributeNameSelect.className = "form-control";
    attributeNameSelect.id = "name-attribute";
    attributeNameSelect.name = "name-attribute";
    attributeNameSelect.appendChild(optionAttribute);


    let attributeValueSelect = document.createElement("select");
    let optionAttributeValue = document.createElement("option");
    optionAttributeValue.text = "Giá trị"
    optionAttributeValue.value = "";
    attributeValueSelect.className = "form-control";
    attributeValueSelect.id = "value-attribute"
    attributeValueSelect.name = "value-attribute"
    attributeValueSelect.appendChild(optionAttributeValue)

    for (let i = 0; i < attributes.length; i++) {
        let optionType = document.createElement("option");
        optionType.text = attributes[i].attributeType;
        optionType.value = attributes[i].attributeType;

        attributeNameSelect.appendChild(optionType);
    }
    attributeNameSelect.addEventListener("change", function () {
        attributeValueSelect.innerHTML = "";
        let selectedAttribute = attributeNameSelect.value;
        let attribute = attributes.find(attr => attr.attributeType === selectedAttribute);
        if (attribute) {
            let values = attribute.attributeValues;
            for (let i = 0; i < values.length; i++) {
                let optionValues = document.createElement("option");
                optionValues.text = values[i];
                optionValues.value = values[i];

                attributeValueSelect.appendChild(optionValues);
            }
        } else {
            optionAttributeValue.text = "Giá trị";
            optionAttributeValue.value = "";
            attributeValueSelect.appendChild(optionAttributeValue);
        }

    })

    inputGroup.appendChild(attributeNameSelect);
    inputGroup.appendChild(attributeValueSelect);

    inputContainer.appendChild(inputGroup);
});

// JavaScript code for deleting the last added input group
document.getElementById("deleteInputBtn").addEventListener("click", function () {
    let inputContainer = document.getElementById("inputContainer");
    let inputGroups = inputContainer.getElementsByClassName("input-group");

    if (inputGroups.length > 0) {
        inputContainer.removeChild(inputGroups[inputGroups.length - 1]);
    }
});

$(document).ready(function () {
    $("#close").click(function (e) {
        let inputContainer = document.getElementById("inputContainer");
        inputContainer.innerHTML = '';
        // Ẩn modal khi nút "Hủy" được nhấn
        $('#choose-attribute-modal').modal('hide');
    });
});


// Sự kiện khi modal attribute hủy
$(document).ready(function () {
    $("#cancelButton").click(function (e) {
        let inputContainer = document.getElementById("inputContainer");
        inputContainer.innerHTML = '';
        // Ẩn modal khi nút "Hủy" được nhấn
        $('#choose-attribute-modal').modal('hide');
    });
});


// Sự kiện khi modal attribute được xác nhận
$(document).ready(function () {
    $("#addButton").click(function (e) {
        // Lấy ra tất cả các input-group trong #inputContainer
        let inputGroups = document.querySelectorAll('#inputContainer .input-group');

        // Lặp qua từng input-group và kiểm tra giá trị của các input
        for (let i = 0; i < inputGroups.length; i++) {
            let nameSelect = inputGroups[i].querySelector("#name-attribute");
            let valueSelect = inputGroups[i].querySelector("#value-attribute");

            // Kiểm tra xem cả hai input đều có giá trị
            if (nameSelect.value && valueSelect.value ) {

            } else {
                // Nếu không có giá trị, xoá input-group đó
                inputGroups[i].remove();
            }
        }
        $('#choose-attribute-modal').modal('hide');
    });
});