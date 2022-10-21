import {Typography} from "@mui/material";
import Grid from "@mui/material/Grid";
import InfoTableItem from "../../../components/InfoTableItem/InfoTableItem";
import {toHoursAndMinutes} from "../../../util/util";
import React from "react";
import Box from "@mui/material/Box";
import SimpleBarChart from "../../../components/SimpleBarChart/SimpleBarChart";

const DeliveryStatiscticsInfo = ({deliveriesStatistics}) => {
    return (
        <>
            <Typography sx={{fontWeight: "bold", mt: 2, mb: 2}} variant="body2" component="h2">
                Загальна статистика:
            </Typography>

            <Grid container spacing={2}>
                <Grid item xs={4}>
                    <InfoTableItem title={"Маршрутів"} data={deliveriesStatistics.routeCount}/>
                </Grid>
                <Grid item xs={4}>
                    <InfoTableItem title={"Замовлень"} data={deliveriesStatistics.orderCount}/>
                </Grid>
                <Grid item xs={4}>
                    <InfoTableItem title={"Вага"} data={deliveriesStatistics.weight + ' кг'}/>
                </Grid>
                <Grid item xs={4}>
                    <InfoTableItem title={"Паливо"} data={deliveriesStatistics.fuelConsumption}/>
                </Grid>
                <Grid item xs={4}>
                    <InfoTableItem title={"Середній час доставки"}
                                   data={toHoursAndMinutes(deliveriesStatistics.averageDeliveryDuration)}/>
                </Grid>
            </Grid>
            <BarChart statistics={deliveriesStatistics.routesForPeriodPerDay}
                      xAxisName={'Дата'}
                      yAxisName={'Маршрути'}
            />

            <BarChart statistics={deliveriesStatistics.ordersForPeriodPerDay}
                      xAxisName={'Дата'}
                      yAxisName={'Замовлення'}
            />

            <BarChart statistics={deliveriesStatistics.weightForPeriodPerDay}
                      xAxisName={'Дата'}
                      yAxisName={'Вага'}
            />

            <BarChart statistics={deliveriesStatistics.fuelConsumptionForPeriodPerDay}
                      xAxisName={'Дата'}
                      yAxisName={'Паливо'}
            />

        </>
    );
}

export default DeliveryStatiscticsInfo;

const BarChart = ({statistics, xAxisName, yAxisName}) => {
    const data = [];
    for (let [key, value] of Object.entries(statistics)) {
        data.push({[xAxisName]: key, [yAxisName]: value});
    }
    return (<Box sx={{mt: 4}}>
        <Typography sx={{fontWeight: "bold", mt: 2, mb: 2}} variant="body1" component="h2">
            {yAxisName}:
        </Typography>
        <SimpleBarChart data={data} xAxisName={xAxisName} yAxisName={yAxisName}/>
    </Box>)
}