import {FC} from "react";
import {RoutePoint, RoutePointStatus} from "../../model/RoutePoint";
import {View} from "react-native";
import tw from "twrnc";
import {Button, Icon} from "@rneui/themed";
import {openGps} from "../../util/util";

interface RoutePointControlButtonsProps {
    routePoint: RoutePoint,
    startRoutePoint?: (id: number) => void
    completeRoutePoint: (id: number) => void
    skipRoutePoint: (id: number) => void
}

export const RoutePointControlButtons: FC<RoutePointControlButtonsProps> = ({
                                                                                routePoint,
                                                                                skipRoutePoint,
                                                                                startRoutePoint,
                                                                                completeRoutePoint
                                                                            }) => {

    const notStarted = routePoint.status === RoutePointStatus.PENDING;
    return <View style={tw`pt-4`}>
        <Button containerStyle={tw`w-100%`}
                icon={<Icon type={"font-awesome-5"}
                            name={"location-arrow"}
                            size={10}
                            iconStyle={tw`ml-2`}
                            color={"rgb(25, 118, 210)"}/>}
                iconRight={true}
                title={'Прокласти маршрут'}
                type={"outline"}
                onPress={() => openGps(routePoint.mapPoint.latitude, routePoint.mapPoint.longitude)}
        />

        <View style={tw`flex-row justify-evenly gap-6 p-2 pt-4`}>
            {notStarted ? <Button containerStyle={tw`w-50%`}
                                  title={'В роботу'}
                                  type={"outline"}
                                  onPress={() => startRoutePoint(routePoint.id)}
            /> : null}
            {!notStarted ? <Button containerStyle={tw`w-50%`}
                                   title={'Завершити'}
                                   type={"outline"}
                                   buttonStyle={tw`border-green-600`}
                                   titleStyle={tw`text-green-600`}
                                   disabled={routePoint.status === RoutePointStatus.DONE}
                                   onPress={() => completeRoutePoint(routePoint.id)}
            /> : null}
            <Button containerStyle={tw`w-50%`}
                    title={'Пропустити'}
                    buttonStyle={tw`border-red-500`}
                    titleStyle={tw`text-red-500`}
                    type={"outline"}
                    onPress={() => skipRoutePoint(routePoint.id)}
            />
        </View>

    </View>
}