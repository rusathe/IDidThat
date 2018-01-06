
function loginUser(e) {
    e.preventDefault();
    var pass=null;
    pass=$("#password").val();
    var encryptedpass= null;
    encryptedpass=CryptoJS.MD5(pass).toString();
    jQuery.ajax({
        url: "/users",
        type: "POST",
        data: JSON.stringify({
            userName: $("#userName").val(),
            emailAddress : $("#emailAddress").val(),
            password : encryptedpass
        }),
        dataType: "json",
        contentType: "application/json; charset=utf-8"
    }).done(function (data) {
        alert("you've successfully signed up");
        jQuery.ajax({
            url: "/userhistory",
            type: "POST",
            data: JSON.stringify({
                userName: $("#userName").val(),
                latest : "",
                second : "",
                third :""
            }),
            dataType: "json",
            contentType: "application/json; charset=utf-8"
        });
        window.location.href = 'login.html'

    })
        .fail(function (xhr) {
            alert('Request Status: ' + xhr.status + ' Status Text: ' + xhr.statusText + ' ' + xhr.responseText);
        });

}

