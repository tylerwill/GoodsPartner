import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import TableCell from "@mui/material/TableCell";
import TableBody from "@mui/material/TableBody";
import OrderTableRow from "./OrderTableRow/OrderTableRow";
import Table from "@mui/material/Table";
import React, {FC} from "react";
import Order from "../../../../../model/Order";

interface OrdersBasicTable {
    isExcluded: boolean
    page: number
    rowsPerPage: number
    keyPrefix: string
    orders: Order[]
    updateOrder: (order: Order) => void
    expandAll: boolean
    collapseAll: boolean
    reset: () => void
}

export const DeliveryOrdersBasicTable: FC<OrdersBasicTable> = ({
                                                    isExcluded,
                                                    page,
                                                    rowsPerPage,
                                                    updateOrder,
                                                    orders,
                                                    expandAll,
                                                    collapseAll,
                                                    reset,
                                                    keyPrefix,
                                                }) => {


    const colSpan = isExcluded ? 9 : 8;

    // Avoid a layout jump when reaching the last page with empty rows.
    const emptyRows =
        page > 0 ? Math.max(0, (1 + page) * rowsPerPage - orders.length) : 0

    return <Table aria-label='collapsible table'>
        <TableHead>
            <TableRow>
                <TableCell/>
                <TableCell sx={{minWidth: '135px'}}>№ замовлення</TableCell>
                <TableCell>Клієнт</TableCell>
                <TableCell>Адреса</TableCell>
                <TableCell sx={{minWidth: '135px'}}>Час доставки</TableCell>
                <TableCell>Менеджер</TableCell>
                {isExcluded && <TableCell>Причина вилучення</TableCell>}
                <TableCell/>
            </TableRow>
        </TableHead>
        <TableBody>
            {/*TODO: [Tolik] Think about keys */}
            {orders
                .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                .map((order, index) => {
                    return (
                        <OrderTableRow
                            isExcluded={isExcluded}
                            order={order}
                            key={keyPrefix + index}
                            keyPrefix={keyPrefix + 'subTable'}
                            updateOrder={updateOrder}
                            expandAll={expandAll}
                            collapseAll={collapseAll}
                            reset={reset}
                            colSpan={colSpan}
                        />
                    )
                })}
            {emptyRows > 0 && (
                <TableRow
                    style={{
                        height: 53 * emptyRows
                    }}
                >
                    <TableCell colSpan={colSpan}/>
                </TableRow>
            )}
        </TableBody>
    </Table>
}