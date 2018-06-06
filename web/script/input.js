function SignUp() {
	var name = CheckData("name", 6);
	var login = CheckData("login", 6);
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

	if(name && login && email && group && password)
		alert("Sign Up!");
}

function SignIn() {
	var login = CheckData("login");
	var password = CheckData("password");

	if(!password)
		document.getElementById("password").value = "";
	
	if(login && password)
		alert("Sign In!");
}

function Restore() {
	if(CheckData("email", 10))
		alert("Message with password sent!");
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
		document.getElementById(elementId).style.borderColor = "red";
		return false;
	}
	else{
		document.getElementById(elementId).style.borderColor = "black";
		return true;
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