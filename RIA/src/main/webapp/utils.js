
function makeCall(method, url, formElement, cback, reset = true) {
	let req = new XMLHttpRequest(); // visible by closure
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
	let req = new XMLHttpRequest();
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

	let regx = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/

	if (email.value.match(regx)) {
		return (true);
	}
	alert("You have entered an invalid email address!")
	return (false);
}



function checkPasswordsMatch(password1, password2) {
	if (password1.value != password2.value) {
		alert("passwords don't match");
		return (false);
	}
	return (true);
}

function validAlbumName(name) {
	return name.length > 0 && name.length < 32;
}

let startElement = null;
/* 
			This fuction puts all row to "notselected" class, 
			then we use CSS to put "notselected" in black and "selected" in red
	*/
function unselectRows(rowsArray) {
	for (let i = 0; i < rowsArray.length; i++) {
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

function clearBackButtonListeners() {
	let button = document.getElementById("id_backbutton");
	let newNode = button.cloneNode(true);
	button.parentNode.replaceChild(newNode, button);
	return newNode;
}