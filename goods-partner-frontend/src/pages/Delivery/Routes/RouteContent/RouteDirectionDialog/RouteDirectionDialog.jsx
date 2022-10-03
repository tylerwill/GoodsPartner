import React, {useRef} from 'react';
import {Button, Dialog, DialogActions, DialogContent, DialogTitle} from "@mui/material";
import {GoogleMap} from "@react-google-maps/api";

const RouteDirectionDialog = ({open, setOpen, routePoints}) => {
    let center;

    center = {
        lat: 50.4520355,
        lng: 30.53269055
    };

    const mapRef = useRef(undefined);

    const containerStyle = {
        width: '640px',
        height: '400px'
    };

    const onLoad = React.useCallback(function callback(map) {
        mapRef.current = map;
    }, [])

    const onUnmount = React.useCallback(function callback(map) {
        mapRef.current = undefined;
    }, [])

    return (<Dialog maxWidth={'680px'}
                    open={open} onClose={() => setOpen(false)}>
        <DialogTitle>Маршрут</DialogTitle>
        <DialogContent>
            <GoogleMap center={center} zoom={12} onLoad={onLoad} onUnmount={onUnmount}
                       mapContainerStyle={containerStyle}
                       options={{
                           streetViewControl: false,
                           mapTypeControl: false
                       }}
            >

            </GoogleMap>
        </DialogContent>
        <DialogActions>
            <Button onClick={() => setOpen(false)}>Скасувати</Button>
        </DialogActions>
    </Dialog>);
}