import React, {useCallback} from "react";
import Grid from "@mui/material/Grid";
import RoutesSidebar from "./RoutesSidebar/RoutesSidebar";
import RouteContent from "./RouteContent/RouteContent";
import {useDispatch, useSelector} from "react-redux";
import {
    setCurrentRouteIndex,
    updateRoutePointStatus,
    updateRouteStatus
} from "../../../features/currentDelivery/currentDeliverySlice";

const Routes = () => {
    const dispatch = useDispatch();
    const {delivery, currentRouteIndex} = useSelector(state => state.currentDelivery);
    const {deliveryDate, routes} = delivery;

    const currentRoute = routes[currentRouteIndex];

    const setCurrentRouteIndexHandler = (index) => dispatch(setCurrentRouteIndex(index));

    const updateRouteStatusHandler = useCallback((routeId, action) =>
        dispatch(updateRouteStatus({routeId, action})), [dispatch]);

    const updateRoutePointStatusHandler = useCallback((routeId, routePointId, action) =>
        dispatch(updateRoutePointStatus({routeId, routePointId, action})), [dispatch]);

    return (<Grid container spacing={2} sx={{padding: '0 24px'}}>
        <Grid item xs={3}>
            <RoutesSidebar routes={routes} currentRoute={currentRoute} setCurrentRouteIndex={setCurrentRouteIndexHandler}/>
        </Grid>
        <Grid item xs={9}>
            <RouteContent deliveryDate={deliveryDate}
                          route={currentRoute}
                          updateRoute={updateRouteStatusHandler}
                          updateRoutePoint={updateRoutePointStatusHandler}
            />
        </Grid>
    </Grid>);
}

export default Routes;