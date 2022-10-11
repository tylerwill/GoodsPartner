import React from "react";
import {ListItem, ListItemText, ListSubheader} from "@mui/material";
import List from "@mui/material/List";
import Divider from "@mui/material/Divider";
import ListItemButton from "@mui/material/ListItemButton";

const RoutesSidebar = ({routes, currentRoute, setCurrentRouteIndex}) => {
    return (
        <List
            sx={{
                width: '100%', height: '80vh',
                bgcolor: 'background.paper',
                border: '1px solid rgba(0, 0, 0, 0.1)',
                borderRadius: '6px'
            }}
            component="nav"
            aria-labelledby="nested-list-subheader"
            subheader={
                <ListSubheader sx={{fontWeight: 'bold'}} component="h3" id="nested-list-subheader">
                    Маршрути
                </ListSubheader>
            }
        >
            {
                routes.map((route, index) => {
                    const car = route.car;
                    return (<React.Fragment key={"routeSidebar"  + route.id}>
                        <Divider/>
                        <ListItem alignItems="flex-start"
                                  sx={{p: 0}}
                        >
                            <ListItemButton onClick={()=> setCurrentRouteIndex(index)} selected={route.id === currentRoute.id}>
                                <ListItemText primary={car.name + ", " + car.licencePlate} secondary={car.driver}/>
                            </ListItemButton>
                        </ListItem>
                    </React.Fragment>)
                })
            }
            <Divider/>
        </List>
    )
}

export default RoutesSidebar;