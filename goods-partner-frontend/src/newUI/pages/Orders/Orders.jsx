import React, {useState} from "react";
import {Box, Button, Card, CardContent, TextField, Typography} from "@mui/material";
import OrdersContent from "./OrdersContent/OrdersContent";

import ArrowForwardIcon from '@mui/icons-material/ArrowForward';

const Orders = ({orders, loaded, date, getOrders}) => {
    const tomorrow = new Date(date);
    tomorrow.setDate(tomorrow.getDate() + 1);
    const defaultDate = tomorrow.toJSON().slice(0, 10).replace(/-/g, '-');
    const [calculationDate, setCalculationDate] = useState(defaultDate);

    return <section>
        <Box>
            <Typography variant="h6" component="h2">
                Замовлення
            </Typography>
            <Box sx={{mt: 2, display: 'flex', justifyContent: 'space-between'}}>
                <Box sx={{display: 'flex', alignItems: 'center'}}>
                    <TextField
                        sx={{minWidth: '200px'}}
                        id="date"
                        type="date"
                        size="small"
                        defaultValue={calculationDate}
                        InputLabelProps={{
                            shrink: true,
                        }}
                        onChange={(e) => setCalculationDate(e.target.value)}/>
                    <Button sx={{ml: 2}} variant={'contained'}
                            onClick={() => getOrders(calculationDate)}>Показати</Button>
                </Box>
                <Button sx={{color: 'rgba(0, 0, 0, 0.26)'}} variant={'contained'} disabled>
                    Розрахувати маршрут
                    <ArrowForwardIcon sx={{ml: 1, color: 'rgba(0, 0, 0, 0.26)', fontSize: '20px'}}/>
                </Button>
            </Box>


            <Box sx={{mt: 4}}>
                <Card variant="outlined">
                    {loaded && (
                        <Box sx={{
                            marginTop: '24px',
                            marginRight: '40px',
                            display: 'flex',
                            justifyContent: 'flex-end',
                            alignItems: 'center'
                        }}>
                            <Button variant="text" onClick={() => alert("Hello")}>Сформувати звіт</Button>
                            <Button variant="outlined">Оновити</Button>
                        </Box>)}
                    <Box sx={{marginTop: '-56px'}}>
                        {loaded ?
                            <OrdersContent orders={orders}/> : notLoadedYet()}
                    </Box>
                </Card>
            </Box>
        </Box>
    </section>
}

function notLoadedYet() {
    return <CardContent sx={{
        minHeight: 'calc(100vh - 280px)', display: 'flex',
        justifyContent: 'center', alignItems: 'center'
    }}>

        <Typography sx={{textTransform: 'uppercase'}} color="text.secondary">
            Для відображення інформації Оберіть дату та натисніть "ПОКАЗАТИ"
        </Typography>

    </CardContent>;
}

export default Orders;
