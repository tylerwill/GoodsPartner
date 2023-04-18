import {createNativeStackNavigator} from "@react-navigation/native-stack";
import TabNavigator from "./TabNavigator";
import {RoutePointListScreen} from "../screens/RoutePointListScreen";
import {RoutePoint} from "../model/RoutePoint";
import {RoutePointDetailsScreen} from "../screens/RoutePointDetailsScreen";
import {LoginScreen} from "../screens/LoginScreen";

export type AuthStackStackParamList = {
    Login: undefined
}

const AuthStack = createNativeStackNavigator<AuthStackStackParamList>();

const AuthNavigator = () => {
    return <AuthStack.Navigator initialRouteName={'Login'}>
        <AuthStack.Screen name="Login" component={LoginScreen}/>
    </AuthStack.Navigator>
};


export default AuthNavigator;