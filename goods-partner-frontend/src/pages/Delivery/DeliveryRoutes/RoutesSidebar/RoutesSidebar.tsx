import React from 'react'
import {ListItem, ListItemIcon, ListItemText, ListSubheader, Tooltip} from '@mui/material'
import List from '@mui/material/List'
import Divider from '@mui/material/Divider'
import ListItemButton from '@mui/material/ListItemButton'
import {Route} from '../../../../model/Route'
import WarningAmberIcon from '@mui/icons-material/WarningAmber';

interface RoutesSidebarProps {
    routes: Array<Route>
    currentRoute: Route
    setCurrentRouteIndex: (index: number) => void
}

const RoutesSidebar = ({
                           routes,
                           currentRoute,
                           setCurrentRouteIndex
                       }: RoutesSidebarProps) => {


    return (
        <List
            sx={{
                width: '100%',
                height: '80vh',
                bgcolor: 'background.paper',
                border: '1px solid rgba(0, 0, 0, 0.1)',
                borderRadius: '6px'
            }}
            component='nav'
            aria-labelledby='nested-list-subheader'
            subheader={
                <ListSubheader
                    sx={{fontWeight: 'bold'}}
                    component='h3'
                    id='nested-list-subheader'
                >
                    Маршрути
                </ListSubheader>
            }
        >
            {routes.map((route: Route, index: number) => {
                const car = route.car
                const hasUnmatchedRoutePoint = route.routePoints.find(r => !r.matchingExpectedDeliveryTime);
                return (
                    <React.Fragment key={'routeSidebar' + route.id}>
                        <Divider/>
                        <ListItem alignItems='flex-start' sx={{p: 0}}>
                            <ListItemButton
                                onClick={() => setCurrentRouteIndex(index)}
                                selected={route.id === currentRoute.id}
                            >
                                <ListItemText
                                    primary={car.name + ', ' + car.licencePlate}
                                    secondary={car.driver.userName}
                                />
                                {
                                    hasUnmatchedRoutePoint &&
                                    <ListItemIcon>
                                        <Tooltip
                                            title={"Доставка не за розкладом"}
                                            placement='top'
                                            arrow
                                        >
                                         <WarningAmberIcon color={'warning'}/>
                                        </Tooltip>
                                    </ListItemIcon>
                                }
                            </ListItemButton>
                        </ListItem>
                    </React.Fragment>
                )
            })}
            <Divider/>
        </List>
    )
}

export default RoutesSidebar

// secondary={car.driver}
