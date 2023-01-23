import {useGetCompletedQuery} from "../../../api/orders/orders.api";
import {OrdersBasicTable} from "../OrdersBasicTable/OrdersBasicTable";

export const DeliveredOrdersContainer = () => {
    const {data: orders} = useGetCompletedQuery();
    return <OrdersBasicTable orders={orders}/>
}