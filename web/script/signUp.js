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

