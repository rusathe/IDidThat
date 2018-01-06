$(function(){

        var loggedinUser = sessionStorage.getItem("username");
        jQuery.ajax({
            url: "/checkin/",
            type: "GET"
        }).done(function (data) {
            data.content.forEach(function(item) {
                if (loggedinUser != item.userID) {
                    $("#tovisit").val(item.placeName);
                }
            })

        })

})