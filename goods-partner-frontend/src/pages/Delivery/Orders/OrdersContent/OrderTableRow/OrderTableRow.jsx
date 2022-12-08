import {useAppDispatch} from "../../../../../hooks/redux-hooks";
import React, {useCallback} from "react";
import {
    setAddressDialogOpen,
    setOrderForAddressModification
} from "../../../../../features/delivery-orders/deliveryOrdersSlice";
import TableRow from "@mui/material/TableRow";
import TableCell from "@mui/material/TableCell";
import IconButton from "@mui/material/IconButton";
import KeyboardArrowUpIcon from "@mui/icons-material/KeyboardArrowUp";
import KeyboardArrowDownIcon from "@mui/icons-material/KeyboardArrowDown";
import Box from "@mui/material/Box";
import Collapse from "@mui/material/Collapse";
import TableContainer from "@mui/material/TableContainer";
import Paper from "@mui/material/Paper";
import Table from "@mui/material/Table";
import TableHead from "@mui/material/TableHead";
import TableBody from "@mui/material/TableBody";
import OrderAdditionalInfo from "../OrderAdditionalInfo/OrderAdditionalInfo";


const OrderTableRow = ({order, keyPrefix, updateOrder, collapseAll, expandAll, reset}) => {
    const dispatch = useAppDispatch();
    const from = order.deliveryStart ?? "09:00";
    const to = order.deliveryFinish ?? "18:00";
    const [orderTableOpen, setOrderTableOpen] = React.useState(false);

    const isTableOpened = expandAll || (orderTableOpen && !collapseAll);

    const handleOpen = useCallback(() => {
            dispatch(setOrderForAddressModification(order));
            dispatch(setAddressDialogOpen(true));
        }, [dispatch, setAddressDialogOpen]
    );

    const isInvalid = order.mapPoint.status === "UNKNOWN";
    return (
        <>
            <TableRow sx={{'& > *': {borderBottom: 'unset'}}}>
                <TableCell>
                    <IconButton
                        aria-label="expand row"
                        size="small"
                        onClick={() => {
                            setOrderTableOpen(!orderTableOpen);
                            reset()
                        }}
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
                    {
                        isInvalid ? <Box sx={{ml: 1, backgroundColor: '#FFECB3'}}>{order.address}</Box>
                            : <Box sx={{ml: 1}}>{order.address}</Box>
                    }

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
export default OrderTableRow;