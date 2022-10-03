import React from "react";
import Grid from "@mui/material/Grid";
import RoutesSidebar from "./RoutesSidebar/RoutesSidebar";
import RouteContent from "./RouteContent/RouteContent";

const Routes = ({routes}) => {
    const [currentRoute, setCurrentRoute] = React.useState(routes[0]);

    return (<Grid container spacing={2} sx={{padding: '0 24px'}}>
        <Grid item xs={3}>
            <RoutesSidebar routes={routes} currentRoute={currentRoute} setCurrentRoute={setCurrentRoute}/>
        </Grid>
        <Grid item xs={9}>
            <RouteContent route = {currentRoute}/>
        </Grid>
    </Grid>);
}

export default Routes;