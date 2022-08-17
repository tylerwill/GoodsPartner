import * as React from 'react';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';

function subtotal(products) {
    return products.map(({totalProductWeight}) => totalProductWeight).reduce((sum, i) => sum + i, 0);
}

export default function OrdersBody({products}) {
    const overallWeight = subtotal(products);
    return (
        <TableContainer component={Paper} sx={{marginTop: '10px'}}>
            <Table aria-label="spanning table">
                <TableHead sx={{backgroundColor: 'rgba(0, 0, 0, 0.12);'}}>
                    <TableRow>
                        <TableCell sx={{padding: '7px'}}>Артикул</TableCell>
                        <TableCell sx={{padding: '7px'}} align="right">Кількість</TableCell>
                        <TableCell sx={{padding: '7px'}} align="right">Вага, кг</TableCell>
                        <TableCell sx={{padding: '7px'}} align="right">Вага заг., кг</TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    {products.map((product) => (
                        <TableRow key={product.productName}>
                            <TableCell sx={{padding: '7px'}}>{product.productName}</TableCell>
                            <TableCell sx={{padding: '7px'}} align="right">{product.amount}</TableCell>
                            <TableCell sx={{padding: '7px'}} align="right">{product.unitWeight}</TableCell>
                            <TableCell sx={{padding: '7px'}} align="right">{product.totalProductWeight}</TableCell>
                        </TableRow>
                    ))}

                    <TableRow>
                        <TableCell colspan={2}/>
                        <TableCell sx={{padding: '7px'}} align="right">Всього: </TableCell>
                        <TableCell sx={{padding: '7px'}} align="right">{overallWeight}</TableCell>
                    </TableRow>
                </TableBody>
            </Table>
        </TableContainer>
    );
}

