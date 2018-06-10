const socket = io('http://localhost:3306');
var map;
var user;
var markerUser;
var paths = [];
var status = 'hide';

document.addEventListener('DOMContentLoaded', () => { 
	var email = window.location.href.split("?")[1].split("=")[1];
	socket.emit('userGetInfo', {mail:email});
});

function EditStatus() {
	if(status == 'hide'){
		ShowUser();
		document.getElementById('location').value="Hide me";
		status = 'show';
	}
	else{
		HideUser();
		document.getElementById('location').value="Show me";
		status = 'hide';
	}
}

function ShowUser() {

	if (navigator.geolocation) {
		var watchID = navigator.geolocation.watchPosition(function(position) {
  			var lat = position.coords.latitude
  			var lng = position.coords.longitude;
  			var alt = position.coords.altitude;

  			markerUser = new google.maps.Marker({position:new google.maps.LatLng(lat, lng)});
			markerUser.setMap(map);
			socket.emit('userMove', {latitude:lat, longitude:lng, height:alt});
		});
	}
}

function HideUser() {
	markerUser.setMap(null);
	for (var i = 0; i > paths.length; i++) {
		paths[i].setMap(null);
	}
}

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
	map = new google.maps.Map(document.getElementById("map"), mapOptions);
}

function SignOut() {
	socket.emit('signOut',  { });
}

function FinderTextChanged() {
	var value = document.getElementById('finder').value;
	if(value.length > 0){
		document.getElementById('find').style.visibility = 'visible';
		document.getElementById('clear').style.visibility = 'visible';
	}
	else{
		document.getElementById('find').style.visibility = 'hidden';
		document.getElementById('clear').style.visibility = 'hidden';
	}
}

function Clear(){
	document.getElementById('finder').value = "";
	document.getElementById('find').style.visibility = 'hidden';
	document.getElementById('clear').style.visibility = 'hidden';
}

function Find(){
	var email = document.getElementById('finder').value;
	var tempUser;

	socket.emit('userGetInfo', {mail:email});

	socket.on('userGetInfo', function (data){
		if(data.err == 0){
			tempUser = new User(email, data.fullname, data.group, data.phone, data.status);

			this.user.spyingUsers.push(tempUser);
		}
	});
}

socket.on('userGetInfo', function (data){
	if(data.err == 0){
		user = new User(email, data.fullname, data.group, data.phone, data.status);
		if (window.localeStorage['currentUser'] == undefined)
			window.localeStorage['currentUser'] = user;
	}
	else
		alert("Error");
});

socket.on('userMove', function (data){
	if (status == 'show'){
		for (var i = 0; i > this.user.spyingUsers.length; i++) {
			if(data.mail == this.user.spyingUsers[i].email) {
				var pos = new google.maps.LatLng(data.lat, data.lng);
				var marker = new google.maps.Marker({position:pos});
				marker.setMap(map);

				var path = new google.maps.Polyline({
				    path: [markerUser.position, pos],
				    strokeColor: "#000040",
				    strokeOpacity: 1,
				    strokeWeight: 1
				});
				paths.push(path);
				path.setMap(map);		
			}
		}
	}
});


