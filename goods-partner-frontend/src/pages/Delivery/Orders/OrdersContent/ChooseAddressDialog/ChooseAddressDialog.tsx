import React, { useCallback, useRef, useState } from 'react'
import {
	Button,
	Dialog,
	DialogActions,
	DialogContent,
	DialogContentText,
	DialogTitle,
	TextField
} from '@mui/material'
import { Autocomplete, GoogleMap, MarkerF } from '@react-google-maps/api'
import { MapPointStatus } from '../../../../../model/MapPointStatus'
import {
	useAppDispatch,
	useAppSelector
} from '../../../../../hooks/redux-hooks'
import { setAddressDialogOpen } from '../../../../../features/delivery-orders/deliveryOrdersSlice'
import { useUpdateOrderMutation } from '../../../../../api/delivery-orders/delivery-orders.api'
import Order from '../../../../../model/Order'
import MapPoint from '../../../../../model/MapPoint'

const ChooseAddressDialog = () => {
	const dispatch = useAppDispatch()
	const order = useAppSelector(
		state => state.deliveryOrders.orderForModification
	)
	const isOpen = useAppSelector(
		state => state.deliveryOrders.isOrderAddressDialogOpen
	)

	const [updateOrder] = useUpdateOrderMutation()

	const handleClose = useCallback(
		() => dispatch(setAddressDialogOpen(false)),
		[dispatch, setAddressDialogOpen]
	)

	const [orderToUpdate, setOrderToUpdate] = useState<Order>(order)
	const address = orderToUpdate.mapPoint.status  === 'UNKNOWN' ? orderToUpdate.address : orderToUpdate.mapPoint.address;

	const handleUpdateAddress = () => {
		updateOrder(orderToUpdate)
		handleClose()
	}

	const isValidAddress =
		orderToUpdate.mapPoint.status !== MapPointStatus.UNKNOWN

	const center = isValidAddress
		? {
				lat: orderToUpdate.mapPoint.latitude,
				lng: orderToUpdate.mapPoint.longitude
		  }
		: {
				// Kiev center
				lat: 50.4520355,
				lng: 30.53269055
		  }

	const mapRef = useRef<any>(undefined)
	const autocompleteRef = useRef<any>(undefined)

	const containerStyle = {
		width: '640px',
		height: '400px'
	}

	const handleAddressChange = (e: React.ChangeEvent<HTMLInputElement>) => {
		setOrderToUpdate({ ...orderToUpdate, address: e.target.value })
	}

	// Todo: debouce auto complete
	// https://blog.woosmap.com/implement-and-optimize-autocomplete-with-google-places-api
	const setCoordinates = (
		formattedAddress: string,
		lat: number,
		lng: number
	) => {
		const newMapPoint = {
			latitude: lat,
			longitude: lng,
			address: formattedAddress,
			status: MapPointStatus.KNOWN
		} as MapPoint

		const newOrderInfo = {
			...orderToUpdate,
			address: formattedAddress,
			mapPoint: newMapPoint
		} as Order

		setOrderToUpdate(newOrderInfo)
	}

	const onAutocompleteLoad = useCallback((autocomplete: any) => {
		autocompleteRef.current = autocomplete
	}, [])

	const onLoad = useCallback((map: any) => {
		mapRef.current = map
	}, [])

	// @ts-ignore
	return (
		<Dialog maxWidth={'lg'} open={isOpen} onClose={handleClose}>
			<DialogTitle>Редагувати адресу</DialogTitle>
			<DialogContent>
				<DialogContentText>
					Введіть адресу власноруч або оберіть точку на карті:
				</DialogContentText>
				<Autocomplete
					onLoad={onAutocompleteLoad}
					onPlaceChanged={() => {
						const place = autocompleteRef.current.getPlace()
						if (!place) {
							return
						}
						const formattedAddress = place.formatted_address
						const location = place.geometry.location
						setCoordinates(formattedAddress, location.lat(), location.lng())
					}}
				>
					<TextField
						sx={{ marginBottom: 2 }}
						autoFocus
						margin='dense'
						label='Адреса'
						type='text'
						fullWidth
						variant='outlined'
						value={address}
						onChange={handleAddressChange}
					/>
				</Autocomplete>

				<GoogleMap
					center={center}
					zoom={12}
					onLoad={onLoad}
					mapContainerStyle={containerStyle}
					options={{
						streetViewControl: false,
						mapTypeControl: false
					}}
				>
					{isValidAddress && <MarkerF position={center} />}
				</GoogleMap>
			</DialogContent>
			<DialogActions>
				<Button onClick={handleClose}>Скасувати</Button>
				<Button onClick={handleUpdateAddress} disabled={!isValidAddress}>
					Зберегти
				</Button>
			</DialogActions>
		</Dialog>
	)
}

export default ChooseAddressDialog
