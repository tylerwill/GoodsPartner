import './App.css';
import Layout from "./components/Layout/Layout";
import CssBaseline from "@mui/material/CssBaseline";
import * as React from "react";
import CarsContainer from "./pages/Cars/CarsContainer";
import {Route, Routes} from "react-router-dom";
import OrdersContainer from "./pages/Orders/OrdersContainer";
import DeliveriesContainer from "./pages/Deliveries/DeliveriesContainer";
import DeliveryContainer from "./pages/Delivery/DeliveryContainer";


function App() {
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
