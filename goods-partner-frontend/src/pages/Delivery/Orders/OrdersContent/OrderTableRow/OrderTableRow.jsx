import {useAppDispatch} from '../../../../../hooks/redux-hooks'
import React, {useCallback} from 'react'
import {
    setAddressDialogOpen,
    setDeliveryTypeDialogOpen,
    setExcludeDialogOpen,
    setOrderForModification
} from '../../../../../features/delivery-orders/deliveryOrdersSlice'
import TableRow from '@mui/material/TableRow'
import TableCell from '@mui/material/TableCell'
import IconButton from '@mui/material/IconButton'
import KeyboardArrowUpIcon from '@mui/icons-material/KeyboardArrowUp'
import KeyboardArrowDownIcon from '@mui/icons-material/KeyboardArrowDown'
import Box from '@mui/material/Box'
import Collapse from '@mui/material/Collapse'
import OrderAdditionalInfo from '../OrderAdditionalInfo/OrderAdditionalInfo'
import OrderActionMenu from '../OrderActionMenu/OrderActionMenu'
import {toDeliveryTypeString} from '../../../../../util/util'
import {ProductsInfoTable} from "../../../../../components/ProductsInfoTable/ProductsInfoTable";

const OrderTableRow = ({
                           order,
                           keyPrefix,
                           updateOrder,
                           collapseAll,
                           expandAll,
                           reset,
                           isExcluded
                       }) => {
    const dispatch = useAppDispatch()
    const from = order.deliveryStart ?? '09:00'
    const to = order.deliveryFinish ?? '18:00'
    const [orderTableOpen, setOrderTableOpen] = React.useState(false)

    const isTableOpened = expandAll || (orderTableOpen && !collapseAll)

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

    const isInvalid = order.mapPoint.status === 'UNKNOWN' && order.deliveryType === 'REGULAR';
    return (
        <>
            <TableRow sx={styles}>
                <TableCell>
                    <IconButton
                        aria-label='expand row'
                        size='small'
                        onClick={() => {
                            setOrderTableOpen(!orderTableOpen)
                            reset()
                        }}
                    >
                        {isTableOpened ? (
                            <KeyboardArrowUpIcon/>
                        ) : (
                            <KeyboardArrowDownIcon/>
                        )}
                    </IconButton>
                </TableCell>
                <TableCell>{order.orderNumber}</TableCell>
                <TableCell>{order.clientName}</TableCell>
                <TableCell>
                    {isInvalid ? (
                        <Box sx={{ml: 1, backgroundColor: '#FFECB3'}}>
                            {order.address}
                        </Box>
                    ) : (
                        <Box sx={{ml: 1}}>{order.address}</Box>
                    )}
                </TableCell>
                <TableCell>{toDeliveryTypeString(order.deliveryType)}</TableCell>
                <TableCell>
                    {from} - {to}
                </TableCell>
                <TableCell>{order.managerFullName}</TableCell>
                {isExcluded && <TableCell>{order.excludeReason}</TableCell>}
                <TableCell align='center'>
                    <OrderActionMenu
                        changeAddress={handleChangeAddressDialogOpen}
                        exclude={handleExcludeOrderDialogOpen}
                        changeDeliveryType={handleChangeDeliveryTypeDialogOpen}
                    />
                </TableCell>
            </TableRow>
            <TableRow>
                <TableCell style={{paddingBottom: 0, paddingTop: 0}} colSpan={7}>
                    <Collapse in={isTableOpened} timeout='auto' unmountOnExit>
                        <Box sx={{margin: 2}}>
                            <ProductsInfoTable order={order} keyPrefix={keyPrefix}/>
                            <OrderAdditionalInfo order={order} updateOrder={updateOrder}/>
                        </Box>
                    </Collapse>
                </TableCell>
            </TableRow>
        </>
    )
}
export default OrderTableRow
