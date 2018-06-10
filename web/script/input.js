const socket = io('http://localhost:3306');
var email;

function SignUp() {
	var name = CheckData("name", 6);
	email = CheckData("email", 10);
	var group = CheckData("group", 4);
	var phone = CheckData("phone", 10);
	var password;

	if(CheckData("password", 6) & CheckData("repeat", 6))
		password = ComparePassword();
	else
		password = false;

	if(!password){
		document.getElementById("password").value = "";
		document.getElementById("repeat").value = "";
	}
	else
		password = document.getElementById("password").value;

	if(name && email && group && phone && password){
		socket.emit('signUp', { fullname:name, mail:email, phone:phone, group:group, password:password });
	}
	else
		alert("error");
}

function SignIn() {
	email = CheckData("email");
	var password = CheckData("password");

	if(!password)
		document.getElementById("password").value = "";
	
	if(email && password){
		socket.emit('signIn', { mail:email, password:password });
	}
}

function Restore() {
	if(CheckData("email", 10)){
		alert("Message with password sent!");
		socket.emit('restorePassword', {mail:email});
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

	socket.on('signIn',function (data){
		if(data.mail != -1){
			document.location.href = "mao.html?mail=" + email;
		}
		else
			alert("Error");
	});

	socket.on('signUp',function (data){
		if(data.err == 0){
			document.location.href = "mao.html?mail=" + email;
		}
		else
			alert("Error");
	});