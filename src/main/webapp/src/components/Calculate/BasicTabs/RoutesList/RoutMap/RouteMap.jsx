import React, {useState} from "react";
import {DirectionsRenderer, GoogleMap, Marker} from "@react-google-maps/api";
import {Button} from "@mui/material";
import Geocode from "react-geocode";

async function getLatLng(address) {
  Geocode.setApiKey("AIzaSyD0M9GvjU-nzoPjTWJCsVuG4jDyAvqHvHc");
  Geocode.setLanguage("uk");
  Geocode.setRegion("es");
  Geocode.setLocationType("ROOFTOP");
  Geocode.enableDebug();

  let promise = Geocode.fromAddress(address).then(
    (response) => {
      const {lat, lng} = response.results[0].geometry.location;
      return {lat, lng};
    },
    (error) => {
      console.error(error);
      return {zxc: "qqq"}
    }
  );
  return await promise;
}

const RouteMap = ({route}) => {

  const center = {
    lat: 50.449217392381776,
    lng: 30.450244577069448
  }
  const [map, setMap] = useState(/** @type google.maps.Map*/null);
  const [directionsResponse, setDirectionsResponse] = useState(null)
  const [distance, setDistance] = useState("")
  const [duration, setDuration] = useState("")
  const [marker, setMarker] = useState(center)

  async function calculateRoute(route) {
    debugger
    // eslint-disable-next-line no-undef
    const directionService = new google.maps.DirectionsService()
    // const bool = route[0].addresses.length >= 2
    //   ? await directionService.route({
    //     origin: route.addresses.shift(),
    //     destination: route.addresses.pop(),
    //     // eslint-disable-next-line no-undef
    //     travelMode: google.maps.TravelMode.DRIVING,
    //     waypoints: route
    //   })
    //   : await directionService.route({
    //     origin: route.addresses.shift(),
    //     destination: route.addresses.pop(),
    //     // eslint-disable-next-line no-undef
    //     travelMode: google.maps.TravelMode.DRIVING
    //   })
    const origin = route[0].addresses[0];
    const dest = route[0].addresses[route[0].addresses.length - 1];
    getLatLng(origin).then(resp => setMarker(resp))
    debugger
    const result = await directionService.route({
      origin: origin,
      destination: dest,
      // eslint-disable-next-line no-undef
      travelMode: google.maps.TravelMode.DRIVING
      // waypoints: {location: route[0].addresses}
    })
    setDirectionsResponse(result)
    setDistance(result.routes[0].legs[0].distance.text)
    setDuration(result.routes[0].legs[0].duration.text)
  }

  debugger

  return <div style={
    {
      width: 835,
      height: 600
    }
  }>
    <div>
      <Button onClick={() => map.panTo(marker)}>Go to start point</Button>
      <Button onClick={() => calculateRoute(route)}>build</Button>
    </div>
    <div>
      Distance: {distance}
      Duration: {duration}
    </div>
    <GoogleMap
      center={marker}
      zoom={15}
      mapContainerStyle={
        {
          width: '100%',
          height: '95%'
        }
      }
      onLoad={(map) => setMap(map)}
    >
      <Marker position={center}/>
      {
        directionsResponse && <DirectionsRenderer directions={directionsResponse}/>
      }
    </GoogleMap>
  </div>
}
export default RouteMap;