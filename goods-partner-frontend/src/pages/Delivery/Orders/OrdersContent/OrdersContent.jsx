import React from "react";
import {Checkbox, FormControlLabel, MenuItem, Select, Stack, Typography} from "@mui/material";
import BasicTabs from "../../../../hoc/BasicTabs/BasicTabs";
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
import ChooseAddressDialog from "./ChooseAddressDialog/ChooseAddressDialog";
import OrderAdditionalInfo from "./OrderAdditionalInfo/OrderAdditionalInfo";

const OrdersContent = ({orders, updatePreviewOrderAddress, updateOrder}) => {
    const [orderAddressDialogOpen, setOrderAddressDialogOpen] = React.useState(false);
    const [editedOrder, setEditedOrder] = React.useState(null);

    const invalidOrders = orders
        .filter(order => order.mapPoint.status === "UNKNOWN");


    const tabLabels = [
        {name: `Всі замовлення (${orders.length})`, enabled: true},
        {name: `потребують уточнення (${invalidOrders.length})`, enabled: true}
    ];

    return <Box>
        <BasicTabs labels={tabLabels}>
            {createTable(orders, "all", setOrderAddressDialogOpen, setEditedOrder, updateOrder)}
            {createTable(invalidOrders, "invalid", setOrderAddressDialogOpen, setEditedOrder, updateOrder)}
        </BasicTabs>

        {
            orderAddressDialogOpen && <ChooseAddressDialog open={orderAddressDialogOpen}
                                                           handleClose={() => setOrderAddressDialogOpen(false)}
                                                           order={editedOrder}
                                                           updatePreviewOrderAddress={updatePreviewOrderAddress}/>
        }
    </Box>
}

const createTable = (orders, keyPrefix, setOrderAddressDialogOpen, setEditedOrder, updateOrder) => {
    return (<TableContainer component={Paper} style={{
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
                    {orders.map((order, index) => {
                        return (<Row order={order} key={keyPrefix + index} keyPrefix={keyPrefix + "subTable"}
                                     setOrderAddressDialogOpen={setOrderAddressDialogOpen}
                                     setEditedOrder={setEditedOrder} updateOrder={updateOrder}/>)
                    })}
                </TableBody>
            </Table>
        </TableContainer>
    );
}


const Row = ({order, keyPrefix, setOrderAddressDialogOpen, setEditedOrder, updateOrder}) => {
    const from = order.deliveryStart ?? "09:00";
    const to = order.deliveryFinish ?? "18:00";
    const [orderTableOpen, setOrderTableOpen] = React.useState(false);


    const isInvalid = order.mapPoint.status === "UNKNOWN";
    return (
        <>
            <TableRow sx={{'& > *': {borderBottom: 'unset'}}}>
                <TableCell>
                    <IconButton
                        aria-label="expand row"
                        size="small"
                        onClick={() => setOrderTableOpen(!orderTableOpen)}
                    >
                        {orderTableOpen ? <KeyboardArrowUpIcon/> : <KeyboardArrowDownIcon/>}
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
                    <Collapse in={orderTableOpen} timeout="auto" unmountOnExit>
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

export default OrdersContent;
