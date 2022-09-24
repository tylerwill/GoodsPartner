import React from "react";
import {CardContent} from "@mui/material";
import BasicTabs from "../../../hoc/BasicTabs/BasicTabs";
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

import InfoIcon from '@mui/icons-material/Info';
import {Edit} from "@mui/icons-material";
import Collapse from "@mui/material/Collapse";
import Box from "@mui/material/Box";

const OrdersContent = ({orders}) => {
    const invalidOrders = orders
        .filter(order => order.mapPoint.status === "UNKNOWN");

    const tabLabels = [`Всі замовлення (${orders.length})`,
        `потребують уточнення (${invalidOrders.length})`]


    return <CardContent>
        <BasicTabs labels={tabLabels}>
            {createTable([...orders], "all")}
            {createTable(invalidOrders, "invalid")}
        </BasicTabs>
    </CardContent>
}

const createTable = (orders, keyPrefix) => {
    return (<TableContainer component={Paper}>
            <Table aria-label="collapsible table">
                <TableHead>
                    <TableRow>
                        <TableCell/>
                        <TableCell>№ замовлення</TableCell>
                        <TableCell>Контрагент</TableCell>
                        <TableCell>Адреса</TableCell>
                        <TableCell>Менеджер</TableCell>
                        <TableCell>Вага замовлення</TableCell>
                        <TableCell>Час доставки</TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    {/*TODO: [Tolik] Think about keys */}
                    {orders.map((order, index) => {
                        return (<Row order={order} key={keyPrefix + index} keyPrefix={keyPrefix + "subTable"}/>)
                    })}
                </TableBody>
            </Table>
        </TableContainer>
    );
}


const Row = ({order, keyPrefix}) => {
    const [open, setOpen] = React.useState(false);
    const isInvalid = order.mapPoint.status === "UNKNOWN";
    return (
        <>
            <TableRow sx={{'& > *': {borderBottom: 'unset'}}}>
                <TableCell>
                    <IconButton
                        aria-label="expand row"
                        size="small"
                        onClick={() => setOpen(!open)}
                    >
                        {open ? <KeyboardArrowUpIcon/> : <KeyboardArrowDownIcon/>}
                    </IconButton>
                </TableCell>
                <TableCell component="th" scope="row">
                    {order.orderNumber}
                </TableCell>
                <TableCell>{order.clientName}</TableCell>
                <TableCell>
                    <div style={{
                        display: 'flex',
                        alignItems: 'center',
                    }}>
                        {isInvalid && <InfoIcon sx={{color: '#FFC107', marginRight: '5px'}}>
                            <Edit fontSize="medium"/>
                        </InfoIcon>}
                        <span>{order.address}</span>
                    </div>

                </TableCell>
                <TableCell>{order.managerFullName}</TableCell>
                <TableCell>{order.orderWeight} кг</TableCell>
                <TableCell>9:00 - 18:00</TableCell>
            </TableRow>
            <TableRow>
                <TableCell style={{paddingBottom: 0, paddingTop: 0}} colSpan={7}>
                    <Collapse in={open} timeout="auto" unmountOnExit>
                        <Box sx={{margin: 1}}>
                            <TableContainer component={Paper}>
                                <Table sx={{minWidth: 650}} size="small" >
                                    <TableHead>
                                        <TableRow>
                                            <TableCell sx={{width: '500px'}}>Артикул</TableCell>
                                            <TableCell>Кількість</TableCell>
                                            <TableCell>Вага</TableCell>
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
                                                <TableCell> {product.unitWeight} кг</TableCell>
                                                <TableCell> {product.totalProductWeight} кг</TableCell>
                                            </TableRow>
                                        ))}
                                    </TableBody>
                                </Table>
                            </TableContainer>
                        </Box>
                    </Collapse>
                </TableCell>
            </TableRow>
        </>
    );
}


export default OrdersContent;
