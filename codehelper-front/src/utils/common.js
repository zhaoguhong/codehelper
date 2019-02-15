var StringUtils = {
	isBlank : function(str) {
		return !str || str.trim().length == 0;
	},
	isNotBlank : function(str) {
		return str && str.trim().length != 0;
	}
};

// 判断字符串是否是json
function isJSON(str) {
	if (typeof str == 'string' && (str.includes("{") || str.includes("["))) {
		try {
			var obj = JSON.parse(str);
			return true;
		} catch (e) {
		}
	}
	return false;
}

//判断对象是否是数组
function isArray(str) {
	return str instanceof Array;
}

//判断对象是否是字符串
function isString(obj) { 
	return Object.prototype.toString.call(obj) === "[object String]";
}

var ArrayUtils = {
	// 判断数组是否有重复
	isRepeat : function(arr) {
		var hash = {};
		for ( var i in arr) {
			if (hash[arr[i]])
				return true;
			hash[arr[i]] = true;
		}
		return false;
	}
}

// 获取一个字符串中一个单词的下一个单词
var getNextWord = function(str, word) {
	str = str.substring(str.indexOf(word) + word.length).trim();
	var nextWord = "";
	for (var i = 0; i < str.length; i++) {
		if (str.charAt(i) == " " || str.charAt(i) == ";") {
			break;
		}
		nextWord += str.charAt(i);
	}
	return nextWord;
}

export { StringUtils ,ArrayUtils }