import React, {ChangeEvent, FC} from 'react'
import Box from '@mui/material/Box'
import {Button, Checkbox} from '@mui/material'
import UnfoldMoreIcon from '@mui/icons-material/UnfoldMore'
import UnfoldLessIcon from '@mui/icons-material/UnfoldLess'
import Paper from '@mui/material/Paper'
import TableContainer from '@mui/material/TableContainer'
import Table from '@mui/material/Table'
import TableHead from '@mui/material/TableHead'
import TableRow from '@mui/material/TableRow'
import TableCell from '@mui/material/TableCell'
import TableBody from '@mui/material/TableBody'
import OrderRow from './OrderRow/OrderRow'
import TablePagination from '@mui/material/TablePagination'
import {useActions, useAppSelector} from '../../../hooks/redux-hooks'

import RescheduleDialog from './RescheduleDialog/RescheduleDialog'
import {useDeleteOrdersMutation, useRescheduleOrdersMutation} from '../../../api/orders/orders.api'
import {ConfirmationDialog} from "../../../components/ConfirmationDialog/ConfirmationDialog";
import Order from "../../../model/Order";
import TextField from "@mui/material/TextField";
import {useOrdersSearch} from "../useOrdersSearch";
import {useCollapseExpand} from "../../../hooks/useCollapseExpand";
import {useTablePaging} from "../../../hooks/useTablePaging";

interface OrdersTableWithRescheduleProps {
    orders: Order[] | undefined,
    hasExcluded: boolean
}


const OrdersTableWithReschedule: FC<OrdersTableWithRescheduleProps> = ({orders=[], hasExcluded}) => {
    const [filteredOrders, filter] = useOrdersSearch(orders);
    const {
        allSelected,
        selectedOrderIds,
        rescheduleDialogOpen,
        deleteOrdersDialogOpen
    } = useAppSelector(state => state.orders)

    const {deselectAll,
        selectAll,
        setDeleteOrdersDialogOpen,
        setRescheduleDialogOpen} = useActions();

    const [rescheduleOrders] = useRescheduleOrdersMutation()
    const [deleteOrders] = useDeleteOrdersMutation()

    const setDialogOpenHandler = (isOpen: boolean) => setRescheduleDialogOpen(isOpen);
    const setDeleteDialogOpenHandler = (isOpen: boolean) => setDeleteOrdersDialogOpen(isOpen)

    const reschedule = (rescheduleDate: string) => {
        rescheduleOrders({rescheduleDate, orderIds: selectedOrderIds})
        deselectAll();
    }

    const deleteOrdersHandler = () => {
        deleteOrders(selectedOrderIds)
        deselectAll()
    }

    const [page, rowsPerPage, handleChangePage, handleChangeRowsPerPage, emptyRows]
        = useTablePaging(orders.length);

    const [collapseAll, expandAll, collapseAllHandler, expandAllHandler, reset] = useCollapseExpand();

    const handleSelectAllClick = (event: ChangeEvent<HTMLInputElement>) => {
        if (event.target.checked) {
            selectAll(orders)
        }else {
            deselectAll()
        }
    }

    const hasSelected = selectedOrderIds.length !== 0

    return (
        <Box>

            <Box sx={{display: 'flex', mb: 2, justifyContent: 'space-between'}}>

                <Box  flex={1} display={'flex'}>
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
                <Box>

                    <Button
                        variant='outlined'
                        color={'error'}
                        onClick={() => setDeleteDialogOpenHandler(true)}
                        disabled={!hasSelected}
                    >
                        Видалити
                    </Button>
                    <Button
                        variant='contained'
                        color={'primary'}
                        sx={{ml: 2}}
                        onClick={() => setDialogOpenHandler(true)}
                        disabled={!hasSelected}
                    >
                        Запланувати доставку
                    </Button>
                </Box>
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
                                <TableCell padding={'checkbox'}/>
                                <TableCell padding='checkbox'>
                                    <Checkbox
                                        color='primary'
                                        checked={allSelected}
                                        onChange={handleSelectAllClick}
                                        inputProps={{
                                            'aria-label': 'select all desserts'
                                        }}
                                    />
                                </TableCell>
                                <TableCell sx={{minWidth: '135px'}}>№ замовлення</TableCell>
                                <TableCell>Дата</TableCell>
                                <TableCell>Клієнт</TableCell>
                                <TableCell>Адреса</TableCell>
                                {hasExcluded && <TableCell>Причина вилучення</TableCell>}
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {/*TODO: [Tolik] Think about keys */}
                            {filteredOrders
                                .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                                .map((order, index) => {
                                    return (
                                        <OrderRow
                                            order={order}
                                            key={'skippedOrders' + index}
                                            keyPrefix={'skippedOrders' + 'subTable'}
                                            expandAll={expandAll}
                                            collapseAll={collapseAll}
                                            reset={reset}
                                            hasExcluded={hasExcluded}
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
            <RescheduleDialog
                open={rescheduleDialogOpen}
                setOpen={setDialogOpenHandler}
                onAction={reschedule}
            />
            <ConfirmationDialog
                title={"Видалити замовлення"}
                text={"Ви впевнені, що бажаєте видалити замовлення? Цю дію не можна буде відмінити."}
                open={deleteOrdersDialogOpen}
                setOpen={setDeleteDialogOpenHandler}
                onAction={deleteOrdersHandler}
            />
        </Box>
    )
}

export default OrdersTableWithReschedule
