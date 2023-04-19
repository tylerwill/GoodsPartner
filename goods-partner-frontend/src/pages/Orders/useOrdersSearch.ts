import Order from "../../model/Order"
import {useState} from "react";

export const useOrdersSearch = (ordersList: Order[]): [Order[], (keyword: string) => void] => {
    const [orders, setOrders] = useState(ordersList);
    const filter = (keyword: string) => {
        const filterValue = keyword.toLowerCase();
        if (filterValue.length >= 2) {
            const filteredOrders = ordersList
                .filter(order => order.orderNumber.toLowerCase().includes(filterValue)
                    || order.address?.toLowerCase().includes(filterValue)
                    || order.clientName.toLowerCase().includes(filterValue));

            setOrders(filteredOrders)
        } else {
            setOrders(ordersList)
        }
    }
    return [orders, filter];
}