import React, {useCallback} from 'react'
import {Box} from '@mui/material'
import OrdersTable from './OrdersContent/OrdersTable'
import {setOrderTabIndex} from '../../../features/currentDelivery/currentDeliverySlice'
import BasicTabs from '../../../hoc/BasicTabs/BasicTabs'
import {useParams} from 'react-router-dom'
import {useGetOrdersForDeliveryQuery, useUpdateOrderMutation} from '../../../api/delivery-orders/delivery-orders.api'
import {useAppDispatch, useAppSelector} from '../../../hooks/redux-hooks'
import {MapPointStatus} from '../../../model/MapPointStatus'
import Order from '../../../model/Order'
import ChooseAddressDialog from './OrdersContent/ChooseAddressDialog/ChooseAddressDialog'
import {DeliveryType} from '../../../model/DeliveryType'
import Loading from '../../../components/Loading/Loading'
import ExcludeOrderDialog from './OrdersContent/ExcludeOrderDialog/ExcludeOrderDialog'
import DeliveryTypeDialog from './OrdersContent/DeliveryTypeDialog/DeliveryTypeDialog'

const Orders = () => {
    const {deliveryId} = useParams()

    const {
        data: orders,
        isLoading,
        error
    } = useGetOrdersForDeliveryQuery(String(deliveryId))

    const isOrderAddressDialogOpen = useAppSelector(
        state => state.deliveryOrders.isOrderAddressDialogOpen
    )
    const isExcludeOrderDialogOpen = useAppSelector(
        state => state.deliveryOrders.isExcludeOrderDialogOpen
    )
    const isDeliveryTypeDialogOpen = useAppSelector(
        state => state.deliveryOrders.isDeliveryTypeDialogOpen
    )

    const [updateOrder] = useUpdateOrderMutation()
    const {orderTabIndex} = useAppSelector(state => state.currentDelivery)
    const dispatch = useAppDispatch()

    const setOrdersTabHandler = useCallback(
        (index: number) => dispatch(setOrderTabIndex(index)),
        [dispatch]
    )

    const updateOrderHandler = useCallback(
        (updatedOrder: Order) => updateOrder(updatedOrder),
        [dispatch]
    )

    if (!orders) {
        return <Loading/>
    }


    // TODO: Maybe use memo to not recalculate
    const ordersWithInvalidAddress = orders
        .filter(order => order.deliveryType === DeliveryType.REGULAR)
        .filter(order => order.mapPoint.status === MapPointStatus.UNKNOWN)
		.filter(order => !order.excluded)

    const excludedOrders = orders.filter(order => order.excluded)

    const grandeDolceOrders = orders
        .filter(order => order.deliveryType === DeliveryType.REGULAR)
        .filter(order => !order.excluded)
        .filter(order => !order.dropped);

    const otherOrders = orders
        .filter(order => order.deliveryType !== DeliveryType.REGULAR)
        .filter(order => !order.excluded)
        .filter(order => !order.dropped);

    const droppedOrders = orders.filter(order => order.dropped);

    const tabLabels = [
        {name: `Grande Dolce (${grandeDolceOrders.length})`, enabled: true},
        {name: `Інше (${otherOrders.length})`, enabled: true},
        {name: `Потребують уточнення (${ordersWithInvalidAddress.length})`, enabled: true},
        {name: `Вилучені (${excludedOrders.length})`, enabled: true}
    ]


    const tabs = [{
        orders: grandeDolceOrders,
        keyPrefix: 'grandeDolce'
    },
        {
            orders: otherOrders,
            keyPrefix: 'other'
        },
        {
            orders: ordersWithInvalidAddress,
            keyPrefix: 'invalid'
        },
        {
            orders: excludedOrders,
            keyPrefix: 'excluded',
            isExcluded: true
        }

    ];


    if (droppedOrders.length !== 0) {
        tabLabels.push({name: `Нерозраховані (${droppedOrders.length})`, enabled: true});
        tabs.push({
            orders: droppedOrders,
            keyPrefix: 'dropped'
        });
    }


    return (
        <Box sx={{padding: '0 24px'}}>
            <BasicTabs
                labels={tabLabels}
                setTabIndex={setOrdersTabHandler}
                tabIndex={orderTabIndex}
            >

                {
                    tabs.map(e => <OrdersTable
                        isExcluded={e.isExcluded}
                        orders={e.orders}
                        keyPrefix={e.keyPrefix}
                        key={"OrderTab" + e.keyPrefix}
                        updateOrder={updateOrderHandler}
                    />)
                }

            </BasicTabs>

            {isOrderAddressDialogOpen && <ChooseAddressDialog/>}

            {isExcludeOrderDialogOpen && <ExcludeOrderDialog/>}

            {isDeliveryTypeDialogOpen && <DeliveryTypeDialog/>}
        </Box>
    )
}

export default Orders
