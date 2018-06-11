const socket = io('http://localhost:3306');
var user;

document.addEventListener('DOMContentLoaded', () => { 
	var email = window.location.href.split("?")[1].split("=")[1];
	socket.emit('userGetInfo', {mail:email});
});

function SetData(){
	document.getElementById('')('fullname').value = user.fullname;
	document.getElementById('email').value = user.email;
	document.getElementById('phone').value = user.phone;
	document.getElementById('group').value = user.group;
}

function EditProfile(){
	var fullname = CheckData('fullname');
	var email = CheckData('email', 10);
	var phone = CheckData('phone', 10);
	var group = CheckData('group', 4);

	if (fullname && email && phone && group){
		user.fullname = fullname;
		user.email = email;
		user.phone = phone;
		user.group = group;

		socket.emit('userEditProfile', {mail:email, fullname:fullname, phone:phone, group:group});
	}
}

function EditStatus() {
	if (false) {
		alert('Request has been sent');
		socket.emit('userEditStatus', {mail:user.email, fullname:user.fullname, group:user.group})
	}
	else{
		var result = confirm('Are you sure?');
		if(result && user != null) {
			user.status = "teacher";
			document.getElementById('status').textContent = user.status;
		}
	}
}

function CheckData(id, minLenght) {
	minLenght = minLenght ? minLenght : 6;

	if (IsEmpty(id, minLenght)){
		document.getElementById(id).style.boxShadow = "0 0 20px red";
		return false;
	}
	else{
		document.getElementById(id).style.boxShadow = "none";
		return document.getElementById(id).value;
	}
}

function IsEmpty(id, minLenght){
	var value = document.getElementById(id).value;
	if(value == undefined || value == "" | value.length < minLenght)
		return true;
	else
		return false;
}

function ToMap(){
	if(user == null)
		document.location.href = "map.html";
	else
		document.location.href = "map.html?mail=" + user.email;
}

function ToChats(){
	if(user == null)
		document.location.href = "chats.html";
	else
		document.location.href = "chats.html?mail=" + user.email;
}

function ToEvents(){
	if(user == null)
		document.location.href = "events.html";
	else
		document.location.href = "events.html?mail=" + user.email;
}

function ToProfile(){
	if(user == null)
		document.location.href = "profile.html";
	else
		document.location.href = "profile.html?mail=" + user.email;
}

socket.on('userGetInfo', function (data){
	if(data.err == 0){
		user = new User(email, data.fullname, data.group, data.phone, data.status);
		SetData();
	}
	else
		alert("Error");
});