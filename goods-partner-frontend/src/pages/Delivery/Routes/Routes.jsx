import React from "react";
import Grid from "@mui/material/Grid";
import RoutesSidebar from "./RoutesSidebar/RoutesSidebar";
import RouteContent from "./RouteContent/RouteContent";

const Routes = ({deliveryDate, routes, updateRoutePoint, updateRoute, getOrderById}) => {
    const [currentRouteIndex, setCurrentRouteIndex] = React.useState(0);
    const currentRoute = routes[currentRouteIndex];

    return (<Grid container spacing={2} sx={{padding: '0 24px'}}>
        <Grid item xs={3}>
            <RoutesSidebar routes={routes} currentRoute={currentRoute} setCurrentRouteIndex={setCurrentRouteIndex}/>
        </Grid>
        <Grid item xs={9}>
            <RouteContent deliveryDate={deliveryDate} updateRoute={updateRoute}
                          updateRoutePoint={updateRoutePoint} route={currentRoute}
                          getOrderById={getOrderById}/>
        </Grid>
    </Grid>);
}

export default Routes;