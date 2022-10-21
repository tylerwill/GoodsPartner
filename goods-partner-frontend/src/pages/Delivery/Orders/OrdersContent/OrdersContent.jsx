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
import Grid from "@mui/material/Grid";
import ChooseAddressDialog from "./ChooseAddressDialog/ChooseAddressDialog";

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
                <TableCell>9:00 - 18:00</TableCell>
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

                            <AdditionalInfo order={order} updateOrder={updateOrder}/>
                        </Box>
                    </Collapse>
                </TableCell>
            </TableRow>
        </>
    );
}

// TODO: [Max UI] Move into separate file
const AdditionalInfo = ({order, updateOrder}) => {
    const from = order.deliveryStart ?? "09:00";
    const to = order.deliveryFinish ?? "18:00";

    const handleChangeFrom = (event) => {
        const newOrder = {...order, deliveryStart: event.target.value};
        updateOrder(newOrder);
    };
    const handleChangeTo = (event) => {
        const newOrder = {...order, deliveryFinish: event.target.value};
        updateOrder(newOrder);
    };

    const handleFreeze = (event) => {
        const newOrder = {...order, frozen:event.target.checked};
        updateOrder(newOrder);
    };

    return (
        <Grid sx={{mt: 2, p: 2, background: 'rgba(0, 0, 0, 0.02)', borderRadius: '6px'}} container spacing={2}>

            <Grid item xs={4}>
                <Typography sx={{mb: 1}} variant="caption" display="block" gutterBottom>
                    Діапазон доставки
                </Typography>
                <Box>
                    <Select
                        value={from}
                        label="Age"
                        onChange={handleChangeFrom}
                        sx={{minWidth: "140px", height: "40px", mr: 1}}
                    >
                        <MenuItem value={"09:00"}>9:00</MenuItem>
                        <MenuItem value={"10:00"}>10:00</MenuItem>
                        <MenuItem value={"11:00"}>11:00</MenuItem>
                        <MenuItem value={"12:00"}>12:00</MenuItem>
                        <MenuItem value={"13:00"}>13:00</MenuItem>
                        <MenuItem value={"14:00"}>14:00</MenuItem>
                        <MenuItem value={"15:00"}>15:00</MenuItem>
                        <MenuItem value={"16:00"}>16:00</MenuItem>
                        <MenuItem value={"17:00"}>17:00</MenuItem>
                        <MenuItem value={"18:00"}>18:00</MenuItem>
                    </Select>
                    {/*TODO: [UI Max] Move select creation into different component*/}
                    <Select
                        value={to}
                        label="Age"
                        onChange={handleChangeTo}
                        sx={{minWidth: "140px", height: "40px"}}
                    >
                        <MenuItem value={"10:00"}>10:00</MenuItem>
                        <MenuItem value={"11:00"}>11:00</MenuItem>
                        <MenuItem value={"12:00"}>12:00</MenuItem>
                        <MenuItem value={"13:00"}>13:00</MenuItem>
                        <MenuItem value={"14:00"}>14:00</MenuItem>
                        <MenuItem value={"15:00"}>15:00</MenuItem>
                        <MenuItem value={"16:00"}>16:00</MenuItem>
                        <MenuItem value={"17:00"}>17:00</MenuItem>
                        <MenuItem value={"18:00"}>18:00</MenuItem>
                        <MenuItem value={"19:00"}>19:00</MenuItem>
                    </Select>
                </Box>
            </Grid>
            <Grid item xs={4}>
                <Typography sx={{mb: 1}} variant="caption" display="block" gutterBottom>
                    Заморозка
                </Typography>
                <Box>
                    <FormControlLabel onChange={(e)=>handleFreeze(e)} checked={order.frozen} control={<Checkbox/>} label="Потребує заморозки"/>
                </Box>
            </Grid>
            <Grid item xs={4}>
                <Typography sx={{mb: 1}} variant="caption" display="block" gutterBottom>
                    Коментар
                </Typography>
                <Typography variant="caption" display="block" gutterBottom>
                    {order.comment?.length === 0 ? '-' : order.comment}
                </Typography>
            </Grid>
        </Grid>)
}

export default OrdersContent;
