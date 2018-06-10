const socket = io('http://localhost:3306');
var user;

document.addEventListener('DOMContentLoaded', () => { 
	var email = window.location.href.split("?")[1].split("=")[1];
	socket.emit('userGetInfo', {mail:email});
});


socket.on('userGetInfo', function (data){
	if(data.err == 0){
		user = new User(email, data.fullname, data.group, data.phone, data.status);
		window.localeStorage['currentUser'] = user;
	}
	else
		alert("Error");
});

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