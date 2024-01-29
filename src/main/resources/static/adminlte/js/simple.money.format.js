(function ($) {
	$.fn.simpleMoneyFormat = function() {
		this.each(function(index, el) {		
			let elType = null; // input or other
			let value = null;
			// get value
			if($(el).is('input') || $(el).is('textarea')){
				value = $(el).val().replace(/,/g, '');
				elType = 'input';
			} else {
				value = $(el).text().replace(/,/g, '');
				elType = 'other';
			}
			// if value changes
			$(el).on('paste keyup', function(){
				value = $(el).val().replace(/,/g, '');
				formatElement(el, elType, value); // format element
			});
			formatElement(el, elType, value); // format element
		});
		function formatElement(el, elType, value){
			let result = '';
			let valueArray = value.split('');
			let resultArray = [];
			let counter = 0;
			let temp = '';
			for (let i = valueArray.length - 1; i >= 0; i--) {
				temp += valueArray[i];
				counter++
				if(counter === 3){
					resultArray.push(temp);
					counter = 0;
					temp = '';
				}
			}
			if(counter > 0){
				resultArray.push(temp);				
			}
			for (let i = resultArray.length - 1; i >= 0; i--) {
				let resTemp = resultArray[i].split('');
				for (let j = resTemp.length - 1; j >= 0; j--) {
					result += resTemp[j];
				}
				if(i > 0){
					result += ','
				}
			}
			if(elType === 'input'){
				$(el).val(result);
			} else {
				$(el).empty().text(result);
			}
		}
	};
}(jQuery));