import React from 'react'
import TableRow from '@mui/material/TableRow'
import TableCell from '@mui/material/TableCell'
import IconButton from '@mui/material/IconButton'
import KeyboardArrowUpIcon from '@mui/icons-material/KeyboardArrowUp'
import KeyboardArrowDownIcon from '@mui/icons-material/KeyboardArrowDown'
import Box from '@mui/material/Box'
import Collapse from '@mui/material/Collapse'
import Order from '../../../../model/Order'
import {reformatDate} from '../../../../util/util'
import {Checkbox} from '@mui/material'
import {useAppDispatch, useAppSelector} from '../../../../hooks/redux-hooks'
import {deselectOrder, selectOrder} from '../../../../features/orders/ordersSlice'
import OrderAdditionalInfo from '../../OrderAdditionalInfo/OrderAdditionalInfo'
import {ProductsInfoTable} from "../../../../components/ProductsInfoTable/ProductsInfoTable";

interface Props {
    order: Order
    keyPrefix: string
    collapseAll: boolean
    expandAll: boolean
    reset: () => void
    hasExcluded:boolean
}

const OrderRow = ({
                      order,
                      keyPrefix,
                      collapseAll,
                      expandAll,
                      reset,
    hasExcluded
                  }: Props) => {
    const [orderTableOpen, setOrderTableOpen] = React.useState(false)
    const isTableOpened = expandAll || (orderTableOpen && !collapseAll)

    const {selectedOrderIds} = useAppSelector(state => state.orders)

    const dispatch = useAppDispatch()
    const isSelected = selectedOrderIds.includes(order.id)
    const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        if (event.target.checked) {
            dispatch(selectOrder(order.id))
        } else {
            dispatch(deselectOrder(order.id))
        }
        event.preventDefault()
    }

    return (
        <>
            <TableRow sx={{'& > *': {borderBottom: 'unset'}}}>
                <TableCell padding={'checkbox'}>
                    <IconButton
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
                <TableCell padding='checkbox'>
                    <Checkbox
                        color='primary'
                        checked={isSelected}
                        onChange={handleChange}
                    />
                </TableCell>
                <TableCell component='th' scope='row'>
                    {order.orderNumber}
                </TableCell>
                <TableCell>{reformatDate(order.shippingDate)}</TableCell>
                <TableCell>{order.clientName}</TableCell>
                <TableCell>{order.address}</TableCell>
                {hasExcluded && <TableCell>{order.excludeReason}</TableCell>}
            </TableRow>
            <TableRow>
                <TableCell style={{paddingBottom: 0, paddingTop: 0}} colSpan={hasExcluded ? 7 : 6}>
                    <Collapse in={isTableOpened} timeout='auto' unmountOnExit>
                        <Box sx={{margin: 2}}>
                            <ProductsInfoTable order={order} keyPrefix={keyPrefix}/>

                            <OrderAdditionalInfo order={order}/>
                        </Box>
                    </Collapse>
                </TableCell>
            </TableRow>
        </>
    )
}

export default OrderRow
