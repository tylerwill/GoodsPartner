import Container from "@mui/material/Container";
import Box from "@mui/material/Box";
import Grid from "@mui/material/Grid";
import DateChooserCard from "./DateChooserCard/DateChooserCard";
import BasicTabs from "./BasicTabs/BasicTabs";
import React from "react";
import {Route, Routes} from "react-router-dom";
import Cars from "./Cars/Cars";

const Calculate = ({orders, routes, getCalculatedDataByDate, routeAddresses, changeRoutePointStatus,
                       changeRouteStatus,updateRoute}) => {
    return (
        <div className="App">
            <Container>
                <Box sx={{flexGrow: 1}}>
                    <Grid container spacing={3} direction="column">
                        <Grid item xs={2}>
                            <DateChooserCard
                                getCalculatedDataByDate={getCalculatedDataByDate}
                            />
                        </Grid>
                        <Grid item xs={10}>
                            <Routes>
                                <Route path="/" exact element={<BasicTabs
                                    orders={orders}
                                    routes={routes}
                                    routeAddresses={routeAddresses}
                                    changeRoutePointStatus = {changeRoutePointStatus}
                                    changeRouteStatus = {changeRouteStatus}
                                    updateRoute = {updateRoute}
                                />}/>
                                <Route path="/cars" element={<Cars/>}/>
                            </Routes>

                        </Grid>
                    </Grid>
                </Box>
            </Container>
        </div>
    );
}

export default Calculate;