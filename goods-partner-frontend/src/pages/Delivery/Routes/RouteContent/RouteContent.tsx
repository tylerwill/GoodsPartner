import React from "react";
import {ListItem, ListSubheader, Typography} from "@mui/material";
import List from "@mui/material/List";
import Divider from "@mui/material/Divider";
import RouteDetails from "./RouteDetails/RouteDetails";
import RoutePointDetails from "./RoutePointDetails/RoutePointDetails";
import {Route} from "../../../../model/Route";

interface RouteContentProps {
    deliveryDate: string,
    route: Route,
}

const RouteContent = ({deliveryDate, route}: RouteContentProps) => {

    return <List
        sx={{
            width: '100%', minHeight: '80vh',
            bgcolor: 'background.paper',
            border: '1px solid rgba(0, 0, 0, 0.1)',
            borderRadius: '6px'
        }}
        component="nav"
        aria-labelledby="nested-list-subheader"
        subheader={
            <ListSubheader sx={{fontWeight: 'bold'}} component="h3" id="nested-list-subheader">
                Деталі маршруту
            </ListSubheader>
        }
    >
        <Divider/>
        <ListItem>
            <RouteDetails route={route} deliveryDate={deliveryDate}/>
        </ListItem>
        <ListItem>
            <Typography component={'h3'} sx={{fontSize: '14px', fontWeight: 'bold'}}>
                Пункти призначення
            </Typography>
        </ListItem>
        {route.routePoints.map((routePoint, index) =>
            <ListItem
                key={routePoint.id}>
                <RoutePointDetails
                    routePoint={routePoint} orderNumber={index + 1}/>
            </ListItem>)}
    </List>
}

export default RouteContent;