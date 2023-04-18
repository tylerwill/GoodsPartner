import {NavigationContainer} from "@react-navigation/native";
import {useAuth} from "../auth/AuthProvider";
import RootNavigator from "./RootNavigator";
import AuthNavigator from "./AuthNavigator";

export const AppNavigator = () => {
    const {authState: {authenticated}} = useAuth();
    return <NavigationContainer>
        {authenticated ? <RootNavigator/> : <AuthNavigator/>}
    </NavigationContainer>
};
