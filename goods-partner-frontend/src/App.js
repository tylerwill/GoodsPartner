import './App.css';
import Layout from "./components/Layout/Layout";
import CssBaseline from "@mui/material/CssBaseline";
import * as React from "react";
import CarsContainer from "./pages/Cars/CarsContainer";
import {Route, Routes} from "react-router-dom";
import OrdersContainer from "./pages/Orders/OrdersContainer";
import DeliveriesContainer from "./pages/Deliveries/DeliveriesContainer";


function App() {
    return (
        <>
            <CssBaseline/>
            <Layout>
                <Routes>
                    <Route path="/cars" element={<CarsContainer/>}/>
                    <Route path="/orders" element={<OrdersContainer/>}/>
                    <Route path="/deliveries" element={<DeliveriesContainer/>}/>
                </Routes>
            </Layout>
        </>
    );
}

export default App;
