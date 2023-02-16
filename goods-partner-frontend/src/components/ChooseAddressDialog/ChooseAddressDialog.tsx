import React, {FC, useCallback, useRef, useState} from 'react'
import {Button, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, TextField} from '@mui/material'
import {Autocomplete, GoogleMap, MarkerF} from '@react-google-maps/api'
import MapPoint from "../../model/MapPoint";
import {MapPointStatus} from "../../model/MapPointStatus";


interface ChooseAddressDialogProps {
    isOpen: boolean
    setIsOpen: (isOpen: boolean) => void
    onAction: (mapPoint: MapPoint) => void
    currentMapPoint: MapPoint
    defaultAddress: string
}

export const ChooseAddressDialog: FC<ChooseAddressDialogProps> = ({isOpen, setIsOpen, currentMapPoint, onAction, defaultAddress}) => {
    const handleClose = useCallback(() =>
            setIsOpen(false),
        [setIsOpen]
    )
    const address = currentMapPoint.status  === MapPointStatus.UNKNOWN ? defaultAddress : currentMapPoint.address;
    const [addressToUpdate, setAddressToUpdate] = useState(address);
    const [mapPoint, setMapPoint] = useState(currentMapPoint);

    const handleUpdateAddress = () => {
        onAction(mapPoint);
        handleClose()
    }

    const isValidAddress =
        mapPoint.status !== MapPointStatus.UNKNOWN

    const center = isValidAddress
        ? {
            lat: mapPoint.latitude,
            lng: mapPoint
                .longitude
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
        setMapPoint({...mapPoint, address: e.target.value});
        setAddressToUpdate(e.target.value);
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

        setMapPoint(newMapPoint)
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
                        setAddressToUpdate(formattedAddress);
                        setCoordinates(formattedAddress, location.lat(), location.lng())
                    }}
                >
                    <TextField
                        sx={{marginBottom: 2}}
                        autoFocus
                        margin='dense'
                        label='Адреса'
                        type='text'
                        fullWidth
                        variant='outlined'
                        value={addressToUpdate}
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
                    {isValidAddress && <MarkerF position={center}/>}
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
