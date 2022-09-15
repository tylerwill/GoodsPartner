import React from 'react';
import {Button, Card, CardContent, Checkbox, Grid, Stack, Switch, Typography} from "@mui/material";
import Box from "@mui/material/Box";
import TableContainer from "@mui/material/TableContainer";
import Paper from "@mui/material/Paper";
import Table from "@mui/material/Table";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import TableCell from "@mui/material/TableCell";
import TableBody from "@mui/material/TableBody";
import {deleteCar} from "../../../../redux/thunks/calculate-thunk";
import {useDispatch} from "react-redux";

const label = {inputProps: {'aria-label': 'Checkbox'}};

const handleDelete = (id) => {
    dispatch(deleteCar(id));
}

function CarTable({cars}) {
    let isCarPrinted = false;
    console.log("car table, ", cars);

    let body = "";
    if (cars) {
        body = cars.cars.map((car) => (<>
                <>
                    <TableRow
                        key={car.id}
                        sx={{'&:last-child td, &:last-child th': {border: 0}}}
                    >
                        {
                            <TableCell sx={{padding: '7px'}} component="th" scope="row">
                                {car.id}
                            </TableCell>
                        }
                        <TableCell sx={{padding: '7px'}} component="th" scope="row" align="center">
                            {car.name}
                        </TableCell>
                        <TableCell sx={{padding: '7px'}} component="th" scope="row" align="center">
                            {car.licencePlate}
                        </TableCell>
                        <TableCell sx={{padding: '7px'}} component="th" scope="row" align="center">
                            {car.driver}
                        </TableCell>
                        <TableCell sx={{padding: '7px'}} component="th" scope="row" align="center">
                            {car.weightCapacity}
                        </TableCell>
                        <TableCell sx={{padding: '7px'}} component="th" scope="row" align="center">
                            {car.travelCost}
                        </TableCell>
                        <TableCell sx={{padding: '7px'}} component="th" scope="row" align="center">
                            <Switch {...label} defaultChecked/>
                        </TableCell>
                        <TableCell sx={{padding: '7px'}} component="th" scope="row" align="center">
                            <Switch checked={car.available}  {...label} defaultChecked/>
                        </TableCell>
                        <TableCell sx={{padding: '7px'}} component="th" scope="row" align="center">
                            <Checkbox {...label} />
                        </TableCell>
                        {isCarPrinted = true}
                    </TableRow>
                </>
            </>
        ));
    }
    return (<Card sx={{minWidth: 275}}>
            <CardContent>
                <Stack spacing={2}>
                    <Grid container spacing={1}>
                        <Grid item xs={12}>
                            <Typography variant="h5" gutterBottom component="div" align="center">
                                Список авто
                            </Typography>
                        </Grid>
                        <Grid item xs={10}>
                        </Grid>
                        <Grid item xs={2}>
                            <Button size="small" variant="contained" style={{width: '12em'}}>
                                Додати авто
                            </Button>
                        </Grid>
                    </Grid>
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
                                        <TableCell sx={{padding: '7px'}} align="center">Наявність морозильної
                                            камери</TableCell>
                                        <TableCell sx={{padding: '7px'}} align="center">Доступність
                                            автомобіля</TableCell>
                                        <TableCell sx={{padding: '7px'}} align="center"
                                                   onClick={() => handleDelete(id)}>Видалити
                                            авто</TableCell>
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
