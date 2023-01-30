import {useGetScheduledQuery} from "../../../api/orders/orders.api";
import OrdersTableWithReschedule from "../OrdersTableWithReschedule/OrdersTableWithReschedule";

export const ScheduledOrdersContainer = () => {
    const {data: orders} = useGetScheduledQuery();
    return <OrdersTableWithReschedule orders={orders} hasExcluded={false}/>
}