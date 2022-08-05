import React from 'react';
import {AppBar, Button, Card, CardContent, Stack, TextField, Toolbar, Typography} from "@mui/material";
import {NavLink} from "react-router-dom";
import Box from "@mui/material/Box";
import TableContainer from "@mui/material/TableContainer";
import Paper from "@mui/material/Paper";
import Table from "@mui/material/Table";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import TableCell from "@mui/material/TableCell";
import TableBody from "@mui/material/TableBody";

function CarTable({cars}) {
    let isCarPrinted = false;
    console.log("car table, ", cars);

    let body = "";
    if (cars) {
        body = cars.map((car) => (<>
                <>
                    <TableRow
                        key={car.id}
                        sx={{'&:last-child td, &:last-child th': {border: 0}}}
                    >
                        {
                            isCarPrinted === false &&
                            <TableCell sx={{padding: '7px'}} component="th"
                                       scope="row">
                                {car.id}
                            </TableCell>
                        }
                        <TableCell sx={{padding: '7px'}} component="th" scope="row"
                                   align="right">
                            {car.name}
                        </TableCell>
                        <TableCell sx={{padding: '7px'}} component="th" scope="row"

                                   align="right">
                            {car.licence_plate}
                        </TableCell>
                        <TableCell sx={{padding: '7px'}} component="th" scope="row"
                                   align="right">
                            {car.driver}
                        </TableCell>
                        <TableCell sx={{padding: '7px'}} component="th" scope="row"
                                   align="right">
                            {car.weight_capacity}
                        </TableCell>
                        <TableCell sx={{padding: '7px'}} component="th" scope="row"
                                   align="right">
                            {car.cooler}
                        </TableCell>
                        <TableCell sx={{padding: '7px'}} component="th" scope="row"
                                   align="right">
                            {car.available}
                        </TableCell>
                        {isCarPrinted = true}
                    </TableRow>
                </>
                )}
            </>
        ));
    }
    return (<Card sx={{minWidth: 275}}>
            <CardContent>
                <Stack spacing={2}>
                    <Typography variant="h5" gutterBottom component="div" align="center">
                        Список авто
                    </Typography>
                    <Box sx={{flexGrow: 1}}>
                        <TableContainer component={Paper}>
                            <Table aria-label="simple table">
                                <TableHead sx={{backgroundColor: 'rgba(0, 0, 0, 0.12);'}}>
                                    <TableRow>
                                        <TableCell sx={{padding: '7px'}}>№</TableCell>
                                        <TableCell sx={{padding: '7px'}} align="center">Модель авто</TableCell>
                                        <TableCell sx={{padding: '7px'}} align="center">Номер авто</TableCell>
                                        <TableCell sx={{padding: '7px'}} align="center">Водій</TableCell>
                                        <TableCell sx={{padding: '7px'}} align="center">Вантажопідйомність,
                                            т</TableCell>
                                        <TableCell sx={{padding: '7px'}} align="center">Витрати палива
                                            л/100км</TableCell>
                                        <TableCell sx={{padding: '7px'}} align="center">Коефіцієнт
                                            завантаження</TableCell>
                                        <TableCell sx={{padding: '7px'}} align="center">Наявність морозильної
                                            камери</TableCell>
                                        <TableCell sx={{padding: '7px'}} align="center">Доступність
                                            автомобіля</TableCell>
                                        <TableCell sx={{padding: '7px'}} align="center">Видалити авто</TableCell>
                                    </TableRow>
                                </TableHead>
                                <TableBody>
                                    {body}
                                </TableBody>
                            </Table>
                        </TableContainer>
                    </Box>
                </Stack>
            </CardContent>
        </Card>
    );
}

export default CarTable;
