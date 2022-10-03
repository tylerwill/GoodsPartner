import React, {useRef, useState} from "react";
import {Button, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, TextField} from "@mui/material";
import {Autocomplete, GoogleMap, MarkerF} from "@react-google-maps/api";

// TODO: Maybe we should use it as hoc in future
const ChooseAddressDialog = ({order, open, handleClose, updatePreviewOrderAddress}) => {
    const {mapPoint} = order;

    const [orderAddress, setOrderAddress] = useState({
        refKey: order.refKey,
        address: order.address,
        mapPoint: {
            ...mapPoint
        }
    });

    const isValidAddress = orderAddress.mapPoint.status !== 'UNKNOWN';

    let center;

    if (!isValidAddress) {
        // Kiev coordinates
        center = {
            lat: 50.4520355,
            lng: 30.53269055
        };
    } else {
        center = {
            lat: orderAddress.mapPoint.latitude,
            lng: orderAddress.mapPoint.longitude
        }
    }

    const mapRef = useRef(undefined);
    const autocompleteRef = useRef(undefined);

    const containerStyle = {
        width: '640px',
        height: '400px'
    };

    const handleAddressChange = (e) => {
        setOrderAddress({...orderAddress, address: e.target.value});
    }

    const setCoordinates = (formattedAddress, lat, lng) => {
        const newMapPoint = {
            latitude: lat,
            longitude: lng,
            address: formattedAddress,
            status: "AUTOVALIDATED"
        }

        console.log("new map point", newMapPoint);
        const newOrderAddress = {
            ...orderAddress,
            address: formattedAddress,
            mapPoint: newMapPoint
        };
        setOrderAddress(newOrderAddress);

    }

    const onAutocompleteLoad = React.useCallback(function callback(autocomplete) {
        autocompleteRef.current = autocomplete;
    }, [])

    const onAutocompleteUnmount = React.useCallback(function callback(map) {
        autocompleteRef.current = undefined;
    }, [])

    const onLoad = React.useCallback(function callback(map) {
        mapRef.current = map;
    }, [])

    const onUnmount = React.useCallback(function callback(map) {
        mapRef.current = undefined;
    }, [])

    return (<Dialog maxWidth={'680px'}
                    open={open} onClose={handleClose}>
        <DialogTitle>Редагувати адресу</DialogTitle>
        <DialogContent>
            <DialogContentText>
                Введіть адресу власноруч або оберіть точку на карті:
            </DialogContentText>
            <Autocomplete
                onLoad={onAutocompleteLoad}
                onUnmount={onAutocompleteUnmount}
                onPlaceChanged={(e) => {
                    const place = autocompleteRef.current.getPlace();
                    if (!place) {
                        return;
                    }
                    console.log("onChange", place);
                    const formattedAddress = place.formatted_address;
                    const location = place.geometry.location;
                    setCoordinates(formattedAddress, location.lat(), location.lng());
                }}

            >
                <TextField sx={{marginBottom: 2}} autoFocus margin="dense" label="Адреса" type="text"
                           fullWidth variant="outlined" value={orderAddress.address} onChange={handleAddressChange}/>

            </Autocomplete>

            <GoogleMap center={center} zoom={12} onLoad={onLoad} onUnmount={onUnmount}
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
            <Button onClick={() => {updatePreviewOrderAddress(orderAddress); handleClose();}} disabled={!isValidAddress}>Зберегти</Button>
        </DialogActions>
    </Dialog>);
}

export default ChooseAddressDialog;