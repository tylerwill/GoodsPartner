import {createNativeStackNavigator} from "@react-navigation/native-stack";
import TabNavigator from "./TabNavigator";
import {RoutePointListScreen} from "../screens/RoutePointListScreen";
import {RoutePoint} from "../model/RoutePoint";
import {RoutePointDetailsScreen} from "../screens/RoutePointDetailsScreen";

export type RootStackParamList = {
    Main: undefined,
    RoutePoints: undefined,
    RoutePointDetails: {
        routePoint: RoutePoint
        routePointNumber: number
        routePointId: number
    }
}

const RootStack = createNativeStackNavigator<RootStackParamList>();

const RootNavigator = () => {
    return <RootStack.Navigator initialRouteName={'Main'}>
        <RootStack.Group>
            <RootStack.Screen name="Main" component={TabNavigator}/>
        </RootStack.Group>

        <RootStack.Group>
            <RootStack.Screen name="RoutePoints" component={RoutePointListScreen}/>
        </RootStack.Group>

        <RootStack.Group>
            <RootStack.Screen name="RoutePointDetails" component={RoutePointDetailsScreen}/>
        </RootStack.Group>
    </RootStack.Navigator>
};


export default RootNavigator;