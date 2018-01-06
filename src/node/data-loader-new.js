var MongoClient = require('mongodb').MongoClient;

var dbConnection = null;

var lockCount = 0;

function getDbConnection(callback){
    MongoClient.connect("mongodb://localhost/ididthatDB", function(err, db){
        if(err){
            console.log("Unable to connect to Mongodb");
        }else{
            dbConnection = db;
            callback();
        }
    });
};

function closeConnection() {
    if (dbConnection)
        dbConnection.close();

}

getDbConnection(function(){
    dbConnection.dropDatabase(function(err,doc){
        if (err)
            console.log("Could not drop database");
        else
            addUser();
            addPlace();
            addCheckin();
            adduserhistory();
            addcategory();
            addlocation();
            addsearchdata();
    });
});

function addUser() {
    u = [{
        "userName": "Jerry",
        "emailAddress": "jerry@tom.com",
        "password": "abc"
    },
        {
            "userName": "Tom",
            "emailAddress": "tom@jerry.com",
            "password": "abcd"
        }];
    var users = dbConnection.collection('users');
    users.insertOne(u[0], function (err, doc) {
        if (err) {
            console.log("Could not add user 1");
        }
        else {
            //addCheckintoUser(doc.ops[0]._id.toString(), 45);
        }
    })
    users.insertOne(u[1], function (err, doc) {
        if (err) {
            console.log("Could not add user 2");
        }
        else {
            //addCheckintoUser(doc.ops[0]._id.toString(), 120);
        }
    })
}
function addPlace() {

    p = [{
        "placeName": "Shana",
        "cityName" : "mtv",
        "placeCategoryType": "Restaurant",
        "numberCheckins": 2,
        "avgRating": 4.1,
        "latestRankingbyCategory": 1

    },
    {
        "placeName": "Park1",
        "cityName" : "mtv",
        "placeCategoryType": "Park",
        "numberCheckins": 2,
        "avgRating": 4.1,
        "latestRankingbyCategory": 2
    }];

    var places = dbConnection.collection('places');
    places.insertOne(p[0], function (err, doc) {
        if (err) {
            console.log("Could not add place 1");
        }
        else {
            addRatingtoPlace(doc.ops[0]._id.toString(), 150);
        }
    })
    places.insertOne(p[1], function (err, doc) {
        if (err) {
            console.log("Could not add place 2");
        }
        else {
            addRatingtoPlace(doc.ops[0]._id.toString(), 100);
        }
    })

}
/*
placeNameList = ['Stevens Creek', 'Levis Stadium', 'NASA Ames', 'Great America', 'GooglePlex', 'Madras Cafe'];

    function addCheckintoUser(userID,count) {
        sequence = Array(count);
        console.log("sequence",sequence);
        var c = [];
        for (i=0;i<count;i++){
            console.log("Trying")
            var placeName = placeNameList[Math.floor(Math.random() * placeNameList.length)];
            var placeID = userID + i;
            var number = Math.floor(Math.random() * 100);
            var userID = userID;
            var rating = rating;
            c.push ({
                placeID : placeID,
                userID : userID,
                placeName : placeName,
                isExisting : true,
                rating : rating
            });

        }

        c.forEach(function(checkins){
            var checkin = dbConnection.collection('checkin');
            checkin.insertOne(checkins);
        })

    }

*/
function addCheckin() {

    p = [{
        placeID : "122",
        userID: "dummy2",
        placeName : "newPlace2",
        isExisting: true,
        rating: 4

    },
        {
            placeID : "121",
            userID: "dummy1",
            placeName : "newPlace",
            isExisting: true,
            rating: 4
        }];

    var checkin = dbConnection.collection('checkin');
    checkin.insertOne(p[0], function (err, doc) {
        if (err) {
            console.log("Could not add checkin 1");
        }
    })
    checkin.insertOne(p[1], function (err, doc) {
        if (err) {
            console.log("Could not add checkin 2");
        }
    })

}


function addRatingtoPlace(placeID,count) {
    sequence = Array(count);
    console.log("sequence",sequence);
    var c = [];
    for (i=0;i<count;i++){
        var rating = Math.floor(((Math.random() * 5)));
        var avgRating = Math.floor(((Math.random() * 5)));


        var placeID = placeID;


        c.push ({
            rating : rating,
            avgRating : avgRating,
            placeID : placeID
        });

    }

    c.forEach(function(rating){
        var ratings = dbConnection.collection('ratings');
        ratings.insertOne(rating);
    })

}


function adduserhistory() {


    var userh = dbConnection.collection('userhistory');

}

function addsearchdata() {

    sd = [
        {
            "placeLocation":"Santana Row",
            "placeType":"Restaurant"
        },
        {
            "placeLocation":"Palo Alto",
            "placeType":"Mall"
        },
        {
            "placeLocation":"Chicago",
            "placeType":"Trail"
        },
        {
            "placeLocation":"New York",
            "placeType":"Park"
        },
        {
            "placeLocation":"Mumbai",
            "placeType":"Beach"
        },
        {
            "placeLocation":"New Delhi",
            "placeType":"Park"
        }
    ]
    var places = dbConnection.collection('searchdata');
    places.insertOne(sd[0], function (err, doc) {
        if (err) {
            console.log("Could not add search data 1");
        }
    })
    places.insertOne(sd[1], function (err, doc) {
        if (err) {
            console.log("Could not add search data 2");
        }
    })
    places.insertOne(sd[2], function (err, doc) {
        if (err) {
            console.log("Could not add search data 3");
        }
    })
    places.insertOne(sd[3], function (err, doc) {
        if (err) {
            console.log("Could not add search data 4");
        }
    })
    places.insertOne(sd[4], function (err, doc) {
        if (err) {
            console.log("Could not add search data 5");
        }
    })
    places.insertOne(sd[5], function (err, doc) {
        if (err) {
            console.log("Could not add search data 6");
        }
    })

}

function addcategory() {

    c = [{
        "categoryName" : "Park"
    },
        {
            "categoryName" : "Trail"
        },
        {
            "categoryName" : "Restaurant"
        },
        {
            "categoryName" : "Museum"
        },
        {
            "categoryName" : "Theatre"
        },
        {
            "categoryName" : "Mall"
        }
    ]
    var places = dbConnection.collection('category');
    places.insertOne(c[0], function (err, doc) {
        if (err) {
            console.log("Could not add category 1");
        }
    })
    places.insertOne(c[1], function (err, doc) {
        if (err) {
            console.log("Could not add category 2");
        }
    })
    places.insertOne(c[2], function (err, doc) {
        if (err) {
            console.log("Could not add category 3");
        }
    })
    places.insertOne(c[3], function (err, doc) {
        if (err) {
            console.log("Could not add category 4");
        }
    })
    places.insertOne(c[4], function (err, doc) {
        if (err) {
            console.log("Could not add category 5");
        }
    })
}

function addlocation() {

    l = [
        {
            "locName": "Cascal",
            "cityName": "Mountain View",
            "stateName": "CA",
            "countryName": "USA",
            "isSuburb": 1,
            "numberCheckin": 3
        },
        {
            "locName": "Los Gatos Creek",
            "cityName": "San Jose",
            "stateName": "CA",
            "countryName": "USA",
            "isSuburb": 1,
            "numberCheckin": 6
        },
        {
            "locName": "Essel World",
            "cityName": "Mumbai",
            "stateName": "MH",
            "countryName": "IN",
            "isSuburb": 1,
            "numberCheckin": 20
        },
        {
            "locName": "Red Fort",
            "cityName": "New Delhi",
            "stateName": "DL",
            "countryName": "IN",
            "isSuburb": 1,
            "numberCheckin": 6
        },
        {
            "locName": "Ross Diner",
            "cityName": "Los Altos",
            "stateName": "CA",
            "countryName": "USA",
            "isSuburb": 0,
            "numberCheckin": 2
        },
        {
            "locName": "CiderClub",
            "cityName": "San Jose",
            "stateName": "CA",
            "countryName": "USA",
            "isSuburb": 1,
            "numberCheckin": 7
        }
    ]
    var places = dbConnection.collection('location');
    places.insertOne(l[0], function (err, doc) {
        if (err) {
            console.log("Could not add location 1");
        }
    })
    places.insertOne(l[1], function (err, doc) {
        if (err) {
            console.log("Could not add location 2");
        }
    })
    places.insertOne(l[2], function (err, doc) {
        if (err) {
            console.log("Could not add location 3");
        }
    })
    places.insertOne(l[3], function (err, doc) {
        if (err) {
            console.log("Could not add location 4");
        }
    })
    places.insertOne(l[4], function (err, doc) {
        if (err) {
            console.log("Could not add location 5");
        }
    })
    places.insertOne(l[5], function (err, doc) {
        if (err) {
            console.log("Could not add location 6");
        }
    })
}
