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
import OrderAdditionalInfo from '../../OrderAdditionalInfo/OrderAdditionalInfo'
import {ProductsInfoTable} from "../../../../components/ProductsInfoTable/ProductsInfoTable";

interface Props {
    order: Order
    keyPrefix: string
    collapseAll: boolean
    expandAll: boolean
    reset: () => void
}

const OrderRow = ({
                      order,
                      keyPrefix,
                      collapseAll,
                      expandAll,
                      reset
                  }: Props) => {
    const [orderTableOpen, setOrderTableOpen] = React.useState(false)
    const isTableOpened = expandAll || (orderTableOpen && !collapseAll)

    return (
        <>
            <TableRow sx={{'& > *': {borderBottom: 'unset'}}}>
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
                <TableCell component='th' scope='row'>
                    {order.orderNumber}
                </TableCell>
                <TableCell>{reformatDate(order.shippingDate)}</TableCell>
                <TableCell>{order.clientName}</TableCell>
                <TableCell>{order.address}</TableCell>
            </TableRow>
            <TableRow>
                <TableCell style={{paddingBottom: 0, paddingTop: 0}} colSpan={7}>
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
