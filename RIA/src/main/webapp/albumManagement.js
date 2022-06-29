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
		this.saveOrderButton = options['saveOrder'];
		this.noAlbumAlert = options['noAlbumAlert'];
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
								if (myAlbumsToShow.length != 0) {
									this.updateMyAlbums(myAlbumsToShow);
									this.noAlbumAlert.textContent = "";
								}
								this.noAlbumAlert.textContent = "No Albums to your name"
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
				row.draggable = true;
				row.addEventListener("dragstart", dragStart);
				row.addEventListener("dragover", dragOver);
				row.addEventListener("dragleave", dragLeave);
				row.addEventListener("drop", dragDrop);
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
				saveOrderButton: document.getElementById("id_saveorder"),
				noAlbumAlert: document.getElementById("noalbumalert"),
				myListContainer: document.getElementById("id_myalbumslist"),
				myContainerBody: document.getElementById("id_myalbumsbody"),
				otherListContainer: document.getElementById("id_otheralbumslist"),
				otherContainerBody: document.getElementById("id_otheralbumsbody"),

			});
			document.getElementById("logoutbutton").addEventListener('click', (e) => {
				window.sessionStorage.removeItem('username');
				makeCall("POST", 'Logout', null, (x) => { });
				window.location.href = "index.html";
			})
			document.getElementById("id_saveorder").addEventListener('click', (e) => {
				var albums = document.getElementById("id_myalbumsbody");
				var albumsList = new Array();
				for (let i = 0, row; row = albums.rows[i]; i++) {
					var col = row.cells[2];
					var anchor = col.getElementsByTagName("a")[0];
					albumsList.push(anchor.getAttribute("albumid"));
				}
				sendSaveOrder(albumsList, (req) => {
					if (req.readyState == XMLHttpRequest.DONE) {
						var message = req.responseText;
						switch (req.status) {
							case 200:
								break;
							case 502:
								this.alertContainer.textContent = message;
								break;
						}
					}
				})
			})
		};
		this.refresh = function () {
			alertContainer.textContent = "";
			albums.reset();
			albums.show();
		};
	}

	let startElement;
	/* 
				This fuction puts all row to "notselected" class, 
				then we use CSS to put "notselected" in black and "selected" in red
		*/
	function unselectRows(rowsArray) {
		for (var i = 0; i < rowsArray.length; i++) {
			rowsArray[i].classList.remove("selected");
		}
	}


	/* 
			The dragstart event is fired when the user starts 
			dragging an element (if it is draggable=True)
			https://developer.mozilla.org/en-US/docs/Web/API/Document/dragstart_event
	*/
	function dragStart(event) {
		/* we need to save in a variable the row that provoked the event
		 to then move it to the new position */
		startElement = event.target.closest("tr");
	}

	/*
			The dragover event is fired when an element 
			is being dragged over a valid drop target.
			https://developer.mozilla.org/es/docs/Web/API/Document/dragover_event
	*/
	function dragOver(event) {
		// We need to use prevent default, otherwise the drop event is not called
		event.preventDefault();

		// We need to select the row that triggered this event to marked as "selected" so it's clear for the user
		var dest = event.target.closest("tr");

		// Mark  the current element as "selected", then with CSS we will put it in red
		dest.classList.add("selected");
	}

	/*
			The dragleave event is fired when a dragged 
			element leaves a valid drop target.
			https://developer.mozilla.org/en-US/docs/Web/API/Document/dragleave_event
	*/
	function dragLeave(event) {
		// We need to select the row that triggered this event to marked as "notselected" so it's clear for the user 
		var dest = event.target.closest("tr");

		// Mark  the current element as "notselected", then with CSS we will put it in black
		dest.classList.remove("selected");
	}

	/*
			The drop event is fired when an element or text selection is dropped on a valid drop target.
			https://developer.mozilla.org/en-US/docs/Web/API/Document/drop_event
	*/
	function dragDrop(event) {

		// Obtain the row on which we're dropping the dragged element
		var dest = event.target.closest("tr");

		// Obtain the index of the row in the table to use it as reference 
		// for changing the dragged element possition
		var table = dest.closest('table');
		var rowsArray = Array.from(table.querySelectorAll('tbody > tr'));
		var indexDest = rowsArray.indexOf(dest);

		// Move the dragged element to the new position
		if (rowsArray.indexOf(startElement) < indexDest)
			// If we're moving down, then we insert the element after our reference (indexDest)
			startElement.parentElement.insertBefore(startElement, rowsArray[indexDest + 1]);
		else
			// If we're moving up, then we insert the element before our reference (indexDest)
			startElement.parentElement.insertBefore(startElement, rowsArray[indexDest]);

		// Mark all rows in "not selected" class to reset previous dragOver
		unselectRows(rowsArray);
	}
};