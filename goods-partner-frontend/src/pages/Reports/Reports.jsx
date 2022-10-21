import React from 'react';
import {Button, Card, CardContent, TextField, Typography} from "@mui/material";
import Box from "@mui/material/Box";
import InfoTableItem from "../../components/InfoTableItem/InfoTableItem";
import Grid from "@mui/material/Grid";
import {toHoursAndMinutes} from "../../util/util";
import SimpleBarChart from "../../components/SimpleBarChart/SimpleBarChart";

const Reports = ({deliveriesStatistics, getDeliveriesStatistics}) => {
    const [dateFrom, setDateFrom] = React.useState('2022-02-02');
    const [dateTo, setDateTo] = React.useState('2022-02-28');

    return (
        <Card sx={{minWidth: '100%'}}>
            <CardContent>
                <Typography sx={{fontWeight: "bold", mb: 2}} variant="body2" component="h2">
                    Період:
                </Typography>
                <Box
                    component="form"
                    sx={{
                        '& .MuiTextField-root': {width: '25ch', mr: 2}, display: 'flex', alignItems: 'center'
                    }}
                    noValidate
                    autoComplete="off"
                >
                    <TextField
                        id={"reportsDeliveryStatisticsDateFrom"}
                        type="date"
                        size="small"
                        format="yyyy/MM/dd"
                        value={dateFrom}
                        required
                        onChange={(event) => setDateFrom(event.target.value)}
                    />
                    <TextField
                        id={"reportsDeliveryStatisticsDateTo"}
                        type="date"
                        size="small"
                        value={dateTo}
                        required
                        onChange={(event) => setDateTo(event.target.value)}
                    />
                    <Button variant={"contained"}
                            onClick={() => getDeliveriesStatistics(dateFrom, dateTo)}>Показати</Button>
                </Box>

                {
                    deliveriesStatistics && <>
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
                }
            </CardContent>
        </Card>
    )
}

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


export default Reports;