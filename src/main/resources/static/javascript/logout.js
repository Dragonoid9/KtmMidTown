// Function to clear all cookies
function clearAllCookies() {
    const cookies = document.cookie.split("; ");
    for (const cookie of cookies) {
        const eqPos = cookie.indexOf("=");
        const name = eqPos > -1 ? cookie.substring(0, eqPos) : cookie;
        document.cookie = name + "=;expires=Thu, 01 Jan 1970 00:00:00 GMT;path=/";
    }
}

// Function to clear session storage
function clearSessionStorage() {
    sessionStorage.clear();
}

// Function to clear local storage
function clearLocalStorage() {
    localStorage.clear();
}

// Function to clear all storage and cookies
function clearAllStorageAndCookies() {
    clearAllCookies();
    clearSessionStorage();
    clearLocalStorage();
}

// Call this function on logout or page unload
window.addEventListener('load', () => {
    const logoutLink = document.getElementById('logout-link');
    if (logoutLink) {
        logoutLink.addEventListener('click', (event) => {
            event.preventDefault();
            clearAllStorageAndCookies();
            document.getElementById('logout-form').submit();
        });
    }
});