$(function(){

        var loggedinUser = sessionStorage.getItem("username");
        jQuery.ajax({
            url: "/userhistory/",
            type: "GET"
        }).done(function (data) {
            data.forEach(function (item) {
                if (loggedinUser == item.userName) {
                    $("#place1").val(item.latest);
                    $("#place2").val(item.second);
                    $("#place3").val(item.third);
                }
            })

        })

})