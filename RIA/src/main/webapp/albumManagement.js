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
		this.backButton = options['backButton'];
		this.pageContainer = options['pageContainer'];
		this.saveOrderButton = options['saveOrderButton'];
		this.createAlbumButton = options['createAlbumButton'];
		this.noAlbumAlert = options['noAlbumAlert'];
		this.myListContainer = options['myListContainer'];
		this.myContainerBody = options['myContainerBody'];
		this.otherListContainer = options['otherListContainer'];
		this.otherContainerBody = options['otherContainerBody'];

		this.saveOrderButton.addEventListener('click', (e) => {
			let albums = document.getElementById("id_myalbumsbody");
			let albumsList = new Array();
			for (let i = 0, row; row = albums.rows[i]; i++) {
				let col = row.cells[2];
				let anchor = col.getElementsByTagName("a")[0];
				albumsList.push(anchor.getAttribute("albumid"));
			}
			sendSaveOrder(albumsList, (req) => {
				if (req.readyState == XMLHttpRequest.DONE) {
					let message = req.responseText;
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
			this.backButton = clearBackButtonListeners();
			this.backButton.addEventListener('click', (e) => {
				window.sessionStorage.removeItem('username');
				makeCall("POST", 'Logout', null, (x) => { });
				window.location.href = "index.html";
			})
			makeCall("GET", "GetHomeData", null,
				(req) => {
					if (req.readyState == XMLHttpRequest.DONE) {
						let message = req.responseText;
						switch (req.status) {
							case 200:
								let json = JSON.parse(req.responseText);
								let myAlbumsToShow = json[0];
								let otherAlbumsToShow = json[1];
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
			let elem, i, row, nameCell, dateCell, linkCell, anchor, linkText;
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
			let elem, i, row, nameCell, dateCell, linkCell, anchor, linkText;
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
			this.backToAlbumsButton = clearBackButtonListeners();
			this.backToAlbumsButton.addEventListener("click", (e) => {
				window.location.href = "home.html";
			})
			makeCall("GET", "GetAlbumData?albumid=" + albumID, null,
				(req) => {
					if (req.readyState == XMLHttpRequest.DONE) {
						let message = req.responseText;
						switch (req.status) {
							case 200:
								let json = JSON.parse(req.responseText);
								let albumImages = json;
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
			this.imagesContainer.style.visibility = "visible";
			this.imagesContainer.innerHTML = "";
			let row, imageCell, imgTag, dataCell, date;
			albumImages.forEach((image, index) => {
				if (index < (page * 5) && index >= (page * 5) - 5) {
					row = document.createElement("tr");
					imageCell = document.createElement("td");
					imgTag = document.createElement("img");
					imgTag.src = "." + image.path;
					imageCell.appendChild(imgTag);
					row.appendChild(imageCell);
					dataCell = document.createElement("td");
					date = new Date(image.date).toLocaleDateString();
					dataCell.textContent = image.title + " " + date + "</br>" + image.description;
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

	function CreateAlbumPage(backButton, pageContainer, alertContainer) {
		this.pageContainer = pageContainer;
		this.alert = alertContainer;
		this.backButton = backButton;

		this.show = (previous) => {
			previous.clear();
			this.backButton = clearBackButtonListeners();
			this.backButton = document.getElementById("id_backbutton");
			backButton.addEventListener('click', (e) => {
				
			})
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
			// creation of create album form
			let title1 = document.createElement("p");
			title1.textContent = "Create an album";
			title1.classList.add("createOrAddForm");
			pageContainer.appendChild(title1);
			createForm = document.createElement("form");
			createForm.action = "#";
			createForm.classList.add("createOrAddForm")
			createInput = document.createElement("input");
			createInput.name = "albumName";
			createInput.required = true;
			createButton = document.createElement("input");
			createButton.type = "button";
			createButton.value = "create album";
			createButton.addEventListener('click', e => {
				if (!validAlbumName(createInput.value)) {
					this.alert.textContent = "Invalid album name length";
					return;
				}
				makeCall("POST", "CreateAlbum", createForm,
					(x) => {
						if (x.readyState == XMLHttpRequest.DONE) {
							let message = x.responseText;
							switch (x.status) {
								case 200:
									this.show(this);
									break;
								case 400: // bad request
									this.alert.textContent = message;
									break;
								case 502: // bad gateway
									this.alert.textContent = message;
									break;
							}
						}
					});
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
			title2.classList.add("createOrAddForm")
			pageContainer.appendChild(title2);
			addImageForm = document.createElement("form");
			addImageForm.classList.add("createOrAddForm");
			addImageForm.action = "#";
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

		this.clear = () => {
			pageContainer.innerHTML = "";
		}
	}

	function PageOrchestrator() {
		let alertContainer, backButton;

		this.start = function () {
			alertContainer = document.getElementById("id_alert");
			backButton = document.getElementById("id_backbutton");
			albums = new AlbumLists({
				alert: alertContainer,
				backButton: backButton,
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
				backToAlbumsButton: backButton,
			});
			createAlbumPage = new CreateAlbumPage(backButton, document.getElementById("id_page"), alertContainer);

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
};