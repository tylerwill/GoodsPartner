import Box from '@mui/material/Box';
import {Button, Card, CardActions, CardContent, Stack, TextField, Typography} from "@mui/material";

import * as React from 'react';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';


function createRow(desc, store, weight) {
    return { desc, store, weight };
}

function subtotal(items) {
    return items.map(({ weight }) => weight).reduce((sum, i) => sum + i, 0);
}

const rows = [
    createRow('23454 Паста шоколадна', 'Склад №1', 1.0),
    createRow('54643  Стружка кокосова', 'Склад №2', 45.99),
    createRow('56743 Масло вешкове', 'Склад №1', 17.99),
];

const overallWeight = subtotal(rows);

export default function OrdersTable() {
    return (
        <TableContainer component={Paper} sx={{marginTop:'10px'}}>
            <Table  aria-label="spanning table">
                <TableHead sx={{backgroundColor:'rgba(0, 0, 0, 0.12);'}}>
                    <TableRow>
                        <TableCell sx={{padding:'7px'}}>Артикул</TableCell>
                        <TableCell sx={{padding:'7px'}} align="right">Склад</TableCell>
                        <TableCell sx={{padding:'7px'}} align="right">Маса, кг</TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    {rows.map((row) => (
                        <TableRow key={row.desc}>
                            <TableCell sx={{padding:'7px'}}>{row.desc}</TableCell>
                            <TableCell sx={{padding:'7px'}} align="right">{row.store}</TableCell>
                            <TableCell sx={{padding:'7px'}} align="right">{row.weight}</TableCell>
                        </TableRow>
                    ))}

                    <TableRow>
                        <TableCell rowSpan={3} />
                        <TableCell sx={{padding:'7px'}} align="right">Всього: </TableCell>
                        <TableCell sx={{padding:'7px'}} align="right">{overallWeight}</TableCell>
                    </TableRow>
                </TableBody>
            </Table>
        </TableContainer>
    );
}

