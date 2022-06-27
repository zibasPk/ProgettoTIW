{
	let albums, pageOrchestrator = new PageOrchestrator(); // main controller

	window.addEventListener("load", () => {
		if (sessionStorage.getItem("username") == null) {
			window.location.href = "index.html";
		} else {
			pageOrchestrator.start(); // initializes components
			pageOrchestrator.refresh();
		}
	})

	function AlbumLists(options) {
		this.alert = options['alert'];
		this.noAlbumAlert = options['noAlbumAlert']
		this.myListContainer = options['myListContainer'];
		this.myContainerBody = options['myContainerBody'];
		this.otherListContainer = options['otherListContainer'];
		this.otherContainerBody = options['otherContainerBody'];

		this.reset = () => {
			//TODO: reset resets to original order maybe
		}

		this.show = (next) => {
			makeCall("GET", "GetHomeData", null,
				(req) => {
					if (req.readyState == XMLHttpRequest.DONE) {
						var message = req.responseText;
						switch (req.status) {
							case 200:
								var json = JSON.parse(req.responseText);
								var myAlbumsToShow = json[0];
								var otherAlbumsToShow = json[1];
								if (myAlbumsToShow.length == 0) {
									this.noAlbumAlert.textContent = "No Albums to your name"
									return;
								}
								this.noAlbumAlert.textContent = "";
								this.updateMyAlbums(myAlbumsToShow);
								this.updateOtherAlbums(otherAlbumsToShow);
								break;
							case 502:
								this.alert.textContent = message;
								break;
						}
					}
				});
		}

		this.updateMyAlbums = (myAlbums) => {
			var elem, i, row, nameCell, dateCell, linkCell, anchor, linkText;
			this.myContainerBody.innerHTML = ""; // empties the table body
			myAlbums.forEach((album) => {
				row = document.createElement("tr");
				nameCell = document.createElement("td");
				nameCell.textContent = album.title;
				row.appendChild(nameCell);
				dateCell = document.createElement("td");
				dateCell.textContent = new Date(album.creationDate).toLocaleDateString();
				row.appendChild(dateCell);
				linkCell = document.createElement("td");
				anchor = document.createElement("a");
				linkCell.appendChild(anchor);
				linkText = document.createTextNode("Open");
				anchor.appendChild(linkText);
				anchor.setAttribute('albumid', album.id);
				anchor.addEventListener("click", (e) => {
					//todo show album details
				}, false);
				anchor.href = "#";
				row.appendChild(linkCell);
				this.myContainerBody.appendChild(row);
			});
			this.myListContainer.style.visibility = "visible";

		}

		this.updateOtherAlbums = (otherAlbums) => {
			var elem, i, row, nameCell, dateCell, linkCell, anchor, linkText;
			this.otherContainerBody.innerHTML = ""; // empties the table body
			otherAlbums.forEach((album) => {
				row = document.createElement("tr");
				nameCell = document.createElement("td");
				nameCell.textContent = album.title;
				row.appendChild(nameCell);
				dateCell = document.createElement("td");
				dateCell.textContent = new Date(album.creationDate).toLocaleDateString();
				row.appendChild(dateCell);
				linkCell = document.createElement("td");
				anchor = document.createElement("a");
				linkCell.appendChild(anchor);
				linkText = document.createTextNode("Open");
				anchor.appendChild(linkText);
				anchor.setAttribute('albumid', album.id);
				anchor.addEventListener("click", (e) => {
					//todo show album details
				}, false);
				anchor.href = "#";
				row.appendChild(linkCell);
				this.otherContainerBody.appendChild(row);
			});
			this.otherListContainer.style.visibility = "visible";
		}

		this.reset = () => {
			this.myListContainer.style.visibility = "hidden";
			this.otherListContainer.style.visibility = "hidden";
		}
	}

	function PageOrchestrator() {
		var alertContainer;

		this.start = function () {
			alertContainer = document.getElementById("id_alert");
			albums = new AlbumLists({
				alert: alertContainer,
				noAlbumAlert: document.getElementById("noalbumalert"),
				myListContainer: document.getElementById("id_myalbumslist"),
				myContainerBody: document.getElementById("id_myalbumsbody"),
				otherListContainer: document.getElementById("id_otheralbumslist"),
				otherContainerBody: document.getElementById("id_otheralbumsbody"),

			});
			document.getElementById("logoutbutton").addEventListener('click', (e) => {
				window.sessionStorage.removeItem('username');
				makeCall("POST", 'Logout', null, (x) => {});
				window.location.href = "index.html";
			})
		};
		this.refresh = function () {
			alertContainer.textContent = "";
			albums.reset();
			albums.show();
		};
	}
};