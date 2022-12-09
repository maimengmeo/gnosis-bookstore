console.log(document.getElementById("isAdmin"));

function verify(event) {
	var isFormValid = true;
	var form = document.forms['form'];
	console.log(form);
	var password = document.forms['form']['password'].value;
	var userName = document.forms['form']['username'].value;
	
	if (password == null || password == "" ||
		userName == null || userName == "") {
			
			document.getElementById("error").innerHTML = "User name and password are required";
			isFormValid = false
		}
		
	var checkboxes = document.getElementsByName("authorities");
	var isAdmin = document.getElementById("isAdmin"); 
	var isCheckboxValid = !isAdmin;

	console.log("admin", isAdmin);
	if(isAdmin) {	
		for (var i = 0; i< checkboxes.length; i++) {
			if (checkboxes[i].checked) {
				isCheckboxValid = true;
				break;
			}
			
			document.getElementById("error").innerHTML = "You must select at least one role";
		}
	}
	console.log("checkboxes", isCheckboxValid)
	var isValid = isFormValid && isCheckboxValid;
	console.log("valid", isValid);
	if(!isValid) {
		event.preventDefault();
	}

 	return isValid;
}