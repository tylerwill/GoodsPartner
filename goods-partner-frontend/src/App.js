import './App.css';
import Layout from "./newUI/components/Layout/Layout";
import CssBaseline from "@mui/material/CssBaseline";
import * as React from "react";
import CarsContainer from "./newUI/pages/Cars/CarsContainer";
import {Route, Routes} from "react-router-dom";


function App() {
    return (
        <>
            <CssBaseline/>
            <Layout>
                <Routes>
                    <Route path="/new/cars" element={<CarsContainer/>}/>
                </Routes>
            </Layout>
        </>
    );
}

export default App;
