import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import TableCell from "@mui/material/TableCell";
import TableBody from "@mui/material/TableBody";
import Table from "@mui/material/Table";
import React, {FC} from "react";
import Order from "../../../../../model/Order";
import OrderTableRow from "./OrderTableRow/OrderTableRow";

interface OrdersBasicTable {
    page: number
    rowsPerPage: number
    orders: Order[]
}

export const DeliveryOrdersShortTable: FC<OrdersBasicTable> = ({
                                                                   page,
                                                                   rowsPerPage,
                                                                   orders
                                                               }) => {


    // Avoid a layout jump when reaching the last page with empty rows.
    const emptyRows =
        page > 0 ? Math.max(0, (1 + page) * rowsPerPage - orders.length) : 0

    return <Table aria-label='collapsible table'>
        <TableHead>
            <TableRow>
                <TableCell sx={{minWidth: '135px'}}>№ замовлення</TableCell>
                <TableCell>Клієнт</TableCell>
                <TableCell>Комментар</TableCell>
                <TableCell/>
            </TableRow>
        </TableHead>
        <TableBody>
            {/*TODO: [Tolik] Think about keys */}
            {orders
                .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                .map((order, index) => <OrderTableRow order={order}/>)}

            {emptyRows > 0 && (
                <TableRow
                    style={{
                        height: 53 * emptyRows
                    }}
                >
                    <TableCell colSpan={4}/>
                </TableRow>
            )}
        </TableBody>
    </Table>
}