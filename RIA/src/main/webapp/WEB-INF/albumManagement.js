{
	let pageOrchestrator = new PageOrchestrator(); // main controller

	window.addEventListener("load", () => {
		if (false && sessionStorage.getItem("username") == null) {
			window.location.href = "../index.html";
		} else {
			pageOrchestrator.start(); // initializes components
			pageOrchestrator.refresh();
		}
	})

	function PageOrchestrator() {
		var alertContainer = document.getElementById("id_alert");

		this.start = function () {
			document.getElementById("logoutbutton").addEventListener('click', (e) => {
				var form = e.target.closest("form");
				if (form.checkValidity()) {
					makeCall("POST", 'Logout', e.target.closest("form"),
						(x) => {
							if (x.readyState == XMLHttpRequest.DONE) {
								window.sessionStorage.removeItem('username');
							}
						}
					)
				}
			})
		};
		this.refresh = function () {
		};
	}
};