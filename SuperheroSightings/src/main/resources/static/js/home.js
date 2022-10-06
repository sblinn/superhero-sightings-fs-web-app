
/* NOTE: 
max bounds for Google Maps Latitude is +-85.
map will not display a location if out of bounds.
*/

// Initialize and add the map
function initMap() {

    if ($('#recent-sightings-latlngs').val().trim().length != 0) {
        var points = $('#recent-sightings-latlngs').val();

        points = points.slice(1, -1);
        
        var latLngs = points.split(",");
        
        var locations = [];
        for (let index = 0; index < latLngs.length; index++) {
            if (index % 2 == 0) {
                var x = latLngs[index];
                var y = latLngs[index + 1];
                locations.push(new google.maps.LatLng(x, y));
            }
        }

        // The map, centered at the coordinates, global
        const map = new google.maps.Map(document.getElementById("map"), {
            zoom: 0,
            center: { lat: 0.0000, lng: 0.0000 },
        });

        var infowindow = new google.maps.InfoWindow();

        var markers = [];

        var marker,i;
        
        for (i = 0; i < locations.length; i++) {  
            marker = new google.maps.Marker({
                position: new google.maps.LatLng(locations[i]),
                map
            });
            
            google.maps.event.addListener(marker, 'click', (function(marker, i) {
                return function() {
                    //infowindow.setContent(label);
                    //infowindow.open(map, marker);
                }
            }) (marker, i)); 

            markers.push(marker);
        }

    } else {

        var latitude = $('#selected-location-latitude').val();
        var longitude = $('#selected-location-longitude').val();

        var latLng = new google.maps.LatLng(latitude, longitude);

        // The map, centered at the coordinates, zoomed in
        const map = new google.maps.Map(document.getElementById("map"), {
            zoom: 10,
            center: latLng,
        });
        // The marker, positioned at coordinates
        const marker = new google.maps.Marker({
            position: latLng,
            map: map,
        });
    }

}


window.initMap = initMap;