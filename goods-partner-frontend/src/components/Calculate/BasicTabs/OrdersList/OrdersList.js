import Box from '@mui/material/Box';
import {Button, Card, CardContent, Stack, Typography} from "@mui/material";
import Grid from "@mui/material/Grid";
import OrdersTable from "./OrdersTable/OrdersTable";
import React, {useState} from "react";
import {generateReportLink} from "../../../../util/util";

function OrdersList({date, orders}) {
    const initialOrder = orders.length === 0 ? '' : orders[0].orderNumber;

    const [activeOrder, setActiveOrder] = useState(initialOrder);
    const reportLink = generateReportLink("orders", date);
    return (<Card sx={{minWidth: 275}}>
            <CardContent>
                <Stack spacing={2}>
                    {
                        initialOrder !== ''
                            ? <>
                                <Box>
                                    <Grid container spacing={1}>
                                        <Grid item xs={10}>
                                            <Typography variant="h5" gutterBottom component="div">
                                                Список замовлень на {date}
                                            </Typography>
                                        </Grid>
                                        <Grid item xs={2}>
                                            <a target="_blank" rel="noreferrer" href={reportLink}>
                                                <Button size="small" variant="contained" color="success"
                                                        style={{width: '12em'}}>
                                                    Сформувати звіт
                                                </Button>
                                            </a>
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
                                                                key={order.orderNumber + "button"}
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
                                                {orders.map(order => order.orderNumber === activeOrder ?
                                                    <OrdersTable order={order} key={order.orderNumber + "table"}/> : '')
                                                }
                                            </Stack>
                                        </Grid>
                                    </Grid>
                                </Box>
                            </>
                            : date ? <Typography variant="h5" gutterBottom component="div">
                                    Немає ордерів на зазначену дату
                                </Typography>
                                :
                                <Typography variant="h5" gutterBottom component="div">
                                    Оберіть дату та натисніть "РОЗРАХУВАТИ"
                                </Typography>
                    }
                </Stack>
            </CardContent>
        </Card>
    );
}

export default OrdersList;
