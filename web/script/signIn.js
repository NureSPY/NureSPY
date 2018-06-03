function SignIn(argument) {
	var login = CheckData("login");
	var password = CheckData("password");

	if(!password)
		document.getElementById("password").value = "";
	
	if(login && password)
		alert("Sign In!");
}