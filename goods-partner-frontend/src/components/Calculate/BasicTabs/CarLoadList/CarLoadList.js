import Box from '@mui/material/Box';
import {Button, Card, CardContent, Stack, Typography} from "@mui/material";

import * as React from 'react';
import Table from '@mui/material/Table';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import TableBody from "@mui/material/TableBody";
import Grid from "@mui/material/Grid";
import {generateReportLink} from "../../../../util/util";

function StoreTable({date, carLoadDetails}) {
    if (carLoadDetails.length === 0) {
        return (<div>Nothing</div>);
    }
    const reportLink = generateReportLink("carsload", date);
    const rows = [];
    carLoadDetails.forEach(carLoadDetail => {
        const currentCar = carLoadDetail.car;

        carLoadDetail.orders.forEach(order => {
            const orderNumber = order.orderNumber;

            order.products.forEach(product => {
                rows.push(createTableRow(currentCar, orderNumber, product));
            })
        })
    })

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
                                                Загрузка машин на {date}
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
                                    <TableContainer component={Paper}>
                                        <Table aria-label="simple table">
                                            <TableHead sx={{backgroundColor: 'rgba(0, 0, 0, 0.12);'}}>
                                                <TableRow>
                                                    <TableCell sx={{padding: '7px'}}>Машина</TableCell>
                                                    <TableCell sx={{padding: '7px'}} align="right">Замовлення</TableCell>
                                                    <TableCell sx={{padding: '7px'}} align="right">Товар</TableCell>
                                                    <TableCell sx={{padding: '7px'}} align="right">Кількість</TableCell>
                                                    <TableCell sx={{padding: '7px'}} align="right">Маса одиниці,
                                                        кг</TableCell>
                                                    <TableCell sx={{padding: '7px'}} align="right">Загальна маса,
                                                        кг</TableCell>
                                                </TableRow>
                                            </TableHead>
                                            <TableBody>
                                                {rows}
                                            </TableBody>
                                        </Table>
                                    </TableContainer>
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

const createTableRow = (car, orderNumber, product) => {
    return (<TableRow sx={{'&:last-child td, &:last-child th': {border: 0}}}>

        <TableCell sx={{padding: '7px'}} component="th" scope="row">
            {car.name + " (" + car.licencePlate + ")"}
        </TableCell>

        <TableCell sx={{padding: '7px'}} component="th" scope="row" align="right">
            {orderNumber}
        </TableCell>

        <TableCell sx={{padding: '7px'}} component="th" scope="row" align="right">
            {product.productName}
        </TableCell>

        <TableCell sx={{padding: '7px'}} component="th" scope="row" align="right">
            {product.amount}
        </TableCell>

        <TableCell sx={{padding: '7px'}} component="th" scope="row" align="right">
            {product.unitWeight.toLocaleString(undefined, {maximumFractionDigits: 2})}
        </TableCell>

        <TableCell sx={{padding: '7px'}} component="th" scope="row" align="right">
            {product.totalProductWeight.toLocaleString(undefined, {maximumFractionDigits: 2})}
        </TableCell>


    </TableRow>)
}

export default StoreTable;
