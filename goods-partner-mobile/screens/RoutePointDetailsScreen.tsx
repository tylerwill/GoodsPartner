import {RoutePoint} from "../model/RoutePoint";
import {FC} from "react";
import {Card} from "@rneui/themed";
import {Text, View} from "react-native";
import tw from "twrnc";
import {RoutePointControlButtons} from "../components/RoutePointDetails/RoutePointControlButtons";
import {TableRow} from "../components/TableRowProps";
import {StatusChip} from "../components/RoutePointDetails/StatusChip";
import {RootStackParamList} from "../navigator/RootNavigator";
import {RouteProp, useRoute} from "@react-navigation/native";
import {
    useCompleteRoutePointMutation,
    useGetRoutesForDeliveryQuery,
    useSkipRoutePointMutation
} from "../api/routes/routes.api";
import Spinner from "react-native-loading-spinner-overlay";

interface RoutePointDetailsScreenProps {
    routePoint: RoutePoint
    routePointNumber: number
}

type RoutePointDetailsScreenRouteProps = RouteProp<RootStackParamList, 'RoutePointDetails'>;

export const RoutePointDetailsScreen: FC<RoutePointDetailsScreenProps> = () => {
    const [completeRoutePoint] = useCompleteRoutePointMutation();
    const [skipRoutePoint] = useSkipRoutePointMutation();
    let {params: {routePoint, routePointNumber, routePointId}} = useRoute<RoutePointDetailsScreenRouteProps>();
    const {
        data: routes,
        isLoading: isRoutesLoading
    } = useGetRoutesForDeliveryQuery(String("4d5af503-ad81-4cab-9ea5-c467a69957e6"))

    if (isRoutesLoading || !routes) {
        return <Spinner visible={isRoutesLoading}/>
    }

    routePoint =  routes[1].routePoints.filter(rp => rp.id === routePointId)[0];

    const completedAt =
        routePoint.completedAt === null ? '-' : routePoint.completedAt;
    const deliveryTime = routePoint.deliveryStart + ' - ' + routePoint.deliveryEnd;

    const weight = routePoint.addressTotalWeight + ' кг';

    return <Card>
        <View style={tw`flex flex-row justify-between mb-2`}>
            <Text style={tw`mr-2 w-65% font-semibold `}>№{routePointNumber}, {routePoint.mapPoint.address}</Text>
            <View>
                <StatusChip routePointStatus={routePoint.status}/>
            </View>
        </View>

        <Card.Divider style={tw`mb-0`}/>
        <View>
            <TableRow firstName={"Час доставки"} firstValue={deliveryTime}
                      secondName={"Вага"} secondValue={weight}/>
            <TableRow firstName={"Клієнт"} firstValue={routePoint.clientName}
                      secondName={"Прибуття, прогноз"} secondValue={routePoint.expectedArrival}/>
            <TableRow firstName={"Завершення, прогноз"} firstValue={routePoint.expectedCompletion}
                      secondName={"Завершення, факт"} secondValue={completedAt}/>
        </View>
        <RoutePointControlButtons
            completeRoutePoint={completeRoutePoint}
            skipRoutePoint={skipRoutePoint}
            routePoint={routePoint}/>
    </Card>
}


