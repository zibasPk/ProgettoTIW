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


		this.show = () => {
			makeCall("GET", "GetHomeData", null,
				(req) => {
					if (req.readyState == XMLHttpRequest.DONE) {
						var message = req.responseText;
						switch (req.status) {
							case 200:
								var json = JSON.parse(req.responseText);
								var myAlbumsToShow = json[0];
								var otherAlbumsToShow = json[1];
								this.noAlbumAlert.textContent = "No Albums to your name"
								if (myAlbumsToShow.length != 0) {
									this.updateMyAlbums(myAlbumsToShow);
									this.noAlbumAlert.textContent = "";
								}
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

					var images = new ShowAlbumImages(album.id);
					images.startImages();
					images.show(album.id);

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

					var images = new ShowAlbumImages(album.id);
					images.startImages();
					images.show(album.id);

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

	function ShowAlbumImages(albumID) {

		let page = 1;

		this.startImages = function() {
			document.getElementById("id_allalbums").innerHTML = "";
			document.getElementById("id_buttons").style.visibility = "visible";
		}

		this.show = function(albumID) {

			makeCall("GET", "GetAlbumData?albumid=" + albumID, null,
				(req) => {
					if (req.readyState == XMLHttpRequest.DONE) {
						var message = req.responseText;
						switch (req.status) {
							case 200:
								var json = JSON.parse(req.responseText);
								var albumImages = json;
								document.getElementById("id_noimagesalert").textContent = "this Album has no images!";
								document.getElementById("id_buttons").style.visibility = "hidden";
								if (albumImages.length != 0) {
									this.clearPage();
									this.update(albumImages, page);
									document.getElementById("id_noimagesalert").textContent = "";
								}
								break;
							case 502:
								this.alert.textContent = message;
								break;
						}
					}
				});
		}

		this.update = function(albumImages, page) {

			document.getElementById("id_next").style.visibility = "visible";
			document.getElementById("id_previous").style.visibility = "visible";
			document.getElementById("id_backtoalbums").style.visibility = "visible";
			document.getElementById("id_albumimages").style.visibility = "visible";
			document.getElementById("id_albumimages").innerHTML = "";

			let row, imageCell, dataCell, date;

			albumImages.forEach((image, index) => {
				if (index < (page * 5) && index >= (page * 5) - 5) {

					row = document.createElement("tr");
					imageCell = document.createElement("td");
					imageCell.innerHTML = '<img src ="/pure_html/' + image.path + '"/>';
					row.appendChild(imageCell);
					dataCell = document.createElement("td");
					date = new Date(image.date).toLocaleDateString();
					dataCell.textContent = image.title + " " + date + "\n" + image.description;
					row.appendChild(dataCell);
					document.getElementById("id_albumimages").appendChild(row);
				}
			});

			if (page * 5 > albumImages.length) {
				document.getElementById("id_next").style.visibility = "hidden";
			}
			if (page < 2) {
				document.getElementById("id_previous").style.visibility = "hidden";
			}


			document.getElementById("id_next").addEventListener("click", (e) => {
				page++;
				this.update(albumImages, page);



			}, false);
			document.getElementById("id_previous").addEventListener("click", (e) => {

				page--;
				this.update(albumImages, page);

			}, false);

			document.getElementById("id_backtoalbums").addEventListener("click", (e) => {
				window.location.href = "home.html";
			})


		}

		this.clearPage = () => {
			// document.getElementById("allalbums").innerHTML = "";
			return;
		}

	}

	function PageOrchestrator() {
		var alertContainer;

		this.start = function() {
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
							case 400: // bad request
								alertContainer.textContent = message;
								break;
							case 502: // bad gateway
								alertContainer.textContent = message;
								break;
						}
					}
				})
			})
		};
		this.refresh = function() {
			alertContainer.textContent = "";
			albums.reset();
			albums.show();

			//the following line of code makes so that the image table isn't visible at the beginning (testing phase for it)
			document.getElementById("id_albumimages").style.visibility = "hidden";
			document.getElementById("id_buttons").style.visibility = "hidden";

		};
	}

	let startElement = null;
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

		if (startElement != null) {
			startElement.getElementsByTagName("a")[0];
			// Move the dragged element to the new position
			if (rowsArray.indexOf(startElement) < indexDest)
				// If we're moving down, then we insert the element after our reference (indexDest)
				startElement.parentElement.insertBefore(startElement, rowsArray[indexDest + 1]);
			else
				// If we're moving up, then we insert the element before our reference (indexDest)
				startElement.parentElement.insertBefore(startElement, rowsArray[indexDest]);
		}

		// Mark all rows in "not selected" class to reset previous dragOver
		unselectRows(rowsArray);
		// Make start element null
		startElement = null;
	}
};