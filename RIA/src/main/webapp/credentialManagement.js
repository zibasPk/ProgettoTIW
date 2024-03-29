

(function login() { // avoid variables ending up in the global scope
	
	let email = document.getElementById("email");
	document.getElementById("loginbutton").addEventListener('click', (e) => {
		let form = e.target.closest("form");
		if (checkEmailField(email)) {
			if (form.checkValidity()) {
				makeCall("POST", 'CheckLogin', e.target.closest("form"),
					function(x) {
						if (x.readyState == XMLHttpRequest.DONE) {
							let message = x.responseText;
							switch (x.status) {
								case 200:
									sessionStorage.setItem('username', message);
									window.location.href = "home.html";
									break;
								case 400: // bad request
									document.getElementById("errormessage").textContent = message;
									break;
								case 401: // unauthorized
									document.getElementById("errormessage").textContent = message;
									break;
								case 502: // bad gate way
									document.getElementById("errormessage").textContent = message;
									break;
							}
						}
					}
				);

			} else {
				form.reportValidity();
			}
		}
	});

}

)();



(function registration() { // avoid variables ending up in the global scope

	let regemail = document.getElementById("registrationemail");
	let password1 = document.getElementById("password1");
	let password2 = document.getElementById("password2");

	document.getElementById("registrationbutton").addEventListener('click', (e) => {
		let form = e.target.closest("form");
		if (checkPasswordsMatch(password1, password2) && checkEmailField(regemail)) {
			if (form.checkValidity()) {
				makeCall("POST", 'CheckRegistration', e.target.closest("form"),
					function(x) {
						if (x.readyState == XMLHttpRequest.DONE) {
							let message = x.responseText;
							switch (x.status) {
								case 200:
									sessionStorage.setItem('username', message);
									window.location.href = "home.html";
									break;
								case 400: // bad request
									document.getElementById("errormessage").textContent = message;
									break;
								case 401: // unauthorized
									document.getElementById("errormessage").textContent = message;
									break;
								case 502: // bad gateway
									document.getElementById("errormessage").textContent = message;
									break;
							}
						}
					}
				);

			} else {
				form.reportValidity();
			}
		}
	});

})();




