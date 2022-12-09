import React from "react";
import Box from "@mui/material/Box";
import Grid from "@mui/material/Grid";
import {Button, FormControl, MenuItem, Select, SelectChangeEvent, styled, Typography} from "@mui/material";
import {toHoursAndMinutes} from "../../../../../util/util";
import InfoTableItem from "../../../../../components/InfoTableItem/InfoTableItem";
import RouteMapDialog from "../RouteMapDialog/RouteMapDialog";
import {Route} from "../../../../../model/Route";
import {useCompleteRouteMutation, useStartRouteMutation} from "../../../../../api/routes/routes.api";

interface RouteDetailsProps {
    route: Route,
    deliveryDate?: string
}

const RouteDetails: React.FC<RouteDetailsProps> = ({route, deliveryDate}) => {
    return (<Box sx={{
        width: '100%', background: 'rgba(0, 0, 0, 0.02)',
        borderRadius: '6px', p: 2
    }}>
        <RouteDetailsHeader route={route} deliveryDate={deliveryDate}/>
        <Box sx={{mt: 3}}>
            <RouteDetailsBody route={route}/>
        </Box>
    </Box>)

}

export default RouteDetails;

const RouteDetailsHeader: React.FC<RouteDetailsProps> = ({route, deliveryDate}) => {
    const [routeMapOpen, setRouteMapOpen] = React.useState(false);

    return (<Box sx={{width: '100%', display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
        <Typography sx={{fontWeight: "bold"}} variant="body2" component="h2">
            {/*TODO: replace with real date*/}
            Маршрут №{route.id} від {deliveryDate}
        </Typography>
        <Box>

            <Box sx={{display: 'flex'}}>
                <Button sx={{mr: 2}} onClick={() => setRouteMapOpen(true)} variant="text">Показати на
                    мапі</Button>
                <RouteStatusSelect route={route}/>
            </Box>

            <RouteMapDialog route={route} open={routeMapOpen} closeDialog={() => setRouteMapOpen(false)}/>
        </Box>
    </Box>);
}


const RouteDetailsBody = ({route}: { route: Route }) => {
    const car = route.car;

    const startTime = route.startTime ? route.startTime : '8:00';
    const finishTime = route.finishTime ? route.finishTime : '-';
    const spentTime = route.spentTime ? route.spentTime : '-';

    return (<Grid container spacing={2}>
        <Grid item xs={3}>
            <InfoTableItem title={"Вантажність машини"} data={car.weightCapacity + " кг"}/>
        </Grid>
        <Grid item xs={3}>
            <InfoTableItem title={"Склад"} data={route.store.name}/>
        </Grid>
        <Grid item xs={6}>
            <InfoTableItem title={"Адреса складу"} data={route.store.address}/>
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


const RouteStatusSelect = ({route}: { route: Route }) => {
    const {status} = route;

    const [startRoute] = useStartRouteMutation();
    const [completeRoute] = useCompleteRouteMutation();

    const handleChange = (event: SelectChangeEvent<unknown>) => {
        if (event.target.value === 'INPROGRESS') {
            startRoute(route.id);
        } else if (event.target.value === 'COMPLETED') {
            completeRoute(route.id);
        }
    }
    const CustomSelect = styled(Select)(() => ({
        '& .MuiOutlinedInput-input': {
            padding: '4px 16px',
            textTransform: 'uppercase',
            fontSize: '13px',
            fontWeight: 500,
            backgroundColor: '#1565C0',
            color: '#fff',
            'WebkitTextFillColor': '#fff !important'
        },

        '& .MuiSelect-icon': {
            color: '#fff !important'
        }
    }));

    return <div>
        <FormControl disabled={status === 'COMPLETED'}>
            <CustomSelect
                value={status}
                onChange={handleChange}
                autoWidth
                MenuProps={{MenuListProps: {disablePadding: true}}}
            >
                <MenuItem value={'INPROGRESS'}>В роботі</MenuItem>
                <MenuItem disabled value={'APPROVED'}>Підтверджений</MenuItem>
                <MenuItem value={'COMPLETED'}>Закінчений</MenuItem>
            </CustomSelect>
        </FormControl>
    </div>
}


