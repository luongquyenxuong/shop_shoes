// JavaScript code
console.log(attributes)
console.log(attributesProduct)
loadAttributesProduct();

function loadAttributesProduct() {
    let inputUpdateContainer = document.getElementById("inputUpdateContainer");

    attributesProduct.forEach(function (option) {
        createSelectAttribute(inputUpdateContainer, option)
    })

}

document.getElementById("showInputUpdateBtn").addEventListener("click", function () {
    let inputUpdateContainer = document.getElementById("inputUpdateContainer");
    createSelectAttribute(inputUpdateContainer)
});


function createSelectAttribute(inputUpdateContainer, option) {
    let inputGroup = document.createElement("div");
    inputGroup.className = "input-group";

    let attributeNameUpdateSelect = document.createElement("select");
    let optionAttributeUpdate = document.createElement("option");
    optionAttributeUpdate.text = "Vui lòng chọn thuộc tính"
    optionAttributeUpdate.value = ""
    attributeNameUpdateSelect.className = "form-control";
    attributeNameUpdateSelect.id = "name-update-attribute";
    attributeNameUpdateSelect.name = "name-update-attribute";
    attributeNameUpdateSelect.appendChild(optionAttributeUpdate);

    let attributeValueUpdateSelect = document.createElement("select");
    let optionAttributeValueUpdate = document.createElement("option");
    optionAttributeValueUpdate.text = "Giá trị"
    optionAttributeValueUpdate.value = "";
    attributeValueUpdateSelect.className = "form-control";
    attributeValueUpdateSelect.id = "value-update-attribute"
    attributeValueUpdateSelect.name = "value-update-attribute"
    attributeValueUpdateSelect.appendChild(optionAttributeValueUpdate);

    generateListAttribute(attributeNameUpdateSelect, attributeValueUpdateSelect, optionAttributeValueUpdate, option)

    if (option) {
        attributeNameUpdateSelect.value = option.name;
        let changeEvent = new Event('change');
        // Gọi phương thức dispatchEvent để kích hoạt sự kiện change
        attributeNameUpdateSelect.dispatchEvent(changeEvent);
        attributeValueUpdateSelect.value = option.value;
    }

    inputGroup.appendChild(attributeNameUpdateSelect);
    inputGroup.appendChild(attributeValueUpdateSelect);

    inputUpdateContainer.appendChild(inputGroup);
}


function generateListAttribute(attributeNameUpdateSelect, attributeValueUpdateSelect, optionAttributeValueUpdate) {

    for (let i = 0; i < attributes.length; i++) {
        let optionType = document.createElement("option");
        optionType.text = attributes[i].attributeType.split('_').join(' ');
        optionType.value = attributes[i].attributeType;

        attributeNameUpdateSelect.appendChild(optionType);

    }
    attributeNameUpdateSelect.addEventListener("change", function () {
        attributeValueUpdateSelect.innerHTML = "";
        let selectedAttributeUpdate = attributeNameUpdateSelect.value;
        let attributeUpdate = attributes.find(attr => attr.attributeType === selectedAttributeUpdate);
        if (attributeUpdate) {
            let values = attributeUpdate.attributeValues;
            for (let i = 0; i < values.length; i++) {
                let optionValues = document.createElement("option");
                optionValues.text = values[i];
                optionValues.value = values[i];
                attributeValueUpdateSelect.appendChild(optionValues);
            }
        } else {
            optionAttributeValueUpdate.text = "Giá trị";
            optionAttributeValueUpdate.value = "";
            attributeValueUpdateSelect.appendChild(optionAttributeValueUpdate);
        }
    })
}


// JavaScript code for deleting the last added input group
document.getElementById("deleteInputUpdateBtn").addEventListener("click", function () {
    let inputUpdateContainer = document.getElementById("inputUpdateContainer");
    let inputGroups = inputUpdateContainer.getElementsByClassName("input-group");

    if (inputGroups.length > 0) {
        inputUpdateContainer.removeChild(inputGroups[inputGroups.length - 1]);
    }
});


// Hàm kiểm tra và xóa các input-group không hợp lệ
function validateAndRemoveInvalidInputGroups(containerSelector) {
    let inputGroups = document.querySelectorAll(containerSelector + ' .input-group');
    inputGroups.forEach(function (inputGroup) {
        let nameSelect = inputGroup.querySelector('select[name="name-update-attribute"]');
        let valueSelect = inputGroup.querySelector('select[name="value-update-attribute"]');

        // Kiểm tra xem cả hai input đều có giá trị
        if (!nameSelect.value || !valueSelect.value) {
            // Nếu không có giá trị, xoá input-group đó
            inputGroup.remove();
        }
    });
}

$(document).ready(function () {
    $("#close, #addUpdateButton").click(function () {
        // Gọi hàm kiểm tra và xóa các input-group không hợp lệ
        validateAndRemoveInvalidInputGroups('#inputUpdateContainer');

        // Ẩn modal khi nút được nhấn (cả close, cancel và add button)
        $('#choose-attribute-modal').modal('hide');
    });
});

