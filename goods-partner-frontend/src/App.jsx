import Layout from './components/Layout/Layout'
import CssBaseline from '@mui/material/CssBaseline'
import {Navigate, Outlet, Route, Routes} from 'react-router-dom'
import {useJsApiLoader} from '@react-google-maps/api'
import {Backdrop} from '@mui/material'
import Deliveries from './pages/Deliveries/Deliveries'
import Reports from './pages/Reports/Reports'
import Delivery from './pages/Delivery/Delivery'
import Users from './pages/Users/Users'
import Notifications from './hoc/Notifications/Notifications'
import {DeliveryOrders as DeliveryOrders} from './pages/Delivery/DeliveryOrders/DeliveryOrders'
import {Orders as OrdersPage} from './pages/Orders/Orders'
import {DeliveryRoutes as CarRoutes} from './pages/Delivery/DeliveryRoutes/DeliveryRoutes'
import Cars from './pages/Cars/Cars'
import DeliveryHistory from "./pages/Delivery/DeliveryHistory/DeliveryHistory";
import React from "react";
import DeliveryShipping from "./pages/Delivery/DeliveryShipping/DeliveryShipping";
import {Clients} from "./pages/Clients/Clients";
import {Tasks} from "./pages/Tasks/Tasks";
import {Settings} from "./pages/Settings/Settings";
import useAuth from "./auth/AuthProvider";
import {Login} from "./pages/Login/Login";

const libraries = ['places']

function App() {
    const key = import.meta.env.VITE_GOOGLE_MAPS_API_KEY;
    console.log(key)

    //TODO: [UI] check if we are using specific region
    const {isLoaded} = useJsApiLoader({
        id: 'google-map-script',
        googleMapsApiKey: key,
        libraries: libraries,
        region: 'UA'
    })

    // TODO: Temp solution, render our app only if google maps loaded.
    if (!isLoaded) {
        return (
            <Backdrop
                sx={{color: '#fff', zIndex: theme => theme.zIndex.drawer + 1}}
                open={true}
            />
        )
    }

    return (
        <Routes>
            <Route path='/login' element={<Login/>}/>
            <Route element={<ProtectedRoute/>}>

                <Route path='/cars' element={<Cars/>}/>
                <Route path='/users' element={<Users/>}/>
                <Route path='/clients/addresses' element={<Clients/>}/>
                <Route path='/deliveries' element={<Deliveries/>}/>
                <Route path='/tasks' element={<Tasks/>}/>
                <Route path='/settings' element={<Settings/>}/>
                <Route path='/' element={<Deliveries/>}/>

                <Route path='/delivery/:deliveryId' element={<Delivery/>}>
                    <Route index element={<DeliveryOrders/>}/>
                    <Route path='history' element={<DeliveryHistory/>}/>
                    <Route path='orders' element={<DeliveryOrders/>}/>
                    <Route path='routes' element={<CarRoutes/>}/>
                    <Route path='shipping' element={<DeliveryShipping/>}/>
                </Route>

                <Route path='/reports/' element={<Reports/>}/>
                <Route path='/orders/' element={<OrdersPage/>}/>
            </Route>
        </Routes>
    )
}


const ProtectedRoute = ({redirectPath = '/login'}) => {
    const {user} = useAuth();
    if (!user) {
        return <Navigate to={redirectPath} replace/>;
    }

    return <Notifications>
        <CssBaseline/>
        <Layout>
            <Outlet/>
        </Layout>
    </Notifications>;
};

export default App
