import {FC} from "react";
import {RoutePoint, RoutePointStatus} from "../../model/RoutePoint";
import {Alert, View} from "react-native";
import tw from "twrnc";
import {Button, Icon} from "@rneui/themed";
import {openGps} from "../../util/util";

interface RoutePointControlButtonsProps {
    routePoint: RoutePoint,
    completeRoutePoint: (id: number) => void
    skipRoutePoint: (id: number) => void
}

export const RoutePointControlButtons: FC<RoutePointControlButtonsProps> = ({
                                                                                routePoint,
                                                                                skipRoutePoint,
                                                                                completeRoutePoint
                                                                            }) => {


    const disabled = routePoint.status !== RoutePointStatus.INPROGRESS;

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
            <Button containerStyle={tw`w-50%`}
                    title={'Завершити'}
                    type={"outline"}
                    buttonStyle={tw`border-green-600`}
                    titleStyle={tw`text-green-600`}
                    disabled={disabled}
                    onPress={() => createConfirmationAlert('Завершити',
                        'Завершити виконання точки?',
                        () => completeRoutePoint(routePoint.id))}
                // onPress={() => completeRoutePoint(routePoint.id)}
            />
            <Button containerStyle={tw`w-50%`}
                    title={'Пропустити'}
                    buttonStyle={tw`border-red-500`}
                    titleStyle={tw`text-red-500`}
                    type={"outline"}
                    disabled={disabled}
                    onPress={() => createConfirmationAlert('Пропустити',
                        'Пропустити точку?',
                        () => skipRoutePoint(routePoint.id))}
            />

        </View>

    </View>
}


const createConfirmationAlert = (title, message, action) =>
    Alert.alert(title, message, [
        {
            text: 'Так',
            onPress: action,
        },
        {
            text: 'Ні',
            style: 'cancel',
        }
    ]);