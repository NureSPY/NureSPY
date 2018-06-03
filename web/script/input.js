function IsEmpty(elementId, minLenght){
	var value = document.getElementById(elementId).value;
	if(value == "" | value.length < minLenght)
		return true;
	else
		return false;
}

function CheckData(elementId, minLenght) {
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