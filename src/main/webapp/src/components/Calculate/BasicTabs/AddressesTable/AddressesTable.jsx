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

function createData(client, address, order, weight) {
    return {client, address, order, weight};
}

const rows = [
    createData('Клиент 1', 'Адресс', 4342212, 6.0),
    createData('Клиент 1', 'Адресс', 4342212, 6.0),
    createData('Клиент 1', 'Адресс', 4342212, 6.0),
    createData('Клиент 1', 'Адресс', 4342212, 6.0),
    createData('Клиент 1', 'Адресс', 4342212, 6.0),
    createData('Клиент 1', 'Адресс', 4342212, 6.0),
    createData('Клиент 1', 'Адресс', 4342212, 6.0),
];


function AddressesTable() {
    return (<Card sx={{minWidth: 275}}>
            <CardContent>
                <Stack spacing={2}>
                    <Typography variant="h5" gutterBottom component="div">
                        Список замовлень на 14.06.2022
                    </Typography>
                    <Box sx={{flexGrow: 1}}>
                        <TableContainer component={Paper}>
                            <Table aria-label="simple table">
                                <TableHead sx={{backgroundColor: 'rgba(0, 0, 0, 0.12);'}}>
                                    <TableRow>
                                        <TableCell sx={{padding: '7px'}}>Клієнт</TableCell>
                                        <TableCell sx={{padding: '7px'}} align="center">Адреса</TableCell>
                                        <TableCell sx={{padding: '7px'}} align="center">Замовлення</TableCell>
                                        <TableCell sx={{padding: '7px'}} align="center">Маса, кг</TableCell>
                                    </TableRow>
                                </TableHead>
                                <TableBody>
                                    <TableRow
                                    >
                                        <TableCell rowSpan={3} sx={{padding: '7px'}} component="th" scope="row">
                                            Клиент 1
                                        </TableCell>
                                        <TableCell rowSpan={2} sx={{padding: '7px'}} align="center">Адресс 1</TableCell>
                                        <TableCell sx={{padding: '7px'}} align="center">4342212</TableCell>
                                        <TableCell rowSpan={2} sx={{padding: '7px'}} align="center">6.0</TableCell>
                                    </TableRow>
                                    <TableRow

                                    >
                                        <TableCell sx={{padding: '7px'}} align="center">4342212</TableCell>
                                    </TableRow>
                                    <TableRow
                                        sx={{'&:last-child td, &:last-child th': {border: 0}}}
                                    >
                                        <TableCell sx={{padding: '7px'}} align="center">Адресс 3</TableCell>
                                        <TableCell sx={{padding: '7px'}} align="center">4342212</TableCell>
                                        <TableCell sx={{padding: '7px'}} align="center">6.0</TableCell>
                                    </TableRow>
                                    <TableRow>
                                        <TableCell sx={{padding: '7px'}} component="th" scope="row">
                                            Клиент 2
                                        </TableCell>
                                        <TableCell sx={{padding: '7px'}} align="center">Адресс 4</TableCell>
                                        <TableCell sx={{padding: '7px'}} align="center">4342212</TableCell>
                                        <TableCell sx={{padding: '7px'}} align="center">6.0</TableCell>
                                    </TableRow>
                                    <TableRow>
                                        <TableCell rowSpan={2} sx={{padding: '7px'}} component="th" scope="row">
                                            Клиент 2
                                        </TableCell>
                                        <TableCell rowSpan={2} sx={{padding: '7px'}} align="center">Адресс 4</TableCell>
                                        <TableCell sx={{padding: '7px'}} align="center">4342212</TableCell>
                                        <TableCell rowSpan={2} sx={{padding: '7px'}} align="center">6.0</TableCell>
                                    </TableRow>
                                    <TableRow>
                                        <TableCell sx={{padding: '7px'}} align="center">4342212</TableCell>
                                    </TableRow>

                                </TableBody>
                            </Table>
                        </TableContainer>
                    </Box>
                </Stack>
            </CardContent>
        </Card>
    );
}

export default AddressesTable;