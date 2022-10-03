import React from "react";
import Box from "@mui/material/Box";
import Grid from "@mui/material/Grid";
import {Button, Typography} from "@mui/material";
import {toHoursAndMinutes} from "../../../../../util/util";
import InfoTableItem from "../../../../../components/InfoTableItem/InfoTableItem";

const RouteDetails = ({route}) => {
    return (<Box sx={{
        width: '100%', background: 'rgba(0, 0, 0, 0.02)',
        borderRadius: '6px', p: 2
    }}>
        <RouteDetailsHeader route={route}/>
        <Box sx={{mt: 3}}>
            <RouteDetailsBody route={route}/>
        </Box>
    </Box>)

}

export default RouteDetails;

const RouteDetailsHeader = ({route}) => {
    return (<Box sx={{width: '100%', display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
        <Typography sx={{fontWeight: "bold"}} variant="body2" component="h2">
            {/*TODO: replace with real date*/}
            Маршрут №{route.id} від 2022-02-04
        </Typography>
        <Box>
            <Button sx={{mr: 2}} variant="text">Показати на мапі</Button>
            <Button variant="contained" disabled>Створений</Button>
        </Box>
    </Box>);
}


const RouteDetailsBody = ({route}) => {
    const car = route.car;

    const startTime = route.startTime ? route.startTime : '9:30';
    const finishTime = route.finishTime ? route.finishTime : '-';
    const spentTime = route.spentTime ? route.spentTime : '-';

    return (<Grid container spacing={2}>
        <Grid item xs={3}>
            <InfoTableItem title={"Вантажність машини"} data={car.weightCapacity + " кг"}/>
        </Grid>
        <Grid item xs={3}>
            <InfoTableItem title={"Склад"} data={route.storeName}/>
        </Grid>
        <Grid item xs={6}>
            <InfoTableItem title={"Адреса складу"} data={route.storeAddress}/>
        </Grid>
        <Grid item xs={3}>
            <InfoTableItem title={"Кількість адрес"} data={route.totalPoints}/>
        </Grid>
        <Grid item xs={3}>
            <InfoTableItem title={"Кількість замовлень"} data={route.totalOrders}/>
        </Grid>
        <Grid item xs={3}>
            <InfoTableItem title={"Загальна вага"} data={route.totalWeight + " кг"}/>
        </Grid>
        <Grid item xs={3}>
            <InfoTableItem title={"Відстань"} data={route.distance}/>
        </Grid>
        <Grid item xs={3}>
            <InfoTableItem title={"Розрахунковий час виконання"} data={toHoursAndMinutes(route.estimatedTime)}/>
        </Grid>
        <Grid item xs={3}>
            <InfoTableItem title={"Початок виконання"} data={startTime}/>
        </Grid>
        <Grid item xs={3}>
            <InfoTableItem title={"Кінець виконання"} data={finishTime}/>
        </Grid>
        <Grid item xs={3}>
            <InfoTableItem title={"Фактичний час виконання"} data={spentTime}/>
        </Grid>
    </Grid>);
}
