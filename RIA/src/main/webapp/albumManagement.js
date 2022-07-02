{
	let albums, images, createAlbumPage, pageOrchestrator = new PageOrchestrator(); // main controller

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
		this.pageContainer = options['pageContainer'];
		this.saveOrderButton = options['saveOrderButton'];
		this.createAlbumButton = options['createAlbumButton'];
		this.noAlbumAlert = options['noAlbumAlert'];
		this.myListContainer = options['myListContainer'];
		this.myContainerBody = options['myContainerBody'];
		this.otherListContainer = options['otherListContainer'];
		this.otherContainerBody = options['otherContainerBody'];

		this.saveOrderButton.addEventListener('click', (e) => {
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
		});

		this.createAlbumButton.addEventListener('click', (e) => {
			createAlbumPage.show(this);
		});

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
					images.show(album.id, this);
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
					images.show(album.id, this);
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

		this.clear = () => {
			this.pageContainer.innerHTML = "";
		}
	}

	function AlbumPage(options) {
		this.alert = options['alert'];
		this.pageContainer = options['pageContainer'];
		this.imagesContainer = options['imagesContainer'];
		this.buttonsContainer = options['buttonsContainer'];
		this.previousButton = options['previousButton'];
		this.nextButton = options['nextButton'];
		this.backToAlbumsButton = options['backToAlbumsButton'];

		let page = 1;

		this.show = function (albumID, previous) {
			if (previous != null)
				previous.clear();
			this.buttonsContainer.style.visibility = "visible";
			makeCall("GET", "GetAlbumData?albumid=" + albumID, null,
				(req) => {
					if (req.readyState == XMLHttpRequest.DONE) {
						var message = req.responseText;
						switch (req.status) {
							case 200:
								var json = JSON.parse(req.responseText);
								var albumImages = json;
								this.alert.textContent = "this Album has no images!";
								this.buttonsContainer.style.visibility = "hidden";
								if (albumImages.length != 0) {
									this.update(albumImages, page);
									this.alert.textContent = "";
								}
								break;
							case 502:
								this.alert.textContent = message;
								break;
						}
					}
				});
		}

		this.update = function (albumImages, page) {
			this.nextButton.style.visibility = "visible";
			this.previousButton.style.visibility = "visible";
			this.backToAlbumsButton.style.visibility = "visible";
			this.imagesContainer.style.visibility = "visible";
			this.imagesContainer.innerHTML = "";
			let row, imageCell, imgTag, dataCell, date;
			albumImages.forEach((image, index) => {
				if (index < (page * 5) && index >= (page * 5) - 5) {
					row = document.createElement("tr");
					imageCell = document.createElement("td");
					imgTag = document.createElement("img");
					imgTag.src = image.path;
					imageCell.appendChild(imgTag);
					row.appendChild(imageCell);
					dataCell = document.createElement("td");
					date = new Date(image.date).toLocaleDateString();
					dataCell.textContent = image.title + " " + date + "\n" + image.description;
					row.appendChild(dataCell);
					this.imagesContainer.appendChild(row);
				}
			});

			// show or hide next and previous buttons depending on the page
			if (page * 5 > albumImages.length) {
				this.nextButton.style.visibility = "hidden";
			}

			if (page < 2) {
				this.previousButton.style.visibility = "hidden";
			}

			// bind event listeners to buttons
			this.nextButton.addEventListener("click", (e) => {
				page++;
				this.update(albumImages, page);
			},
				false);

			this.previousButton.addEventListener("click", (e) => {
				page--;
				this.update(albumImages, page);
			},
				false);

			this.backToAlbumsButton.addEventListener("click", (e) => {
				window.location.href = "home.html";
			})
		}

		this.reset = () => {
			this.buttonsContainer.style.visibility = "hidden";
			this.imagesContainer.innerHTML = "";
		}

		this.clear = () => {
			this.buttonsContainer.style.visibility = "hidden";
			this.imagesContainer.innerHTML = "";
		}

	}

	function CreateAlbumPage(pageContainer, alertContainer) {
		this.pageContainer = pageContainer;
		this.alert = alertContainer;
		this.show = (previous) => {
			previous.clear();
			makeCall("GET", "GetCreateAlbumData", null,
				(req) => {
					if (req.readyState == XMLHttpRequest.DONE) {
						let message = req.responseText;
						switch (req.status) {
							case 200:
								let json = JSON.parse(req.responseText);
								let myAlbums = json[0];
								let myImages = json[1];
								this.update(myAlbums, myImages);
								break;
							case 502:
								this.alert.textContent = message;
								break;
						}
					}
				});
		}

		this.update = (myAlbums, myImages) => {
			let createForm, createInput, createButton, addImageForm, addImagesButton, selectAlbum, selectImage;
			pageContainer.innerHTML = "";
			// creation of create album form
			let title1 = document.createElement("p");
			title1.textContent = "Create an album";
			pageContainer.appendChild(title1);
			createForm = document.createElement("form");
			createInput = document.createElement("input");
			createInput.name = "albumName";
			createInput.setAttribute('required', '');
			createButton = document.createElement("input");
			createButton.type = "button";
			createButton.value = "create album";
			createButton.addEventListener('click', e => {
				//send create album message needs to recieve created album
			})
			createForm.appendChild(createInput);
			createForm.appendChild(createButton);
			pageContainer.appendChild(createForm);

			// if there is no need to show the add album form 
			if (myAlbums.length == 0 && myImages.length == 0) {
				this.alert.textContent = "You have no images or albums";
				return;
			}
			if (myImages.length == 0) {
				this.alert.textContent = "You have no images to add to an album";
				return;
			}


			// creation of add album form
			let title2 = document.createElement("p");
			title2.textContent = "Add images to your albums";
			pageContainer.appendChild(title2);
			addImageForm = document.createElement("form");
			selectImage = document.createElement("select");
			myImages.forEach((image) => {
				let option = document.createElement("option");
				option.value = image.id;
				option.text = image.title;
				selectImage.appendChild(option);
			})
			selectAlbum = document.createElement("select");
			myAlbums.forEach((album) => {
				let option = document.createElement("option");
				option.value = album.id;
				option.text = album.title;
				selectAlbum.appendChild(option);
			})
			addImagesButton = document.createElement("input");
			addImagesButton.value = "add image to album";
			addImagesButton.type = "button";
			addImagesButton.addEventListener('click', (e) => {
				//if send add image is a success remove image from select 
			})
			addImageForm.appendChild(selectImage);
			addImageForm.appendChild(selectAlbum);
			addImageForm.appendChild(addImagesButton);
			pageContainer.appendChild(addImageForm);
		}
	}


	function PageOrchestrator() {
		let alertContainer;

		this.start = function () {
			alertContainer = document.getElementById("id_alert");
			albums = new AlbumLists({
				alert: alertContainer,
				pageContainer: document.getElementById("id_page"),
				saveOrderButton: document.getElementById("id_saveorder"),
				createAlbumButton: document.getElementById("id_createalbum"),
				noAlbumAlert: document.getElementById("noalbumalert"),
				myListContainer: document.getElementById("id_myalbumslist"),
				myContainerBody: document.getElementById("id_myalbumsbody"),
				otherListContainer: document.getElementById("id_otheralbumslist"),
				otherContainerBody: document.getElementById("id_otheralbumsbody"),
			});
			images = new AlbumPage({
				alert: alertContainer,
				pageContainer: document.getElementById("id_albumpage"),
				imagesContainer: document.getElementById("id_albumimages"),
				buttonsContainer: document.getElementById("id_buttons"),
				previousButton: document.getElementById("id_previous"),
				nextButton: document.getElementById("id_next"),
				backToAlbumsButton: document.getElementById("id_backtoalbums"),
			});
			createAlbumPage = new CreateAlbumPage(document.getElementById("id_page"), alertContainer);

			document.getElementById("logoutbutton").addEventListener('click', (e) => {
				window.sessionStorage.removeItem('username');
				makeCall("POST", 'Logout', null, (x) => { });
				window.location.href = "index.html";
			});

		};

		this.refresh = function () {
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