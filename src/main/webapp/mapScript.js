'use strict';

function init() {
    mymap = L.map('mapdiv').setView([45.69775, 4.7537935], 10);
    // mymap = L.map('mapdiv').setView([41.1938727, 14.8652395], 11);
    L.tileLayer('https://api.mapbox.com/styles/v1/{id}/tiles/{z}/{x}/{y}?access_token={accessToken}', {
        attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors, <a href="https://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="https://www.mapbox.com/">Mapbox</a>',
        maxZoom: 18,
        id: 'mapbox/streets-v11',
        accessToken: 'pk.eyJ1IjoieXVya2FuaW5qYWNrIiwiYSI6ImNrajd6NW02ZTBhNnIycXFzeGdwN24xaWsifQ.K042TG7mUswg466J2atjJg'
    }).addTo(mymap);

    markers = [];
    pathVertexs = [];
    path = null;

    mymap.on('click', function(e) {
        var popLocation= e.latlng;

        var xhttp = new XMLHttpRequest(); // jshint ignore: line
        xhttp.contentType = "application/json";
        xhttp.onreadystatechange = function () {
            if (xhttp.readyState === XMLHttpRequest.DONE) {
                if (xhttp.status === 200) {
                    //console.log("response: " + this.responseText);
                    var value = JSON.parse(this.responseText);
                    var marker = new L.marker(new L.LatLng(value.coordinate.latitude, value.coordinate.longitude));
                    marker.bindPopup("<b>Betweenness: </b>" + value.betweenness + "<br><b>OSMID: </b>" + value.osmid);
                    markers.push(marker);
                    showMarkers();
                    marker.openPopup();
                } else {
                    cleanMap();
                    alert("Server cannot satisfy this request!");
                }
            }
        };
        // console.log("open");

            cleanMap();
            var element = document.getElementById("serverLocationRest");
            if (element == null)
                element = document.getElementById("serverLocationWebSocket");
            var serverLocation = element.value;

            xhttp.open('GET','http://'+ serverLocation + baseURIRest + '/intersections/nearest?latitude='+e.latlng.lat+'&longitude='+e.latlng.lng,false);
            // console.log("send");
            xhttp.send();

    });
    // markers.push(new L.marker(new L.LatLng(45.69775, 4.7537935)));
    // markers.push(new L.marker(new L.LatLng(45.79775, 4.7537935)));
    //
    // showMarkers();
    //mymap.removeLayer(marker);
}
function showMarkers() {
    markers.forEach(function(value){
        mymap.addLayer(value);
    });

}

function cleanMarkers(){
    markers.forEach(function (value) {
        mymap.removeLayer(value);
    });
    markers=[];
}

function showPath() {

    // var latlngs = [
    //     [45.51, -122.68],
    //     [37.77, -122.43],
    //     [34.04, -118.2]
    // ];
    //
    // markers=[];
    // path = L.polyline(latlngs, {color: 'red'});

    path = L.polyline(pathVertexs, {color: 'red'});

    mymap.addLayer(path);
// zoom the map to the polyline
    mymap.fitBounds(path.getBounds());
}

function cleanPath(){
    pathVertexs = [];
    mymap.removeLayer(path);
    path = null;
}

function cleanMap(){
    if(path!=null)
        cleanPath();
    if(markers.length > 0)
        cleanMarkers();
}

function centerMap(lat, lon){
    mymap.panTo(new L.LatLng(lat, lon), 11);
}
