import Box from '@mui/material/Box';
import {Card, CardContent, Stack, Typography} from "@mui/material";

import * as React from 'react';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';

function createData(store, order, weight) {
    return { store, order, weight };
}

const rows = [
    createData('Склад №1', 4342212, 6.0),
    createData('Склад №1', 4352212, 16.0),
    createData('Склад №1', 5433232, 88.0),
    createData('Склад №2', 1232132, 16.0),
    createData('Склад №2', 155534, 44.0),
    createData('Склад №3', 2342234, 65.0),
    createData('Склад №3', 3245655, 200.0),
    createData('Склад №3', 2343544, 17.0),

];


function StoreTable() {
    return (<Card sx={{minWidth: 275}}>
            <CardContent>
                <Stack spacing={2}>
                    <Typography variant="h5" gutterBottom component="div">
                        Список замовлень на 14.06.2022
                    </Typography>
                    <Box sx={{flexGrow: 1}}>
                        <TableContainer component={Paper}>
                            <Table aria-label="simple table">
                                <TableHead  sx={{backgroundColor:'rgba(0, 0, 0, 0.12);'}}>
                                    <TableRow>
                                        <TableCell sx={{padding:'7px'}}>Склад</TableCell>
                                        <TableCell sx={{padding:'7px'}} align="right">Замовлення</TableCell>
                                        <TableCell sx={{padding:'7px'}} align="right">Маса, кг</TableCell>
                                    </TableRow>
                                </TableHead>
                                <TableBody>
                                    {rows.map((row) => (
                                        <TableRow
                                            key={row.name}
                                            sx={{ '&:last-child td, &:last-child th': { border: 0 } }}
                                        >
                                            <TableCell sx={{padding:'7px'}} component="th" scope="row">
                                                {row.store}
                                            </TableCell>
                                            <TableCell sx={{padding:'7px'}}  align="right">{row.order}</TableCell>
                                            <TableCell sx={{padding:'7px'}} align="right">{row.weight}</TableCell>
                                        </TableRow>
                                    ))}
                                </TableBody>
                            </Table>
                        </TableContainer>
                    </Box>
                </Stack>
            </CardContent>
        </Card>
    );
}

export default StoreTable;
