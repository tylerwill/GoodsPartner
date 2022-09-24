import './App.css';
import Layout from "./components/Layout/Layout";
import CssBaseline from "@mui/material/CssBaseline";
import * as React from "react";
import CarsContainer from "./pages/Cars/CarsContainer";
import {Route, Routes} from "react-router-dom";
import OrdersContainer from "./pages/Orders/OrdersContainer";


function App() {
    return (
        <>
            <CssBaseline/>
            <Layout>
                <Routes>
                    <Route path="/new/cars" element={<CarsContainer/>}/>
                    <Route path="/new/orders" element={<OrdersContainer/>}/>
                </Routes>
            </Layout>
        </>
    );
}

export default App;
