function clickArrowLeft() {
	var currentSrc = document.images["slides"].src;
	var index = parseInt(currentSrc.charAt(currentSrc.length - 5));
	document.images["slides"].src = "images/index/slide" + (index == 1 ? 3 : --index) + ".png";
}

function clickArrowRight() {
	var currentSrc = document.images["slides"].src;
	var index = parseInt(currentSrc.charAt(currentSrc.length - 5));
	document.images["slides"].src = "images/index/slide" + (index == 3 ? 1 : ++index) + ".png";
}