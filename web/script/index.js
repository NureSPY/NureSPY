function clickArrowLeft() {
	var currentSrc = document.images["info"].src;
	var index = parseInt(currentSrc.charAt(currentSrc.length - 5));
	document.images["info"].src = "images/index/info" + (index == 1 ? 3 : --index) + ".png";
}

function clickArrowRight() {
	var currentSrc = document.images["info"].src;
	var index = parseInt(currentSrc.charAt(currentSrc.length - 5));
	document.images["info"].src = "images/index/info" + (index == 3 ? 1 : ++index) + ".png";
}