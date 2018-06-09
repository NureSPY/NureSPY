const socket = io('http://localhost:3306');

function SignUp() {
	var name = CheckData("name", 6);
	var email = CheckData("email", 10);
	var group = CheckData("group", 4);
	var password;

	if(CheckData("password", 6) & CheckData("repeat", 6))
		password = ComparePassword();
	else
		password = false;

	if(!password){
		document.getElementById("password").value = "";
		document.getElementById("repeat").value = "";
	}

	if(name && email && group && password){
		socket.emit('signUp', { name:name, email:email, group:group, password:password });
		if(!socket.error)
			window.location.href = "map.html";
		else
			alert("Error!");	
	}
}

function SignIn() {
	var email = CheckData("email");
	var password = CheckData("password");

	if(!password)
		document.getElementById("password").value = "";
	
	if(email && password){
		socket.emit('signIn', {email:email, password:password});
		if(!socket.error)
			window.location.href = "map.html";
		else
			alert("User is not found!");
	}
}

function Restore() {
	if(CheckData("email", 10)){
		alert("Message with password sent!");
		socket.emit('restorePassword', {email:email});
		window.location.href = "signIn.html";
	}
}

function IsEmpty(elementId, minLenght){
	var value = document.getElementById(elementId).value;
	if(value == "" | value.length < minLenght)
		return true;
	else
		return false;
}

function CheckData(elementId, minLenght) {
	minLenght = minLenght ? minLenght : 6;

	if(IsEmpty(elementId, minLenght)){
		document.getElementById(elementId).style.boxShadow = "0 0 20px red";
		return false;
	}
	else{
		document.getElementById(elementId).style.boxShadow = "none";
		return document.getElementById(elementId).value;
	}
}

function ComparePassword() {
	if(document.getElementById("password").value != document.getElementById("repeat").value){
		document.getElementById("password").style.borderColor = "red";
		document.getElementById("repeat").style.borderColor = "red";
		return false;
	}
	else{
		document.getElementById("password").style.borderColor = "black";
		document.getElementById("repeat").style.borderColor = "black";
		return true;
	}
}