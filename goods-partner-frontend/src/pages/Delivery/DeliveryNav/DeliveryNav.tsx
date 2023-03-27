import {AppBar, Button, Toolbar} from "@mui/material";
import {Link} from "react-router-dom";
import React, {FC} from "react";
import ShoppingCartIcon from "@mui/icons-material/ShoppingCart";
import RouteIcon from "@mui/icons-material/Route";
import InventoryIcon from "@mui/icons-material/Inventory";
import FactCheckIcon from "@mui/icons-material/FactCheck";

interface DeliveryNavProps {
    calculated: boolean
}

export const DeliveryNav: FC<DeliveryNavProps> = ({calculated}) => {
    const tabLabels = [
        {
            name: 'Замовлення',
            enabled: true,
            icon: <ShoppingCartIcon sx={{mr: 1}}/>,
            to: 'orders'
        },
        {
            name: 'Маршрути',
            enabled: calculated,
            icon: <RouteIcon sx={{mr: 1}}/>,
            to: 'routes'
        },
        {
            name: 'Завантаження',
            enabled: calculated,
            icon: <InventoryIcon sx={{mr: 1}}/>,
            to: 'shipping'
        },
        {
            name: 'Історія', enabled: true, icon: <FactCheckIcon sx={{mr: 1}}/>,
            to: 'history'
        }
    ]


    return <AppBar position="static" sx={{padding: '0 300px'}}>
        <Toolbar sx={{
            display: {xs: "flex"},
            flexDirection: "row",
            justifyContent: "space-around",

        }}>
            {
                tabLabels.map(t =>
                    <Button component={Link} to={t.to}
                            key={"delNav" + t.to}
                            startIcon={t.icon} color="inherit" disabled={!t.enabled}>{t.name}</Button>)
            }

        </Toolbar>
    </AppBar>
}