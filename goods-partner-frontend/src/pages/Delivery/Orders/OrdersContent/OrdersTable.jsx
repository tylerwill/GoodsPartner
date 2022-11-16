import React, {useCallback} from "react";
import {Button, Stack} from "@mui/material";
import TableContainer from "@mui/material/TableContainer";
import Paper from "@mui/material/Paper";
import Table from "@mui/material/Table";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import TableCell from "@mui/material/TableCell";
import TableBody from "@mui/material/TableBody";
import IconButton from "@mui/material/IconButton";
import KeyboardArrowUpIcon from "@mui/icons-material/KeyboardArrowUp";
import KeyboardArrowDownIcon from "@mui/icons-material/KeyboardArrowDown";
import {EditOutlined} from "@mui/icons-material";
import Collapse from "@mui/material/Collapse";
import Box from "@mui/material/Box";
import OrderAdditionalInfo from "./OrderAdditionalInfo/OrderAdditionalInfo";
import TablePagination from "@mui/material/TablePagination";

import UnfoldMoreIcon from '@mui/icons-material/UnfoldMore';
import UnfoldLessIcon from '@mui/icons-material/UnfoldLess';

const OrdersTable = ({orders, keyPrefix, setOrderAddressDialogOpen, setEditedOrder, updateOrder}) => {
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

    const handleChangePage = (event, newPage) => {
        setPage(newPage);
    };

    const handleChangeRowsPerPage = (event) => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPage(0);
    };

    // Avoid a layout jump when reaching the last page with empty rows.
    const emptyRows =
        page > 0 ? Math.max(0, (1 + page) * rowsPerPage - orders.length) : 0;

    return (
        <Box>
            <Box sx={{display: "flex", mb: 2}}>
                <Button onClick={expandAllHandler}>
                    <UnfoldMoreIcon sx={{mr: 1, transform: 'rotate(45deg)', width: '0.75em'}}/> розгорнути всі
                </Button>
                <Button onClick={collapseAllHandler}>
                    <UnfoldLessIcon sx={{mr: 1, transform: 'rotate(45deg)', width: '0.75em'}}/> Згорнути всі
                </Button>
            </Box>

            <Paper variant={"outlined"}>
                <TableContainer style={{
                    borderTop: '1px solid rgba(0, 0, 0, 0.1)'
                }}>
                    <Table aria-label="collapsible table">
                        <TableHead>
                            <TableRow>
                                <TableCell/>
                                <TableCell sx={{minWidth: '135px'}}>№ замовлення</TableCell>
                                <TableCell>Вага</TableCell>
                                <TableCell>Клієнт</TableCell>
                                <TableCell>Адреса</TableCell>
                                <TableCell sx={{minWidth: '135px'}}>Час доставки</TableCell>
                                <TableCell>Менеджер</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {/*TODO: [Tolik] Think about keys */}
                            {orders.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                                .map((order, index) => {
                                    return (
                                        <Row order={order} key={keyPrefix + index} keyPrefix={keyPrefix + "subTable"}
                                             setOrderAddressDialogOpen={setOrderAddressDialogOpen}
                                             setEditedOrder={setEditedOrder} updateOrder={updateOrder}
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
                    count={orders.length}
                    rowsPerPage={rowsPerPage}
                    page={page}
                    onPageChange={handleChangePage}
                    onRowsPerPageChange={handleChangeRowsPerPage}
                />
            </Paper>
        </Box>
    );
}

const Row = ({order, keyPrefix, setOrderAddressDialogOpen, setEditedOrder, updateOrder, collapseAll, expandAll, reset}) => {
    const from = order.deliveryStart ?? "09:00";
    const to = order.deliveryFinish ?? "18:00";
    const [orderTableOpen, setOrderTableOpen] = React.useState(false);

    const isTableOpened = expandAll || (orderTableOpen && !collapseAll);

    const isInvalid = order.mapPoint.status === "UNKNOWN";
    return (
        <>
            <TableRow sx={{'& > *': {borderBottom: 'unset'}}}>
                <TableCell>
                    <IconButton
                        aria-label="expand row"
                        size="small"
                        onClick={() => {setOrderTableOpen(!orderTableOpen); reset()}}
                    >
                        {isTableOpened ? <KeyboardArrowUpIcon/> : <KeyboardArrowDownIcon/>}
                    </IconButton>
                </TableCell>
                <TableCell component="th" scope="row">
                    {order.orderNumber}
                </TableCell>
                <TableCell>{order.orderWeight} кг</TableCell>
                <TableCell>{order.clientName}</TableCell>
                <TableCell>
                    <Stack
                        direction={"row"}
                        alignItems={'center'}>

                        <EditOutlined
                            sx={{cursor: 'pointer'}}
                            onClick={() => {
                                setEditedOrder(order);
                                setOrderAddressDialogOpen(true);
                            }}/>
                        {
                            // TODO: [UI] Shitty code
                            isInvalid ? <Box sx={{ml: 1, backgroundColor: '#FFECB3'}}>{order.address}</Box>
                                : <Box sx={{ml: 1}}>{order.address}</Box>
                        }
                    </Stack>

                </TableCell>
                <TableCell>{from} - {to}</TableCell>
                <TableCell>{order.managerFullName}</TableCell>
            </TableRow>
            <TableRow>
                <TableCell style={{paddingBottom: 0, paddingTop: 0}} colSpan={7}>
                    <Collapse in={isTableOpened} timeout="auto" unmountOnExit>
                        <Box sx={{margin: 2}}>
                            <TableContainer component={Paper}>
                                <Table sx={{minWidth: 650}} size="small">
                                    <TableHead>
                                        <TableRow>
                                            <TableCell sx={{width: '500px'}}>Артикул</TableCell>
                                            <TableCell>Кількість</TableCell>
                                            <TableCell>Упаковка</TableCell>
                                            <TableCell>Загальна вага</TableCell>
                                        </TableRow>
                                    </TableHead>
                                    <TableBody>
                                        {order.products.map((product) => (
                                            <TableRow
                                                key={keyPrefix + product.productName}
                                                sx={{'&:last-child td, &:last-child th': {border: 0}}}
                                            >
                                                <TableCell component="th" scope="row">
                                                    {product.productName}
                                                </TableCell>
                                                <TableCell> {product.amount}</TableCell>
                                                <TableCell> {product.unitWeight} {product.measure}</TableCell>
                                                <TableCell> {product.totalProductWeight} кг</TableCell>
                                            </TableRow>
                                        ))}
                                    </TableBody>
                                </Table>
                            </TableContainer>

                            <OrderAdditionalInfo order={order} updateOrder={updateOrder}/>
                        </Box>
                    </Collapse>
                </TableCell>
            </TableRow>
        </>
    );
}

export default OrdersTable;
