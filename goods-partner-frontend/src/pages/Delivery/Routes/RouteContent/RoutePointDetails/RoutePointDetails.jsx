import React from 'react';
import Box from "@mui/material/Box";
import {
    Button,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    FormControl,
    MenuItem,
    Select,
    styled,
    Typography
} from "@mui/material";
import Grid from "@mui/material/Grid";
import InfoTableItem from "../../../../../components/InfoTableItem/InfoTableItem";
import TableContainer from "@mui/material/TableContainer";
import Paper from "@mui/material/Paper";
import Table from "@mui/material/Table";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import TableCell from "@mui/material/TableCell";
import TableBody from "@mui/material/TableBody";
import {useSelector} from "react-redux";
import {selectOrdersByIds} from "../../../../../features/currentDelivery/currentDeliverySlice";


const RoutePointDetails = ({routePoint, number, updateRoutePoint}) => {
    return (<Box sx={{
        width: '100%', background: 'rgba(0, 0, 0, 0.02)',
        borderRadius: '6px', p: 2
    }}>
        <RoutePointDetailsHeader
            routePoint={routePoint} number={number} updateRoutePoint={updateRoutePoint}/>
        <Box sx={{mt: 3}}>
            <RoutePointDetailsBody routePoint={routePoint}/>
        </Box>
    </Box>)

}

const RoutePointDetailsHeader = ({routePoint, number, updateRoutePoint}) => {
    const ordersIdsFromRoute = routePoint.orders.map(o => o.id);
    const [orderDialogOpen, setOrderDialogOpen] = React.useState(false);

    const ordersDetailedInfo = useSelector(state => selectOrdersByIds(state.currentDelivery, ordersIdsFromRoute));

    return (<Box sx={{width: '100%', display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
        <Typography sx={{fontWeight: "bold", maxWidth: '450px'}} variant="body2" component="h2">
            №{number}, {routePoint.address}
        </Typography>

        {/*<Button variant="outlined" disabled>Змінити машину</Button>*/}
        <Box sx={{display: 'flex'}}>
            <Button variant="text" sx={{mr: 2}} onClick={() => setOrderDialogOpen(true)}>Показати деталі</Button>
            <RoutePointSelect updateRoutePoint={updateRoutePoint} routePoint={routePoint}/>
        </Box>

        <RoutePointOrdersDialog open={orderDialogOpen} closeDialog={() => setOrderDialogOpen(false)}
                                routePoint={routePoint} ordersDetailedInfo={ordersDetailedInfo}
        />
    </Box>);
}

const RoutePointDetailsBody = ({routePoint}) => {
    const orderNumber = routePoint.orders[0].orderNumber;
    const completedAt = routePoint.completedAt === null ? '-' : routePoint.completedAt;
    return (<Grid container spacing={2}>
        <Grid item xs={4}>
            <InfoTableItem title={"Номер замовлення"} data={orderNumber}/>
        </Grid>
        <Grid item xs={4}>
            <InfoTableItem title={"Вага"} data={routePoint.addressTotalWeight + ' кг'}/>
        </Grid>
        <Grid item xs={4}>
            <InfoTableItem title={"Клієнт"} data={routePoint.clientName}/>
        </Grid>
        <Grid item xs={4}>
            <InfoTableItem title={"Прибуття, прогноз"} data={routePoint.expectedArrival}/>
        </Grid>
        <Grid item xs={4}>
            <InfoTableItem title={"Завершення, прогноз"} data={routePoint.expectedCompletion}/>
        </Grid>
        <Grid item xs={4}>
            <InfoTableItem title={"Завершення, факт"} data={completedAt}/>
        </Grid>

    </Grid>);
}

const RoutePointSelect = ({routePoint, updateRoutePoint}) => {
    const {status} = routePoint;
    const selectColor = getSelectColor(status);

    const statusToActionMap = {
        'PENDING': 'RESET',
        'DONE': 'COMPLETE',
        'SKIPPED': 'SKIP',
    }

    const handleChange = (event) => {
        updateRoutePoint(routePoint.id, statusToActionMap[event.target.value]);
    }
    const CustomSelect = styled(Select)(() => ({
        "&.MuiOutlinedInput-root": {
            "& fieldset": {
                borderColor: selectColor
            }
        },
        '& .MuiOutlinedInput-input': {
            padding: '4px 16px',
            textTransform: 'uppercase',
            fontSize: '13px',
            fontWeight: 500,
            color: selectColor
        }
    }));

    return <div>
        <FormControl>
            <CustomSelect
                value={status}
                onChange={handleChange}
                autoWidth
                MenuProps={{MenuListProps: {disablePadding: true}}}
            >
                <MenuItem value={'PENDING'}>В очікуванні</MenuItem>
                <MenuItem value={'DONE'}>Готово</MenuItem>
                <MenuItem value={'SKIPPED'}>Пропущено</MenuItem>
            </CustomSelect>
        </FormControl>
    </div>
}

function getSelectColor(status) {
    switch (status) {
        case 'PENDING':
            return '#1976D2'
        case 'DONE':
            return '#2E7D32'
        case 'SKIPPED':
            return '#ED6C02'
    }
}

const RoutePointOrdersDialog = ({open, closeDialog, routePoint, ordersDetailedInfo}) => {
    const commentText = ordersDetailedInfo.filter(order => order.comment).map(order => order.comment).join(",");
    ;

    return (<Dialog
        maxWidth={'lg'}
        open={open} onClose={closeDialog}>
        <DialogTitle>Деталі</DialogTitle>
        <DialogContent>
            <Grid container spacing={2}>
                <Grid item xs={12}>
                    <InfoTableItem title={"Пункт призначення"} data={routePoint.address}/>
                </Grid>
                <Grid item xs={12}>
                    <InfoTableItem title={"Коментар"} data={commentText.trim().length === 0 ? '-' : commentText}/>
                </Grid>
            </Grid>
            <TableContainer sx={{marginTop: 4}} component={Paper} style={{
                borderTop: '1px solid rgba(0, 0, 0, 0.1)'
            }}>
                <Table sx={{minWidth: 650}} size="small" aria-label="a dense table">
                    <TableHead>
                        <TableRow>
                            <TableCell sx={{minWidth: '170px'}}>Замовлення</TableCell>
                            <TableCell>Артикул</TableCell>
                            <TableCell>Кількість</TableCell>
                            <TableCell>Упаковка</TableCell>
                            <TableCell>Загальна вага</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {ordersDetailedInfo.map((order) => (

                            order.products.map(product => {
                                return (
                                    <TableRow
                                        key={'routePointDetails' + product.refKey}
                                        sx={{'&:last-child td, &:last-child th': {border: 0}}}
                                    >
                                        <TableCell>{order.orderNumber}</TableCell>
                                        <TableCell>{product.productName}</TableCell>
                                        <TableCell> {product.amount}</TableCell>
                                        <TableCell> {product.unitWeight} {product.measure}</TableCell>
                                        <TableCell> {product.totalProductWeight} кг</TableCell>

                                    </TableRow>);
                            })

                        ))}
                    </TableBody>
                </Table>
            </TableContainer>

        </DialogContent>
        <DialogActions>
            <Button sx={{mr: 2, mb: 1}} onClick={() => closeDialog()} variant={'contained'}>Закрити </Button>
        </DialogActions>
    </Dialog>)
}


export default RoutePointDetails;