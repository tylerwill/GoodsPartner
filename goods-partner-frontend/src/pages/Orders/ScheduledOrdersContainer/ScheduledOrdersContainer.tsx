import {useGetScheduledQuery} from "../../../api/orders/orders.api";
import {OrdersBasicTable} from "../OrdersBasicTable/OrdersBasicTable";

export const ScheduledOrdersContainer = () => {
    const {data: orders} = useGetScheduledQuery();
    return <OrdersBasicTable orders={orders}/>
}