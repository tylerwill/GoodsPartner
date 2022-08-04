import Box from '@mui/material/Box';
import {Button, Card, CardContent, Stack, Typography} from "@mui/material";
import Grid from "@mui/material/Grid";
import OrdersTable from "./OrdersTable/OrdersTable";
import React, {useState} from "react";

function OrdersList({date, orders}) {
    const initialOrder = orders === null ? '' : orders[0].orderNumber;

    const [activeOrder, setActiveOrder] = useState(initialOrder);

    return (<Card sx={{minWidth: 275}}>
            <CardContent>
                <Stack spacing={2}>
                    {
                        date
                            ? <>
                                <Box>
                                    <Grid container spacing={1}>
                                        <Grid item xs={10}>
                                            <Typography variant="h5" gutterBottom component="div">
                                                Список замовлень на {date}
                                            </Typography>
                                        </Grid>
                                        <Grid item xs={2}>
                                            <Button size="small" variant="outlined" style={{width: '12em'}}>
                                                Сформувати звіт
                                            </Button>

                                        </Grid>
                                    </Grid>
                                </Box>
                                <Box sx={{flexGrow: 1}}>
                                    <Grid container spacing={2}>
                                        <Grid item xs={3}>
                                            <Stack spacing={0.5}>
                                                {
                                                    orders.map(order =>
                                                        <Button size="small"
                                                                variant={order.orderNumber === activeOrder ? "contained" : "outlined"}
                                                                onClick={() => setActiveOrder(order.orderNumber)}
                                                        >
                                                            ЗАМОВЛЕННЯ №{order.orderNumber}
                                                        </Button>)
                                                }
                                            </Stack>
                                        </Grid>
                                        <Grid item xs={9}>
                                            <Stack spacing={2}>
                                                <Typography variant="h6" component="h1">
                                                    <Stack>
                                                        {orders.map(order => order.orderNumber === activeOrder ?
                                                            <OrdersTable order={order}/> : '')}
                                                    </Stack>
                                                </Typography>
                                            </Stack>
                                        </Grid>
                                    </Grid>
                                </Box>
                            </>
                            : <Typography variant="h5" gutterBottom component="div">
                                Оберіть дату та натисніть "РОЗРАХУВАТИ"
                            </Typography>
                    }
                </Stack>
            </CardContent>
        </Card>
    );
}

export default OrdersList;
