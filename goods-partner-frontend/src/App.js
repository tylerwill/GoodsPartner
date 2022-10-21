import './App.css';
import Layout from "./components/Layout/Layout";
import CssBaseline from "@mui/material/CssBaseline";
import * as React from "react";
import {Route, Routes} from "react-router-dom";
import DeliveryContainer from "./pages/Delivery/DeliveryContainer";
import {useJsApiLoader} from "@react-google-maps/api";
import {Backdrop} from "@mui/material";
import Cars from "./pages/Cars/Cars";
import Deliveries from "./pages/Deliveries/Deliveries";
import Reports from "./pages/Reports/Reports";

const libraries = ['places'];


function App() {
    const key = process.env.REACT_APP_GOOGLE_MAPS_API_KEY;
    console.log("Loading with apiKey", key);
    //TODO: [UI] Set specific region
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
                    <Route path="/deliveries" element={<Deliveries/>}/>
                    <Route path="/delivery/:deliveryId" element={<DeliveryContainer/>}/>
                    <Route path="/reports/" element={<Reports/>}/>
                </Routes>
            </Layout>
        </>
    );
}

export default App;
