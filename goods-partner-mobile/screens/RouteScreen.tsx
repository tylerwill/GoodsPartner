import {Text, View} from "react-native";
import {useGetRoutesForDeliveryQuery} from "../api/routes/routes.api";
import {RouteDetailsPreview} from "../components/RouteDetailsPreview";
import tw from "twrnc";

export const RouteScreen = () => {
    const {
        data: routes,
        isLoading: isRoutesLoading
    } = useGetRoutesForDeliveryQuery(String("4d5af503-ad81-4cab-9ea5-c467a69957e6"))

    console.log("isLoading", isRoutesLoading);

    if (!routes) {
        return <View><Text>Завантаження</Text></View>
    }

    return <View style={tw`flex-1`}>
        <RouteDetailsPreview route={routes[1]}/>
    </View>
}
