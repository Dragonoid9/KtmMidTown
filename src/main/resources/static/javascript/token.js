// Function to get a cookie by name
function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
}

// Function to set a cookie
function setCookie(name, value, days) {
    let expires = "";
    if (days) {
        const date = new Date();
        date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
        expires = `; expires=${date.toUTCString()}`;
    }
    document.cookie = `${name}=${value || ""}${expires}; path=/; secure; HttpOnly`;
}

document.addEventListener("DOMContentLoaded", function() {
    // Retrieve the token from cookies
    const storedToken = getCookie('jwtToken');
    const endpoint = '/rac/secureEndpoint'; // Ensure this is the correct endpoint

    if (storedToken) {
        fetch(endpoint, { // Make a GET request to the endpoint
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${storedToken}`,
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => console.log(data))
            .catch(error => console.error('Error:', error));
    } else {
        console.log('Token not found');
    }
});
