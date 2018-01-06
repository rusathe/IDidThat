function validateregistration(pagename){
    if(sessionStorage.getItem("signedIn")=="yes"){
        if(pagename=="checkin") {
            window.location.href = "/api/checkin.html"
        }
        else{
            window.location.href = "/api/userPage.html"
        }
    }
    else{
        alert("Please login or register to access this feature");
        window.location.href = "/api/login.html"
    }
}