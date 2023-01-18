import React, { useEffect, useRef, useState } from 'react'
import {
	Button,
	Dialog,
	DialogActions,
	DialogContent,
	DialogTitle
} from '@mui/material'
import { GoogleMap, MarkerF } from '@react-google-maps/api'

function centerMap(mapRef, lngs, lats) {
	mapRef.current.fitBounds({
		west: Math.min.apply(null, lngs),
		east: Math.max.apply(null, lngs),
		north: Math.min.apply(null, lats),
		south: Math.max.apply(null, lats)
	})
}

const RouteMapDialog = ({ route, open, closeDialog }) => {
	const mapRef = useRef(undefined)
	const [isMapLoaded, setIsMapLoaded] = useState(false)

	const waypoints = toGoogleWaypoints(route)
	const waypointsParts = splitWaypoints(waypoints)

	// Zoom and center map automatically by stations (each station will be in visible map area)
	const lngs = waypoints.map(function (station) {
		return station.lng
	})
	const lats = waypoints.map(function (station) {
		return station.lat
	})

	const renderDirections = (response, status) => {
		if (status !== 'OK') {
			console.log('Directions request failed due to ', status)
			return
		}
		// eslint-disable-next-line no-undef
		const renderer = new google.maps.DirectionsRenderer()
		renderer.setMap(mapRef.current)
		renderer.setOptions({ suppressMarkers: true, preserveViewport: true })
		renderer.setDirections(response)
		centerMap(mapRef, lngs, lats)
	}

	useEffect(() => {
		if (isMapLoaded) {
			calculateRoute(waypointsParts, renderDirections)
		}
	}, [isMapLoaded])

	const containerStyle = {
		width: '1150px',
		height: '700px'
	}

	const onLoad = React.useCallback(function callback(map) {
		mapRef.current = map
		setIsMapLoaded(true)
	}, [])

	const onUnmount = React.useCallback(function callback(map) {
		mapRef.current = undefined
		setIsMapLoaded(false)
	}, [])

	return (
		<Dialog fullWidth={true} maxWidth={'lg'} open={open} onClose={closeDialog}>
			<DialogTitle>Маршрут</DialogTitle>
			<DialogContent>
				<GoogleMap
					onLoad={onLoad}
					onUnmount={onUnmount}
					mapContainerStyle={containerStyle}
					center={{
						lat: 50.4520355,
						lng: 30.53269055
					}}
					zoom={12}
					options={{
						streetViewControl: false,
						mapTypeControl: false
					}}
				>
					{generateMarkersForWaypoints(waypoints)}
				</GoogleMap>
			</DialogContent>
			<DialogActions>
				<Button onClick={closeDialog}>Закрити</Button>
			</DialogActions>
		</Dialog>
	)
}

function generateMarkersForWaypoints(waypoints) {
	return waypoints.map((waypoint, index) => (
		<MarkerF
			label={generateLabelIndex(index, waypoints.length)}
			key={'waypoint ' + waypoint.name + index}
			position={waypoint}
			title={waypoint.name}
		/>
	))
}

function generateLabelIndex(index, amount) {
	if (index === 0 || index === amount - 1) {
		return ''
	}
	return String(index)
}

function toGoogleWaypoints(route) {
	const { routePoints } = route
	const googleWaypoints = routePoints.map(point => {
		const mapPoint = point.mapPoint
		return {
			lat: mapPoint.latitude,
			lng: mapPoint.longitude,
			name: point.address
		}
	})

	const storeMapPoint = route.store.mapPoint
	const storeWaypoint = {
		lat: storeMapPoint.latitude,
		lng: storeMapPoint.longitude,
		name: route.store.name
	}

	// store is the first and the last point on the way
	googleWaypoints.unshift(storeWaypoint)
	googleWaypoints.push(storeWaypoint)

	return googleWaypoints
}

function splitWaypoints(waypoints) {
	const waypointsParts = []
	for (let i = 0, max = 25 - 1; i < waypoints.length; i = i + max) {
		waypointsParts.push(waypoints.slice(i, i + max + 1))
	}
	return waypointsParts
}

function calculateRoute(waypointsParts, callback) {
	// eslint-disable-next-line no-undef
	const directionService = new google.maps.DirectionsService()

	for (var i = 0; i < waypointsParts.length; i++) {
		// Waypoints does not include first station (origin) and last station (destination)
		var waypoints = []
		for (var j = 1; j < waypointsParts[i].length - 1; j++) {
			const current = waypointsParts[i][j]
			waypoints.push({ location: current, stopover: false })
		}

		const origin = waypointsParts[i][0]
		const destination = waypointsParts[i][waypointsParts[i].length - 1]

		directionService.route(
			{
				origin: origin,
				destination: destination,
				// eslint-disable-next-line no-undef
				travelMode: google.maps.TravelMode.DRIVING,
				// waypoints: [{location: "м. Київ, вул. Хрещатик, 5", stopover: true}, {location: "м. Київ, вул. Сергія Данченка, 5", stopover: true}]
				waypoints: waypoints
				// optimizeWaypoints: true
			},
			callback
		)
	}
}

export default RouteMapDialog
