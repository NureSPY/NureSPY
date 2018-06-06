const images = [...document.getElementById("slider").children];
let index = 0;

function clickArrowLeft() {
	index = index == 0 ? 2 : --index;
	rerenderSlider();
}

function clickArrowRight() {
	index = index == 2 ? 0 : ++index;
	rerenderSlider();
}

function rerenderSlider() {
	images.forEach(image => image.style.opacity = 0);
	images[index].style.opacity = 1;
}