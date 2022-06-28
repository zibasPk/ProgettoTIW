
function makeCall(method, url, formElement, cback, reset = true) {
	var req = new XMLHttpRequest(); // visible by closure
	req.onreadystatechange = function () {
		cback(req)
	}; // closure
	req.open(method, url);
	if (formElement == null) {
		req.send();
	} else {
		req.send(new FormData(formElement));
	}
	if (formElement !== null && reset === true) {
		formElement.reset();
	}
}

function sendSaveOrder(orderedImages, cback) {
	var req = new XMLHttpRequest();
	req.onreadystatechange = () => {
		cback(req);
	}
	req.open("POST", "SaveAlbumOrder");
	if (orderedImages == null) {
		req.send();
	} else {
		req.setRequestHeader("Content-Type", "application/json");
		req.send(JSON.stringify(orderedImages));
	}
}


function checkEmailField(email) {

	if (/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(email)) {
		return (true);
	}
	alert("You have entered an invalid email address!")
	return (false);
}



function checkPasswordsMatch(password1, password2) {
	if (password1 == password2) {
		return (true);
	}
	alert("passwords don't match");
	return (false);
}