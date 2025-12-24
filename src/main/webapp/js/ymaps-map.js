window.addEventListener("DOMContentLoaded", function() {
    var el = document.getElementById('map-data');
    var lat = parseFloat(el.dataset.lat);
    var lng = parseFloat(el.dataset.lng);
    var name = el.dataset.name || '';
    var address = el.dataset.address || '';
    ymaps.ready(function () {
        var map = new ymaps.Map('map', {
            center: [lat, lng], zoom: 16, controls: ['zoomControl']
        });
        map.geoObjects.add(new ymaps.Placemark(
            [lat, lng],
            {balloonContent: name + '<br/>' + address},
            {preset: "islands#redDotIcon"}
        ));
    });
});
