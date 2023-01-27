import Layout from './components/Layout/Layout'
import CssBaseline from '@mui/material/CssBaseline'
import {Route, Routes} from 'react-router-dom'
import {useJsApiLoader} from '@react-google-maps/api'
import {Backdrop} from '@mui/material'
import Deliveries from './pages/Deliveries/Deliveries'
import Reports from './pages/Reports/Reports'
import Delivery from './pages/Delivery/Delivery'
import Users from './pages/Users/Users'
import Notifications from './hoc/Notifications/Notifications'
import {Orders as DeliveryOrders} from './pages/Delivery/Orders/Orders'
import {Orders as OrdersPage} from './pages/Orders/Orders'
import {Routes as CarRoutes} from './pages/Delivery/Routes/Routes'
import Cars from './pages/Cars/Cars'
import History from "./pages/Delivery/History/History";
import React from "react";
import Shipping from "./pages/Delivery/Shipping/Shipping";

const libraries = ['places']

function App() {
    const key = process.env.REACT_APP_GOOGLE_MAPS_API_KEY;
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
        <Notifications>
            <CssBaseline/>
            <Layout>
                <Routes>
                    <Route path='/cars' element={<Cars/>}/>
                    <Route path='/users' element={<Users/>}/>
                    <Route path='/deliveries' element={<Deliveries/>}/>
                    <Route path='/' element={<Deliveries/>}/>

                    <Route path='/delivery/:deliveryId' element={<Delivery/>}>
                        <Route index element={<DeliveryOrders/>}/>
                        <Route path='history' element={<History/>}/>
                        <Route path='orders' element={<DeliveryOrders/>}/>
                        <Route path='routes' element={<CarRoutes/>}/>
                        <Route path='shipping' element={<Shipping/>}/>
                    </Route>

                    <Route path='/reports/' element={<Reports/>}/>
                    <Route path='/orders/' element={<OrdersPage/>}/>
                </Routes>
            </Layout>
        </Notifications>
    )
}

export default App
