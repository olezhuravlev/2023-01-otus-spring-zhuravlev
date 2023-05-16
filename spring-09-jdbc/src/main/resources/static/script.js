let authors = [];
let genres = [];

let authorsUrl = "/authors";
let genresUrl = "/genres";
let booksUrl = "/books";
let bookCommentsUrl = "/comments";

let tempIdPrefix = "-";
let bookRowIdPrefix = "bookRow_";
let commentRowIdPrefix = "commentRow_";

let newElementAttribute = "isNew";

let bookIdAttribute = "book_id";
let bookCommentIdAttribute = "comment_id";

let bookSelector = ".book-selector-";
let bookIdSelector = bookSelector + "id";
let bookTitleSelector = bookSelector + "title";
let bookAuthorSelector = bookSelector + "author";
let bookGenreSelector = bookSelector + "genre";
let bookSaveButtonSelector = bookSelector + "button-save";
let bookDeleteButtonSelector = bookSelector + "button-delete";
let bookDetailsButtonSelector = bookSelector + "button-details";

let bookCommentSelector = ".book-comment-selector-";
let bookCommentIdSelector = bookCommentSelector + "id";
let bookCommentBookIdSelector = bookCommentSelector + "book-id";
let bookCommentTextSelector = bookCommentSelector + "text";
let commentSaveButtonSelector = bookCommentSelector + "button-save";

let tableBodyId = "tableBody";
let commentTableBodyId = "commentTableBody";

let blankBookRowId = "blankBookRow";
let blankBookCommentRowId = "blankBookCommentRow";

let fieldErrorClass = "field-error";
let fieldErrorMessageClass = "field-error-message";

let infoBannerClass = "info";
let infoBannerMessageClass = "message-info";
let infoBannerTransitionClass = "banner-transition";

function showSysInfoBanner(val) {

    let message = document.createElement("p");
    message.classList.add(infoBannerMessageClass);
    message.innerHTML = val;

    let banner = createDiv(infoBannerClass);
    banner.appendChild(message);

    document.body.appendChild(banner);
    setTimeout(() => banner.classList.add(infoBannerTransitionClass), 100);
    setTimeout(() => banner.classList.remove(infoBannerTransitionClass), 1000);
    setTimeout(() => banner.remove(), 2000);
}

window.addEventListener("load", event => {
    serverRequest("/sysinfo", "POST", showSysInfoBanner);
});

window.addEventListener("click", event => {
    const errorsElements = document.querySelectorAll(".field-error");
    errorsElements.forEach(element => element.remove());
});

function internalHandler(e) {
    e.preventDefault(); // required in some browsers
    e.returnValue = ""; // required in some browsers
}

function setDirty(value) {
    if (value === true) {
        if (window.addEventListener) {
            window.addEventListener('beforeunload', internalHandler, true);
        } else if (window.attachEvent) {
            window.attachEvent('onbeforeunload', internalHandler);
        }
    } else {
        if (window.removeEventListener) {
            window.removeEventListener('beforeunload', internalHandler, true);
        } else if (window.detachEvent) {
            window.detachEvent('onbeforeunload', internalHandler);
        }
    }
}

function redirect(url) {
    window.location.href = url;
}

function generateId(prefix) {
    return prefix + Date.now();
}

function isValidString(value) {
    return value.trim().length !== 0;
}

function setNewElement(element, isNew) {
    if (isNew) {
        element.setAttribute(newElementAttribute, '');
    } else {
        element.removeAttribute(newElementAttribute);
    }
}

function isNewElement(element) {
    return element.hasAttribute(newElementAttribute);
}

function isSavingPossible(element) {
    return true;
}

function enableButtons(element, enableSave, enableDelete) {

    let bookElement = getParentAttributedElement(element, bookIdAttribute);
    let saveButton = selectChildElement(bookElement, bookSaveButtonSelector);

    if (isSavingPossible(bookElement)) {
        saveButton.disabled = !enableSave;
    } else {
        saveButton.disabled = true;
    }

    let deleteButton = selectChildElement(bookElement, bookDeleteButtonSelector);
    deleteButton.disabled = !enableDelete;
}

function serverRequest(url, method, onSuccessHandler, onFailHandler, params) {

    let xhr = new XMLHttpRequest();
    xhr.open(method, url);
    xhr.setRequestHeader("Accept", "application/json");
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.setRequestHeader("Cache-control", "no-cache");
    xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");

    xhr.onreadystatechange = function () {
        if (xhr.readyState !== 4) {
            return false;
        }
        if (xhr.status < 400 && onSuccessHandler) {
            onSuccessHandler(xhr.responseText);
        } else if (onFailHandler) {
            onFailHandler(xhr.responseText);
        }
    }
    xhr.send(params);
}

function appendOption(parentElement, value, text) {

    let option = document.createElement("option");
    option.value = value;
    option.text = text;
    parentElement.appendChild(option);

    return option;
}

function getParentAttributedElement(element, attribute) {

    let isAttributeHolder = element.hasAttribute(attribute);
    if (isAttributeHolder) {
        return element;
    }

    let parent = element.parentElement;
    while (parent) {
        isAttributeHolder = parent.hasAttribute(attribute);
        if (isAttributeHolder) {
            return parent;
        }
        parent = parent.parentElement;
    }

    return null;
}

function updateChildrenCallback(element, collection) {
    return function updateChildren() {
        let selectedOptionId = element.value;
        removeChildrenExcept(element, selectedOptionId);
        addChildrenExcept(element, collection, selectedOptionId);
    }
}

function addChildrenExcept(element, collection, selectedOptionId) {
    for (let i = 0; i < collection.length; i++) {
        let currentSourceId = collection[i].id;
        let currentSourceName = collection[i].name;
        if (currentSourceId !== selectedOptionId) {
            appendOption(element, currentSourceId, currentSourceName);
        }
    }
}

function removeChildrenExcept(element, selectedOptionId) {
    let idx = element.children.length - 1;
    while (idx > -1) {
        let currentChild = element.children[idx--];
        if (currentChild.value !== selectedOptionId) {
            element.removeChild(currentChild);
        }
    }
}

function selectChildElement(bookRow, selector) {
    if (selector) {
        return bookRow.querySelector(selector);
    } else {
        return bookRow;
    }
}

function retrieveAuthors(onReadyHandler) {
    serverRequest(authorsUrl, "POST", function onSuccessHandler(jsonText) {
        let parsed = parseJSON(jsonText);
        for (let i = 0; i < parsed.length; i++) {
            authors.push(parsed[i]);
        }
        if (onReadyHandler) {
            onReadyHandler();
        }
    });
}

function fillAuthorSelection(element) {
    if (!authors.length) {
        retrieveAuthors(updateChildrenCallback(element, authors));
    } else {
        updateChildrenCallback(element, authors)();
    }
}

function retrieveGenres(onReadyHandler) {
    serverRequest(genresUrl, "POST", function onSuccessHandler(jsonText) {
        let parsed = parseJSON(jsonText);
        for (let i = 0; i < parsed.length; i++) {
            genres.push(parsed[i]);
        }
        if (onReadyHandler) {
            onReadyHandler();
        }
    });
}

function onSelectAuthor(element) {
    enableButtons(element, true, true);
    setDirty(true);
}

function onSelectGenre(element) {
    enableButtons(element, true, true);
    setDirty(true);
}

function fillGenreSelection(element) {
    if (!genres.length) {
        retrieveGenres(updateChildrenCallback(element, genres));
    } else {
        updateChildrenCallback(element, genres)();
    }
}

function parseJSON(jsonString) {
    try {
        let parsed = JSON.parse(jsonString);
        if (parsed && typeof parsed === 'object') {
            return parsed;
        } else {
            return [];
        }
    } catch (e) {
        return []
    }
}

function setBookId(element, bookId) {

    let holder = getParentAttributedElement(element, bookIdAttribute);
    if (holder) {
        holder.setAttribute(bookIdAttribute, bookId);
        return true;
    }

    return false;
}

function getBookId(element) {

    let holder = getParentAttributedElement(element, bookIdAttribute);
    if (holder) {
        return holder.getAttribute(bookIdAttribute);
    }

    return null;
}

function onChangeText(element) {
    enableButtons(element, true, true);
    setDirty(true);
}

function showBookDetails(element) {
    let bookId = getBookId(element);
    redirect(booksUrl + "/" + bookId);
}

function addNewBook() {

    let tempId = generateId(tempIdPrefix);

    let newRow = document.getElementById(blankBookRowId).children[0].cloneNode(true);
    setNewElement(newRow, true);
    newRow.setAttribute("id", bookRowIdPrefix + tempId);
    setBookId(newRow, tempId);

    let bookIdElement = selectChildElement(newRow, bookIdSelector);
    bookIdElement.innerHTML = tempId;

    let bookDetailsButtonElement = selectChildElement(newRow, bookDetailsButtonSelector);
    bookDetailsButtonElement.disabled = true;

    let bookTitleElement = selectChildElement(newRow, bookTitleSelector);
    bookTitleElement.value = "";

    let bookAuthorElement = selectChildElement(newRow, bookAuthorSelector);
    bookAuthorElement.innerHTML = "";
    let emptyAuthorOption = appendOption(bookAuthorElement, "", selectAuthorText);
    emptyAuthorOption.disabled = true;

    let selectGenreElement = selectChildElement(newRow, bookGenreSelector);
    selectGenreElement.innerHTML = "";
    let emptyGenreOption = appendOption(selectGenreElement, "", selectGenreText);
    emptyGenreOption.disabled = true;

    let tableBody = document.getElementById(tableBodyId);
    tableBody.appendChild(newRow);
}

function saveBook(element) {

    let host = getParentAttributedElement(element, bookIdAttribute);
    let bookId = host.getAttribute(bookIdAttribute);

    let title = selectChildElement(host, bookTitleSelector).value;
    let authorId = selectChildElement(host, bookAuthorSelector).value;
    let genreId = selectChildElement(host, bookGenreSelector).value;

    let params = {
        id: bookId,
        title: title,
        author: {id: authorId},
        genre: {id: genreId},
    };

    serverRequest(booksUrl, "PUT",
        function successHandler(jsonText) {
            let parsed = parseJSON(jsonText);
            setBookValues(element, parsed);
            setNewElement(host, false);
            setDirty(false);
        },
        function failHandler(jsonText) {
            let parsed = parseJSON(jsonText);
            showMessages(element, parsed, bookIdAttribute, bookSelector);
        },
        JSON.stringify(params));
}

function showMessages(element, json, hostAttribute, fieldSelectorPrefix) {
    let hostElement = getParentAttributedElement(element, hostAttribute);
    for (field in json) {
        let targetElement = selectChildElement(hostElement, fieldSelectorPrefix + field);
        if (targetElement) {
            attachErrorMessage(targetElement, json[field]);
        }
    }
}

function attachErrorMessage(element, value) {
    let host = createDiv(fieldErrorClass);
    for (field in value) {
        let message = document.createElement("p");
        message.classList.add(fieldErrorMessageClass);
        message.innerHTML = value[field];
        host.appendChild(message);
    }
    element.after(host);
}

function createDiv(cssClass) {
    let element = document.createElement("div");
    element.classList.add(cssClass);
    return element;
}

function setBookValues(element, json) {

    let hostElement = getParentAttributedElement(element, bookIdAttribute);
    let bookId = hostElement.getAttribute(bookIdAttribute);

    // Update ID used for host element identification.
    if (json.id !== bookId) {
        bookId = json.id;
        hostElement.setAttribute("id", bookRowIdPrefix + bookId);
        hostElement.setAttribute(bookIdAttribute, bookId);

        let bookIdElement = selectChildElement(hostElement, bookIdSelector);
        bookIdElement.innerHTML = bookId;
    }

    let bookDetailsButtonElement = selectChildElement(hostElement, bookDetailsButtonSelector);
    // Element can absent on some forms.
    if (bookDetailsButtonElement) {
        bookDetailsButtonElement.disabled = false;
    }

    let inputTitleElement = selectChildElement(hostElement, bookTitleSelector);
    inputTitleElement.value = json.title;

    let selectAuthorElement = selectChildElement(hostElement, bookAuthorSelector);
    selectAuthorElement.innerHTML = "";
    if (json.author) {
        appendOption(selectAuthorElement, json.author.id, json.author.name);
    } else {
        appendOption(selectAuthorElement, "", "");
    }

    let selectGenreElement = selectChildElement(hostElement, bookGenreSelector);
    selectGenreElement.innerHTML = "";
    if (json.genre) {
        appendOption(selectGenreElement, json.genre.id, json.genre.name);
    } else {
        appendOption(selectGenreElement, "", "");
    }

    let saveButtonElement = selectChildElement(hostElement, bookSaveButtonSelector);
    saveButtonElement.disabled = true;
}

function deleteBook(element) {

    let host = getParentAttributedElement(element, bookIdAttribute);
    if (isNewElement(host)) {
        host.remove();
        return;
    }

    if (!confirm(deleteBookMessage)) {
        return;
    }

    let bookId = getBookId(host);
    serverRequest(booksUrl + "/" + bookId, "DELETE", function onSuccessHandler(jsonText) {
        let parsed = parseJSON(jsonText);
        if (parsed.result.toUpperCase() === "OK") {
            host.remove();
            redirect("/");
        }
    });
}

function setBookCommentId(element, commentId) {

    let holder = getParentAttributedElement(element, bookCommentIdAttribute);
    if (holder) {
        holder.setAttribute(bookCommentIdAttribute, commentId);
        return true;
    }

    return false;
}

function getCommentId(element) {

    let holder = getParentAttributedElement(element, bookCommentIdAttribute);
    if (holder) {
        return holder.getAttribute(bookCommentIdAttribute);
    }

    return null;
}

function onChangeCommentText(element) {

    if (!isValidString(element.value)) {
        return;
    }

    element.value = element.value.trim();

    let bookCommentElement = getParentAttributedElement(element, bookCommentIdAttribute);
    let saveButtonElement = selectChildElement(bookCommentElement, commentSaveButtonSelector);
    if (saveButtonElement) {
        saveButtonElement.disabled = false;
    }

    setDirty(true);
}

function addNewBookComment(element) {

    let bookId = getBookId(element);
    let tempId = generateId(tempIdPrefix);

    let newRow = document.getElementById(blankBookCommentRowId).children[0].cloneNode(true);
    setNewElement(newRow, true);
    newRow.setAttribute("id", commentRowIdPrefix + tempId);
    setBookCommentId(newRow, tempId);

    let bookCommentBookIdElement = selectChildElement(newRow, bookCommentBookIdSelector);
    bookCommentBookIdElement.innerHTML = bookId;

    let bookCommentIdElement = selectChildElement(newRow, bookCommentIdSelector);
    bookCommentIdElement.innerHTML = tempId;

    let bookCommentTextElement = selectChildElement(newRow, bookCommentTextSelector);
    bookCommentTextElement.value = "";

    let commentSaveButtonElement = selectChildElement(newRow, commentSaveButtonSelector);
    commentSaveButtonElement.disabled = true;

    let tableBody = document.getElementById(commentTableBodyId);
    tableBody.insertBefore(newRow, tableBody.children[0]);
}

function saveBookComment(element) {

    let host = getParentAttributedElement(element, bookCommentIdAttribute);
    let commentId = host.getAttribute(bookCommentIdAttribute);

    let commentText = selectChildElement(host, bookCommentTextSelector).value;
    let commentBookId = selectChildElement(host, bookCommentBookIdSelector).innerText;

    let params = {
        id: commentId,
        text: commentText,
        bookId: commentBookId,
    };

    serverRequest(bookCommentsUrl, "PUT",
        function onSuccessHandler(jsonText) {
            let parsed = parseJSON(jsonText);
            setCommentValues(parsed, element);
            setNewElement(host, false);
            setDirty(false);
        },
        function failHandler(jsonText) {
            let parsed = parseJSON(jsonText);
            showMessages(element, parsed, bookCommentIdAttribute, bookCommentSelector);
        },
        JSON.stringify(params));
}

function setCommentValues(json, element) {

    let hostElement = getParentAttributedElement(element, bookCommentIdAttribute);
    let commentId = hostElement.getAttribute(bookCommentIdAttribute);

    // Update ID used for host element identification.
    if (json.id !== commentId) {
        commentId = json.id;
        hostElement.setAttribute("id", commentRowIdPrefix + commentId);
        hostElement.setAttribute(bookCommentIdAttribute, commentId);

        let commentIdElement = selectChildElement(hostElement, bookCommentIdSelector);
        commentIdElement.innerHTML = commentId;
    }

    let saveButtonElement = selectChildElement(hostElement, commentSaveButtonSelector);
    saveButtonElement.disabled = true;
}

function deleteBookComment(element) {

    let host = getParentAttributedElement(element, bookCommentIdAttribute);
    if (isNewElement(host)) {
        host.remove();
        return;
    }

    if (!confirm(deleteBookCommentMessage)) {
        return;
    }

    let commentId = getCommentId(host);
    serverRequest(bookCommentsUrl + "/" + commentId, "DELETE", function onSuccessHandler(jsonText) {
        let parsed = parseJSON(jsonText);
        if (parsed.result.toUpperCase() === "OK") {
            host.remove();
            setDirty(false);
        }
    });
}
