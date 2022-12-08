import React from "react";
import {RoutePoint} from "../../../../../../model/RoutePoint";

interface RoutePointOrdersDialogProps {
    open: boolean,
    closeDialog: () => void,
    routePoint: RoutePoint,

}

const RoutePointOrdersDialog = () => {
    return "div";
}
//
// const RoutePointOrdersDialog = ({open, closeDialog, routePoint, ordersDetailedInfo}) => {
//     const commentText = ordersDetailedInfo.filter(order => order.comment).map(order => order.comment).join(",");
//     ;
//
//     return (<Dialog
//         maxWidth={'lg'}
//         open={open} onClose={closeDialog}>
//         <DialogTitle>Деталі</DialogTitle>
//         <DialogContent>
//             <Grid container spacing={2}>
//                 <Grid item xs={12}>
//                     <InfoTableItem title={"Пункт призначення"} data={routePoint.address}/>
//                 </Grid>
//                 <Grid item xs={12}>
//                     <InfoTableItem title={"Коментар"} data={commentText.trim().length === 0 ? '-' : commentText}/>
//                 </Grid>
//             </Grid>
//             <TableContainer sx={{marginTop: 4}} component={Paper} style={{
//                 borderTop: '1px solid rgba(0, 0, 0, 0.1)'
//             }}>
//                 <Table sx={{minWidth: 650}} size="small" aria-label="a dense table">
//                     <TableHead>
//                         <TableRow>
//                             <TableCell sx={{minWidth: '170px'}}>Замовлення</TableCell>
//                             <TableCell>Артикул</TableCell>
//                             <TableCell>Кількість</TableCell>
//                             <TableCell>Упаковка</TableCell>
//                             <TableCell>Загальна вага</TableCell>
//                         </TableRow>
//                     </TableHead>
//                     <TableBody>
//                         {ordersDetailedInfo.map((order) => (
//
//                             order.products.map(product => {
//                                 return (
//                                     <TableRow
//                                         key={'routePointDetails' + product.refKey}
//                                         sx={{'&:last-child td, &:last-child th': {border: 0}}}
//                                     >
//                                         <TableCell>{order.orderNumber}</TableCell>
//                                         <TableCell>{product.productName}</TableCell>
//                                         <TableCell> {product.amount}</TableCell>
//                                         <TableCell> {product.unitWeight} {product.measure}</TableCell>
//                                         <TableCell> {product.totalProductWeight} кг</TableCell>
//
//                                     </TableRow>);
//                             })
//
//                         ))}
//                     </TableBody>
//                 </Table>
//             </TableContainer>
//
//         </DialogContent>
//         <DialogActions>
//             <Button sx={{mr: 2, mb: 1}} onClick={() => closeDialog()} variant={'contained'}>Закрити </Button>
//         </DialogActions>
//     </Dialog>)
// }

export default RoutePointOrdersDialog;