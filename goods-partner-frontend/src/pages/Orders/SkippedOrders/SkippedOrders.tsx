import React, {useCallback} from 'react';
import Box from "@mui/material/Box";
import {Button, Checkbox} from "@mui/material";
import UnfoldMoreIcon from "@mui/icons-material/UnfoldMore";
import UnfoldLessIcon from "@mui/icons-material/UnfoldLess";
import Paper from "@mui/material/Paper";
import TableContainer from "@mui/material/TableContainer";
import Table from "@mui/material/Table";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import TableCell from "@mui/material/TableCell";
import TableBody from "@mui/material/TableBody";
import OrderRow from "./OrderRow/OrderRow";
import TablePagination from "@mui/material/TablePagination";
import {useAppDispatch, useAppSelector} from "../../../hooks/redux-hooks";
import {
    deleteSkippedOrders,
    deselectAll,
    rescheduleSkippedOrders,
    selectAll,
    setDeleteOrdersDialogOpen,
    setRescheduleDialogOpen
} from "../../../features/orders/ordersSlice";
import RescheduleDialog from "./RescheduleDialog/RescheduleDialog";
import DeleteConfirmDialog from "./DeleteConfirmDialog/DeleteConfirmDialog";

const SkippedOrders = () => {
    const dispatch = useAppDispatch();
    const {skippedOrders, allSelected, selectedOrderIds, rescheduleDialogOpen, deleteOrdersDialogOpen}
        = useAppSelector(state => state.orders);

    const hasSelected = selectedOrderIds.length !== 0;

    const setDialogOpenHandler = (isOpen: boolean) => dispatch(setRescheduleDialogOpen(isOpen));
    const setDeleteDialogOpenHandler = (isOpen: boolean) => dispatch(setDeleteOrdersDialogOpen(isOpen));

    const reschedule = (rescheduleDate: string) => {
        dispatch(rescheduleSkippedOrders({rescheduleDate, orderIds: selectedOrderIds}));
    }

    const deleteOrders = () => {
        dispatch(deleteSkippedOrders(selectedOrderIds));
    }


    const [page, setPage] = React.useState(0);
    const [rowsPerPage, setRowsPerPage] = React.useState(10);

    const [collapseAll, setCollapseAll] = React.useState(false);
    const [expandAll, setExpandAll] = React.useState(false);

    const collapseAllHandler = useCallback(() => {
        setCollapseAll(true);
        setExpandAll(false);
    }, []);

    const expandAllHandler = useCallback(() => {
        setExpandAll(true);
        setCollapseAll(false);
    }, []);

    const reset = useCallback(() => {
        setExpandAll(false);
        setCollapseAll(false);
    }, []);

    const handleChangePage = (event: any, newPage: number) => {
        setPage(newPage);
    };

    const handleChangeRowsPerPage = (event: React.ChangeEvent<HTMLInputElement>) => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPage(0);
    };

    // Avoid a layout jump when reaching the last page with empty rows.
    const emptyRows =
        page > 0 ? Math.max(0, (1 + page) * rowsPerPage - skippedOrders.length) : 0;

    const handleSelectAllClick = (event: React.ChangeEvent<HTMLInputElement>) => {
        if (event.target.checked) {
            dispatch(selectAll());
            return;
        }
        dispatch(deselectAll());
    };

    console.log('skippedOrders', skippedOrders);
    return (
        <Box>
            <Box sx={{display: "flex", mb: 2, justifyContent: 'space-between'}}>
                <Box>
                    <Button onClick={expandAllHandler}>
                        <UnfoldMoreIcon sx={{mr: 1, transform: 'rotate(45deg)', width: '0.75em'}}/> розгорнути всі
                    </Button>
                    <Button onClick={collapseAllHandler}>
                        <UnfoldLessIcon sx={{mr: 1, transform: 'rotate(45deg)', width: '0.75em'}}/> Згорнути всі
                    </Button>
                </Box>
                <Box>
                    <Button variant="outlined" color={"error"}
                            onClick={() => setDeleteDialogOpenHandler(true)}
                            disabled={!hasSelected}>
                        Видалити
                    </Button>
                    <Button variant="contained" color={"primary"}
                            sx={{ml: 2}}
                            onClick={() => setDialogOpenHandler(true)}
                            disabled={!hasSelected}>
                        Запланувати доставку
                    </Button>
                </Box>
            </Box>

            <Paper variant={"outlined"}>
                <TableContainer style={{
                    minHeight: '800px',
                    borderTop: '1px solid rgba(0, 0, 0, 0.1)'
                }}>
                    <Table aria-label="collapsible table">
                        <TableHead>
                            <TableRow>
                                <TableCell/>
                                <TableCell padding="checkbox">
                                    <Checkbox
                                        color="primary"
                                        checked={allSelected}
                                        onChange={handleSelectAllClick}
                                        inputProps={{
                                            'aria-label': 'select all desserts',
                                        }}
                                    />
                                </TableCell>
                                <TableCell sx={{minWidth: '135px'}}>№ замовлення</TableCell>
                                <TableCell>Дата</TableCell>
                                <TableCell>Клієнт</TableCell>
                                <TableCell>Адреса</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {/*TODO: [Tolik] Think about keys */}
                            {skippedOrders.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                                .map((order, index) => {
                                    return (
                                        <OrderRow order={order} key={'skippedOrders' + index}
                                                  keyPrefix={'skippedOrders' + "subTable"}
                                                  expandAll={expandAll} collapseAll={collapseAll} reset={reset}
                                        />)
                                })}
                            {emptyRows > 0 && (
                                <TableRow
                                    style={{
                                        height: 53 * emptyRows,
                                    }}
                                >
                                    <TableCell colSpan={7}/>
                                </TableRow>
                            )}
                        </TableBody>
                    </Table>
                </TableContainer>
                <TablePagination
                    rowsPerPageOptions={[10, 25, 50]}
                    component="div"
                    count={skippedOrders.length}
                    rowsPerPage={rowsPerPage}
                    page={page}
                    onPageChange={handleChangePage}
                    onRowsPerPageChange={handleChangeRowsPerPage}
                />
            </Paper>
            <RescheduleDialog open={rescheduleDialogOpen} setOpen={setDialogOpenHandler}
                              onAction={reschedule}/>
            <DeleteConfirmDialog open={deleteOrdersDialogOpen} setOpen={setDeleteDialogOpenHandler}
                                 onAction={deleteOrders}/>
        </Box>
    );
}


export default SkippedOrders;