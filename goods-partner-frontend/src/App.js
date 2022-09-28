import './App.css';
import Layout from "./components/Layout/Layout";
import CssBaseline from "@mui/material/CssBaseline";
import * as React from "react";
import CarsContainer from "./pages/Cars/CarsContainer";
import {Route, Routes} from "react-router-dom";
import DeliveriesContainer from "./pages/Deliveries/DeliveriesContainer";
import DeliveryContainer from "./pages/Delivery/DeliveryContainer";
import {useJsApiLoader} from "@react-google-maps/api";
import {Backdrop, createTheme} from "@mui/material";

const libraries = ['places'];



function App() {
    const key = process.env.REACT_APP_GOOGLE_MAPS_API_KEY;
    console.log("Loading with apiKey", key);
    //TODO: [UI] Set specific region
    const {isLoaded} = useJsApiLoader({
        id: 'google-map-script',
        googleMapsApiKey: key,
        libraries:libraries
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
                    <Route path="/cars" element={<CarsContainer/>}/>
                    <Route path="/deliveries" element={<DeliveriesContainer/>}/>
                    <Route path="/delivery/:id" element={<DeliveryContainer/>}/>
                </Routes>
            </Layout>
        </>
    );
}

export default App;
