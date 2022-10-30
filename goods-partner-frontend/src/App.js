import Layout from "./components/Layout/Layout";
import CssBaseline from "@mui/material/CssBaseline";
import * as React from "react";
import {Route, Routes, useParams} from "react-router-dom";
import {useJsApiLoader} from "@react-google-maps/api";
import {Backdrop} from "@mui/material";
import Cars from "./pages/Cars/Cars";
import Deliveries from "./pages/Deliveries/Deliveries";
import Reports from "./pages/Reports/Reports";
import Delivery from "./pages/Delivery/Delivery";
import Users from "./pages/Users/Users";
import {useEffect} from "react";
import {currentHost} from "./util/util";
import {useSnackbar} from 'notistack'
import {useDispatch, useSelector} from "react-redux";
import {approveDelivery, calculateDelivery, fetchDelivery} from "./features/currentDelivery/currentDeliverySlice";

const libraries = ['places'];


function App() {
    const {enqueueSnackbar, closeSnackbar} = useSnackbar()
    const dispatch = useDispatch();
    const {delivery} = useSelector(state => state.currentDelivery);

    useEffect(() => {
        const sse = new EventSource(`${currentHost()}api/v1/live-event`);

        function getRealtimeData(data) {
            console.log('data', data);
            enqueueSnackbar(data.message, {variant: data.type === 'INFO' ? 'default' : data.type.toLowerCase()})

            if (data.action?.type === 'DELIVERY_UPDATED') {
                if (delivery && delivery.id === data.action.deliveryId) {
                    dispatch(fetchDelivery(delivery.id));
                }
            }
            // process the data here,
            // then pass it to state to be rendered
        }

        sse.onmessage = e => getRealtimeData(JSON.parse(e.data));
        sse.onerror = (error) => {
            // error log here
            console.log("error in live events", error);
            sse.close();
        }
        return () => {
            sse.close();
        };
    }, []);

    const key = process.env.REACT_APP_GOOGLE_MAPS_API_KEY;
    console.log("Loading with apiKey", key);

    //TODO: [UI] check if we are using specific region
    const {isLoaded} = useJsApiLoader({
        id: 'google-map-script',
        googleMapsApiKey: key,
        libraries: libraries,
        region: 'UA'
    })

    // TODO: Temp solution, render our app only if google maps loaded.
    if (!isLoaded) {
        return <Backdrop
            sx={{color: '#fff', zIndex: (theme) => theme.zIndex.drawer + 1}}
            open={true}
        />
    }


    return (
        <>
            <CssBaseline/>
            <Layout>
                <Routes>
                    <Route path="/cars" element={<Cars/>}/>
                    <Route path="/users" element={<Users/>}/>
                    <Route path="/deliveries" element={<Deliveries/>}/>
                    <Route path="/delivery/:id" element={<Delivery/>}/>
                    <Route path="/reports/" element={<Reports/>}/>
                </Routes>
            </Layout>
        </>
    );
}

export default App;
