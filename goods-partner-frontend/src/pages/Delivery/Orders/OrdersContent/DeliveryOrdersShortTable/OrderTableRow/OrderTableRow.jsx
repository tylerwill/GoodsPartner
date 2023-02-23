import {useAppDispatch} from '../../../../../../hooks/redux-hooks'
import React, {useCallback} from 'react'
import {
    setAddressDialogOpen,
    setDeliveryTypeDialogOpen,
    setExcludeDialogOpen,
    setOrderForModification
} from '../../../../../../features/delivery-orders/deliveryOrdersSlice'
import TableRow from '@mui/material/TableRow'
import TableCell from '@mui/material/TableCell'
import OrderActionMenu from '../../OrderActionMenu/OrderActionMenu'

const OrderTableRow = ({order}) => {

    const dispatch = useAppDispatch()

    const handleChangeAddressDialogOpen = useCallback(() => {
        dispatch(setOrderForModification(order))
        dispatch(setAddressDialogOpen(true))
    }, [dispatch, setAddressDialogOpen, order])

    const handleExcludeOrderDialogOpen = useCallback(() => {
        dispatch(setOrderForModification(order))
        dispatch(setExcludeDialogOpen(true))
    }, [dispatch, setExcludeDialogOpen, order])

    const handleChangeDeliveryTypeDialogOpen = useCallback(() => {
        dispatch(setOrderForModification(order))
        dispatch(setDeliveryTypeDialogOpen(true))
    }, [dispatch, setDeliveryTypeDialogOpen, order])

    let styles = {
        '& > *': {borderBottom: 'unset'}
    }

    if (order.excluded) {
        styles = {
            ...styles,
            background: 'rgba(0, 0, 0, 0.04)'
        }
    }

    return (
        <>
            <TableRow sx={styles}>
                <TableCell>{order.orderNumber}</TableCell>
                <TableCell>{order.clientName}</TableCell>
                <TableCell>{order.comment}</TableCell>
                <TableCell align='center'>
                    <OrderActionMenu
                        changeAddress={handleChangeAddressDialogOpen}
                        exclude={handleExcludeOrderDialogOpen}
                        changeDeliveryType={handleChangeDeliveryTypeDialogOpen}
                    />
                </TableCell>
            </TableRow>
        </>
    )
}
export default OrderTableRow
