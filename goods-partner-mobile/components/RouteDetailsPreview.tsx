import {ScrollView, Text, View} from "react-native";
import React, {FC} from "react";
import {Route, RouteStatus} from "../model/Route";
import {Button, Card, Chip} from '@rneui/themed';
import tw from 'twrnc';
import {useCompleteRouteMutation, useStartRouteMutation} from "../api/routes/routes.api";
import {useNavigation} from "@react-navigation/native";
import {RootStackParamList} from "../navigator/RootNavigator";
import {NativeStackNavigationProp} from "@react-navigation/native-stack";
import {toHoursAndMinutes} from "../util/util";
import {TablePair, TableRow} from "./TableRowProps";


interface RouteDetailsPreviewProps {
    route: Route
}

type RoutePointListScreenNavigationProp = NativeStackNavigationProp<RootStackParamList,
    'RoutePoints'>;

export const RouteDetailsPreview: FC<RouteDetailsPreviewProps> = ({route}) => {

    const navigation = useNavigation<RoutePointListScreenNavigationProp>();
    const [startRoute] = useStartRouteMutation();
    const [completeRoute] = useCompleteRouteMutation();

    const routeStatus = route.status;
    return <ScrollView style={tw` flex-1`}>
        <Card containerStyle={tw`pt-2`}>
            <View style={tw`flex flex-row justify-between items-center mb-2`}>
                <Text style={tw`font-semibold `}>Маршрут №{route.id} від 08.04.2023</Text>
                <View>
                    <StatusChip routeStatus={routeStatus}/>
                </View>
            </View>
            <Card.Divider/>
            <View>
                {route.status === RouteStatus.APPROVED && <Button
                    onPress={() => startRoute(route.id)}
                    title={'Розпочати'}/>}

                {route.status === RouteStatus.INPROGRESS && <Button
                    onPress={() => completeRoute(route.id)}
                    title={'Завершити'}
                />}
            </View>
            <View>
                <RoutePreviewTable route={route}/>
            </View>

            <Button containerStyle={tw`mt-4`}
                    onPress={() => navigation.navigate('RoutePoints')}>Пункти призначення</Button>

        </Card>
    </ScrollView>
}

interface StatusChipProps {
    routeStatus: RouteStatus
}

const StatusChip: FC<StatusChipProps> = ({routeStatus}) => {
    let title;
    let borderColor = 'border-blue-600';
    let textColor = 'text-blue-500';
    if (routeStatus == RouteStatus.APPROVED) {
        title = 'Затверджений';
    } else if (routeStatus == RouteStatus.INPROGRESS) {
        title = 'В роботі';
    } else if (routeStatus == RouteStatus.COMPLETED) {
        title = 'Завершений';
        borderColor = 'border-green-600';
        textColor = 'text-green-500'
    }

    return <Chip buttonStyle={tw`${borderColor}`}
                 titleStyle={tw`${textColor}`}
                 title={title}
                 type={"outline"}
    />;
}

const RoutePreviewTable: FC<RouteDetailsPreviewProps> = ({route}) => {
    const car = route.car;
    const carName = car.name + ', ' + car.licencePlate;
    const startTime = route.startTime ? route.startTime : '8:00'
    const finishTime = route.finishTime ? route.finishTime : '-'
    const spentTime = route.spentTime ? toHoursAndMinutes(route.spentTime) + ' хв' : '-'
    return <View>
        <TablePair name={'Машина'} value={carName}/>
        <TableRow firstName={'Вантажність машини'} firstValue={car.weightCapacity + ' кг'}
                  secondName={'Склад'} secondValue={route.store.name}/>
        <TablePair name={'Адреса складу'} value={route.store.address}/>
        <TableRow firstName={'Кількість адрес'} firstValue={route.totalPoints}
                  secondName={'Кількість замовлень'} secondValue={route.totalOrders}/>
        <TableRow firstName={'Загальна вага'} firstValue={route.totalWeight + ' кг'}
                  secondName={'Відстань'} secondValue={route.distance}/>

        <TableRow firstName={'Початок виконання'} firstValue={startTime} secondName={'Кінець виконання'}
                  secondValue={finishTime}/>
        <TableRow firstName={'Розрахунковий час'} firstValue={route.estimatedTime + ' хв'}
                  secondName={'Фактичний час'} secondValue={spentTime }/>
    </View>
}
