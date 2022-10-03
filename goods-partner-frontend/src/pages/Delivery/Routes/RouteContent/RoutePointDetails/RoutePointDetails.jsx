import React from 'react';
import Box from "@mui/material/Box";
import {Button, Typography} from "@mui/material";
import Grid from "@mui/material/Grid";
import InfoTableItem from "../../../../../components/InfoTableItem/InfoTableItem";


const RoutePointDetails = ({routePoint, number}) => {
    return (<Box sx={{
        width: '100%', background: 'rgba(0, 0, 0, 0.02)',
        borderRadius: '6px', p: 2
    }}>
        <RoutePointDetailsHeader routePoint={routePoint} number={number}/>
        <Box sx={{mt: 3}}>
            <RoutePointDetailsBody routePoint={routePoint}/>
        </Box>
    </Box>)

}

const RoutePointDetailsHeader = ({routePoint, number}) => {
    return (<Box sx={{width: '100%', display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
        <Typography sx={{fontWeight: "bold", maxWidth: '450px'}} variant="body2" component="h2">
            №{number}, {routePoint.address}
        </Typography>
        <Button variant="outlined" disabled>Змінити машину</Button>
    </Box>);
}

const RoutePointDetailsBody = ({routePoint}) => {
    const orderNumber = routePoint.orders[0].orderNumber;

    return (<Grid container spacing={2}>
        <Grid item xs={4}>
            <InfoTableItem title={"Номер замовлення"} data={orderNumber}/>
        </Grid>
        <Grid item xs={4}>
            <InfoTableItem title={"Вага"} data={routePoint.addressTotalWeight + ' кг'}/>
        </Grid>
        <Grid item xs={4}>
            <InfoTableItem title={"Клієнт"} data={routePoint.clientName}/>
        </Grid>
        <Grid item xs={4}>
            <InfoTableItem title={"Прибуття, прогноз"} data={"-"}/>
        </Grid>
        <Grid item xs={4}>
            <InfoTableItem title={"Завершення, прогноз"} data={"-"}/>
        </Grid>
        <Grid item xs={4}>
            <InfoTableItem title={"Завершення, факт"} data={"-"}/>
        </Grid>

    </Grid>);
}


export default RoutePointDetails;