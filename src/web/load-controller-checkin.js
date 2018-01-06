    function checkinPlace(e) {
            e.preventDefault();
            console.log("Hello there")
            var theplace=null;
            theplace=$("#placeName").val();
            jQuery.ajax({
                url: "/checkin",
                type: "POST",
                data: JSON.stringify({
                    placeID: $("#placeName").val(),
                    userID: $("#userName").val(),
                    placeName: $("#placeName").val(),
                    isExisting: true,
                    rating: $("#ratingVal").val()
                }),
                dataType: "json",
                contentType: "application/json; charset=utf-8"
            })
            jQuery.ajax({
                url: "/place",
                type: "POST",
                data: JSON.stringify({
                    placeName: $("#placeName").val(),
                    cityName: $("#cityName").val(),
                    placeCategoryType: $("#placeCategory").val(),
                    numberCheckins: 1,
                    avgRating: 5.0,
                    latestRankingbyCategory: 1
                }),
                dataType: "json",
                contentType: "application/json; charset=utf-8"
            }).done(function (data) {
                alert("you've successfully checked in your visited place");
                saveUserHistory(theplace);
                window.location.href = 'userPage.html'
            })
                .fail(function (xhr) {
                    alert('Request Status: ' + xhr.status + ' Status Text: ' + xhr.statusText + ' ' + xhr.responseText);
                });

        }

        function setRating(ratingvalue) {
            $("#ratingVal").val(ratingvalue);
        }

        function setCityName(pname) {
            $("#cityName").val(pname);
        }
        function saveUserHistory(theplace) {
            var userId=null;
            var two=null;
            var three=null;
            jQuery.ajax ({
                url: "/userhistory",
                type: "GET"
            }).done(function(data){
                    data.forEach(function(item){
                        if(document.getElementById("userName").value == item.userName){
                            userId = item.id;
                            two=item.latest;
                            three=item.second;
                            jQuery.ajax({
                                url: "/userhistory/"+userId,
                                type: "PATCH",
                                data: JSON.stringify({
                                    latest: theplace,
                                    second:two,
                                    third:three,
                                }),
                                dataType: "json",
                                contentType: "application/json; charset=utf-8"
                            })
                        }
                    })

                })



        }