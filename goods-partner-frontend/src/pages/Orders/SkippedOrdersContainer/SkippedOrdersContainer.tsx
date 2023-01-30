import {useGetSkippedQuery} from "../../../api/orders/orders.api";
import OrdersTableWithReschedule from "../OrdersTableWithReschedule/OrdersTableWithReschedule";

export const SkippedOrdersContainer = () => {
    const {data: orders} = useGetSkippedQuery();
    return <OrdersTableWithReschedule orders={orders} hasExcluded={true}/>
}