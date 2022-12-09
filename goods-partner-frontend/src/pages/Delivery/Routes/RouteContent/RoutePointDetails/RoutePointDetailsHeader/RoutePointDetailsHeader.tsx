import React from "react";
import Box from "@mui/material/Box";
import {Button, Typography} from "@mui/material";
import {RoutePoint} from "../../../../../../model/RoutePoint";
import RoutePointSelect from "../RoutePointSelect/RoutePointSelect";

interface RoutePointDetailsHeaderProps {
    routePoint: RoutePoint,
    orderNumber: number
}

const RoutePointDetailsHeader = ({routePoint, orderNumber}: RoutePointDetailsHeaderProps) => {
    const [orderDialogOpen, setOrderDialogOpen] = React.useState(false);

    // const ordersDetailedInfo = [];

    return (<Box sx={{width: '100%', display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
        <Typography sx={{fontWeight: "bold", maxWidth: '450px'}} variant="body2" component="h2">
            №{orderNumber}, {routePoint.address}
        </Typography>

        {/*<Button variant="outlined" disabled>Змінити машину</Button>*/}
        <Box sx={{display: 'flex'}}>
            <Button variant="text" sx={{mr: 2}} onClick={() => setOrderDialogOpen(true)}>Показати деталі</Button>
            <RoutePointSelect routePoint={routePoint}/>
        </Box>

        {/*<RoutePointOrdersDialog open={orderDialogOpen} closeDialog={() => setOrderDialogOpen(false)}*/}
        {/*                        routePoint={routePoint} ordersDetailedInfo={ordersDetailedInfo}*/}
        {/*/>*/}
    </Box>);
}

export default RoutePointDetailsHeader;