import {Pressable, ScrollView, Text, View} from "react-native";
import {FC} from "react";
import {RoutePoint} from "../model/RoutePoint";
import {useGetRoutesForDeliveryQuery} from "../api/routes/routes.api";
import tw from "twrnc";
import {StatusChip} from "../components/RoutePointDetails/StatusChip";
import {Card} from "@rneui/themed";
import {useNavigation} from "@react-navigation/native";
import {NativeStackNavigationProp} from "@react-navigation/native-stack";
import {RootStackParamList} from "../navigator/RootNavigator";


interface RoutePointListScreenProps {
    routePoints: RoutePoint[]
}

type RoutePointDetailsScreenNavigationProp = NativeStackNavigationProp<RootStackParamList,
    'RoutePointDetails'>;

export const RoutePointListScreen: FC<RoutePointListScreenProps> = () => {
    const {
        data: routes,
        isLoading: isRoutesLoading
    } = useGetRoutesForDeliveryQuery(String("4d5af503-ad81-4cab-9ea5-c467a69957e6"))


    const navigation = useNavigation<RoutePointDetailsScreenNavigationProp>();

    if (!routes) {
        return <Text>Завантаження</Text>
    }

    return <ScrollView>
        {routes[1].routePoints.map((routePoint, index) => {
            return <Pressable key={'routePointKey' + routePoint.id}
                              onPress={() => navigation.navigate('RoutePointDetails', {
                                      routePoint: routePoint,
                                      routePointNumber: index + 1,
                                      routePointId: routePoint.id
                                  }
                              )}>
                <Card>
                    <View style={tw`flex flex-row justify-between`}>
                        <Text style={tw`mr-2 w-65% font-semibold `}>№{index + 1}, {routePoint.mapPoint.address}</Text>
                        <View>
                            <StatusChip routePointStatus={routePoint.status}/>
                        </View>
                    </View>
                </Card>
            </Pressable>
        })}
    </ScrollView>
}

