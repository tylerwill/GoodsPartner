import {createBottomTabNavigator} from "@react-navigation/bottom-tabs";
import {RouteScreen} from "../screens/RouteScreen";
import {TasksScreen} from "../screens/TasksScreen";
import {useNavigation} from "@react-navigation/native";
import {useLayoutEffect} from "react";
import {Icon} from "@rneui/themed";

const Tab = createBottomTabNavigator();

const TabNavigator = () => {
    const navigation = useNavigation();

    useLayoutEffect(() => {
        navigation.setOptions({
            headerShown: false
        });
    }, []);

    return (
        <Tab.Navigator screenOptions={({route}) => ({
            tabBarIcon: ({focused, color, size}) => {
                if (route.name === 'Маршрут') {
                    return <Icon type={"font-awesome-5"}
                                 name={"route"}
                                 color={focused ? "rgb(25, 118, 210)" : color}/>
                } else if (route.name === 'Задачі') {
                    return <Icon type={"font-awesome-5"}
                                 name={"tasks"}
                                 color={focused ? "rgb(25, 118, 210)" : color}/>
                }
            }
        })}>
            <Tab.Screen name={"Маршрут"} component={RouteScreen}/>
            <Tab.Screen name={"Задачі"} component={TasksScreen}/>
        </Tab.Navigator>
    );
};

export default TabNavigator;