var user = window.localStorage['currentUser'];

function getUserName() {
}

document.addEventListener('DOMContentLoaded', () => { 
	userName.textContent = user.fullname;
}) 

function map() {
	var mapOptions = {
		center: new google.maps.LatLng(50.01502774, 36.22808368),
		zoom: 18,
		mapTypeId: google.maps.MapTypeId.ROADMAP,
		disableDefaultUI: true,
		clickableIcons: false,
		disableDoubleClickZoom: true,
		draggable: false,
		draggableCursor: "default"
	}
	var map = new google.maps.Map(document.getElementById("map"), mapOptions);
}