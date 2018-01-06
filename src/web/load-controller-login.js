$(function() {
    var signedIn=false;
    $("#login").click(function (e) {
        e.preventDefault();
        jQuery.ajax ({
            url: "/users",
            type: "GET"
        })
            .done(function(data){
                console.log(data);
                var pass=null;
                vartosend =null;
                pass=$("#password").val();
                var encryptedpass= null;
                encryptedpass=CryptoJS.MD5(pass).toString();
                data.forEach(function(item){
                    if(document.getElementById("userName").value == item.userName && encryptedpass==item.password){
                        sessionStorage.setItem("username",document.getElementById("userName").value);
                        signedIn=true;
                        console.log("logged in");
                    }
                });
                if(signedIn) {
                    sessionStorage.setItem("signedIn", "yes");
                    window.location.href = "/api/index.html"
                }else{
                    alert("Incorrect username and password");
                }
            })
            .fail(function(data){alert("something is wrong!")
            })

    });
})