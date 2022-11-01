import * as React from "react";
import {useEffect} from "react";
import {currentHost} from "../../util/util";
import {useSnackbar} from 'notistack'
import {useDispatch, useSelector} from "react-redux";
import {fetchDelivery} from "../../features/currentDelivery/currentDeliverySlice";
import {setNotification} from "../../features/notifications/notificationsSlice";

function Notifications({children}) {
    const {enqueueSnackbar} = useSnackbar()
    const dispatch = useDispatch();
    const {currentNotification} = useSelector(state => state.notifications);
    const {delivery} = useSelector(state => state.currentDelivery);

    useEffect(() => {
        const sse = new EventSource(`${currentHost()}api/v1/live-event`, {withCredentials: true});

        function getRealtimeData(notification) {
            console.log('data', notification);
            if (notification.type === "HEARTBEAT") {
                return;
            }
            dispatch(setNotification(notification))

            // process the data here,
            // then pass it to state to be rendered
        }

        sse.onmessage = e => getRealtimeData(JSON.parse(e.data));
        sse.onerror = (error) => {
            // error log here
            console.log("error in live events", error);
            sse.close();
        }
        return () => {
            sse.close();
        };
    }, []);

    useEffect(() => {
        if (currentNotification == null) {
            return;
        }
        console.log("notification", currentNotification);
        console.log("delivery", delivery);
        enqueueSnackbar(currentNotification.message,
            {variant: currentNotification.type === 'INFO' ? 'default' : currentNotification.type.toLowerCase()})

        if (currentNotification.action?.type === 'DELIVERY_UPDATED') {
            if (delivery && delivery.id === currentNotification.action.deliveryId) {
                dispatch(fetchDelivery(delivery.id));
            }
        }
    }, [currentNotification]);

    return (
        <>
            {children}
        </>
    );
}

export default Notifications;
