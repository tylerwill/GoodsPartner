import {BaseSyntheticEvent, FC, useState} from 'react'
import Box from '@mui/material/Box'
import {Button} from '@mui/material'
import UnfoldMoreIcon from '@mui/icons-material/UnfoldMore'
import UnfoldLessIcon from '@mui/icons-material/UnfoldLess'
import Paper from '@mui/material/Paper'
import TableContainer from '@mui/material/TableContainer'
import Table from '@mui/material/Table'
import TableHead from '@mui/material/TableHead'
import TableRow from '@mui/material/TableRow'
import TableCell from '@mui/material/TableCell'
import TableBody from '@mui/material/TableBody'
import TablePagination from '@mui/material/TablePagination'
import ProductRow from './OrderRow/OrderRow'
import Order from "../../../model/Order";
import {useOrdersSearch} from "../useOrdersSearch";
import TextField from "@mui/material/TextField";
import {useCollapseExpand} from "../../../hooks/useCollapseExpand";
import {useTablePaging} from "../../../hooks/useTablePaging";

interface OrdersBasicTableProps {
    orders: Order[] | undefined
}

export const OrdersBasicTable: FC<OrdersBasicTableProps> = ({orders = []}) => {
    const [filteredOrders, filter] = useOrdersSearch(orders);
    const [page, rowsPerPage, handleChangePage, handleChangeRowsPerPage, emptyRows] = useTablePaging();
    const [collapseAll, expandAll, collapseAllHandler, expandAllHandler, reset] = useCollapseExpand();

    return (
        <Box>
            <Box sx={{display: 'flex', mb: 2}}>
                <Button onClick={expandAllHandler}>
                    <UnfoldMoreIcon
                        sx={{mr: 1, transform: 'rotate(45deg)', width: '0.75em'}}
                    />{' '}
                    розгорнути всі
                </Button>
                <Button onClick={collapseAllHandler}>
                    <UnfoldLessIcon
                        sx={{mr: 1, transform: 'rotate(45deg)', width: '0.75em'}}
                    />{' '}
                    Згорнути всі
                </Button>
                <TextField
                    sx={{backgroundColor: '#fff', width: '35%', ml: 3}}
                    size={"small"}
                    onChange={e => filter(e.target.value)}
                    label="Пошук" variant={'outlined'}/>
            </Box>

            <Paper variant={'outlined'}>
                <TableContainer
                    style={{
                        minHeight: '800px',
                        borderTop: '1px solid rgba(0, 0, 0, 0.1)'
                    }}
                >
                    <Table aria-label='collapsible table'>
                        <TableHead>
                            <TableRow>
                                <TableCell/>
                                <TableCell sx={{minWidth: '135px'}}>№ замовлення</TableCell>
                                <TableCell>Дата</TableCell>
                                <TableCell>Клієнт</TableCell>
                                <TableCell>Адреса</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {/*TODO: [Tolik] Think about keys */}
                            {filteredOrders
                                .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                                .map((order, index) => {
                                    return (
                                        <ProductRow
                                            order={order}
                                            key={'basicOrderSubTable' + index + order.id}
                                            keyPrefix={'basicOrderSubTable'}
                                            expandAll={expandAll}
                                            collapseAll={collapseAll}
                                            reset={reset}
                                        />
                                    )
                                })}
                            {emptyRows > 0 && (
                                <TableRow
                                    style={{
                                        height: 53 * emptyRows
                                    }}
                                >
                                    <TableCell colSpan={7}/>
                                </TableRow>
                            )}
                        </TableBody>
                    </Table>
                </TableContainer>
                <TablePagination
                    rowsPerPageOptions={[25, 50, 100]}
                    component='div'
                    count={filteredOrders.length}
                    rowsPerPage={rowsPerPage}
                    page={page}
                    onPageChange={handleChangePage}
                    onRowsPerPageChange={handleChangeRowsPerPage}
                />
            </Paper>
        </Box>
    )
}

