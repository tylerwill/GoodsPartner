import Paper from "@mui/material/Paper";
import Table from "@mui/material/Table";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import TableCell from "@mui/material/TableCell";
import TableBody from "@mui/material/TableBody";
import TableContainer from "@mui/material/TableContainer";
import React, {FC} from "react";
import Order from "../../model/Order";

interface ProductsInfoTableProps {
    order: Order,
    keyPrefix: string
}

export const ProductsInfoTable: FC<ProductsInfoTableProps> = ({order, keyPrefix}) => {
    return <TableContainer component={Paper}>
        <Table sx={{minWidth: 650}} size='small'>
            <TableHead>
                <TableRow>
                    <TableCell sx={{width: '500px'}}>Артикул</TableCell>
                    <TableCell>Кількість</TableCell>
                    <TableCell>Упаковка</TableCell>
                    <TableCell>Всьго</TableCell>
                </TableRow>
            </TableHead>
            <TableBody>
                {order.products.map((product, index) => {
                    const count = product.productPackaging.amount + ' ' + product.productPackaging.measureStandard;
                    const totalWeight = product.productUnit.amount + ' ' + product.productUnit.measureStandard;
                    const packaging = product.productPackaging.coefficientStandard + ' ' + product.productUnit.measureStandard;
                    return <TableRow
                        key={keyPrefix + index + product.productName}
                        sx={{
                            '&:last-child td, &:last-child th': {border: 0}
                        }}
                    >
                        <TableCell component='th' scope='row'>
                            {product.productName}
                        </TableCell>
                        <TableCell> {count}</TableCell>
                        <TableCell> {packaging}</TableCell>
                        <TableCell> {totalWeight}</TableCell>
                    </TableRow>
                })}
            </TableBody>
        </Table>
    </TableContainer>
}

