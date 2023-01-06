import Layout from "./components/Layout/Layout";
import CssBaseline from "@mui/material/CssBaseline";
import * as React from "react";
import {Route, Routes} from "react-router-dom";
import {useJsApiLoader} from "@react-google-maps/api";
import {Backdrop} from "@mui/material";
import Deliveries from "./pages/Deliveries/Deliveries";
import Reports from "./pages/Reports/Reports";
import Delivery from "./pages/Delivery/Delivery";
import Users from "./pages/Users/Users";
import Notifications from "./hoc/Notifications/Notifications";
import Orders from "./pages/Orders/Orders";
import Cars from "./pages/Cars/Cars";

const libraries = ['places'];


function App() {
    const key = process.env.REACT_APP_GOOGLE_MAPS_API_KEY;
    console.log(key);

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
        <Notifications>
            <CssBaseline/>
            <Layout>
                <Routes>
                    <Route path="/cars" element={<Cars/>}/>
                    <Route path="/users" element={<Users/>}/>
                    <Route path="/deliveries" element={<Deliveries/>}/>
                    <Route path="/" element={<Deliveries/>}/>
                    <Route path="/delivery/:deliveryId" element={<Delivery/>}/>
                    <Route path="/reports/" element={<Reports/>}/>
                    <Route path="/orders/" element={<Orders/>}/>
                </Routes>
            </Layout>
        </Notifications>
    );
}

export default App;
