import React from 'react';
import {Button, Card, CardContent, TextField, Typography} from "@mui/material";
import Box from "@mui/material/Box";
import {useDispatch, useSelector} from "react-redux";
import {fetchDeliveriesStatistics} from "../../features/reports/reportsSlice";
import Loading from "../../components/Loading/Loading";
import ErrorAlert from "../../components/ErrorAlert/ErrorAlert";
import DeliveryStatiscticsInfo from "./DeliveryStatiscticsInfo/DeliveryStatiscticsInfo";

const Reports = () => {
    const {deliveriesStatistics, loading, error} = useSelector(state => state.reports);
    const [dateFrom, setDateFrom] = React.useState('2022-02-02');
    const [dateTo, setDateTo] = React.useState('2022-02-28');

    const dispatch = useDispatch();

    const getStatisticsHandler = () => dispatch(fetchDeliveriesStatistics({dateFrom, dateTo}))

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
                            onClick={getStatisticsHandler}>Показати</Button>
                </Box>

                {loading && <Loading/>}

                {error && <ErrorAlert error={error}/>}

                {
                    deliveriesStatistics.length !== 0 &&
                    <DeliveryStatiscticsInfo deliveriesStatistics={deliveriesStatistics}/>
                }
            </CardContent>
        </Card>
    )
}


export default Reports;