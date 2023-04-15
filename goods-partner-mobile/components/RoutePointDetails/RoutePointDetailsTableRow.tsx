import {FC} from "react";
import {Text, View} from "react-native";
import tw from "twrnc";

interface RoutePointTableRowProps {
    firstName: string
    firstValue: any
    secondName: string
    secondValue: any
}


export const RoutePointTableRow: FC<RoutePointTableRowProps> = ({firstName, firstValue, secondName, secondValue}) => {
    return <View style={tw`pt-3 flex flex-row`}>
        <View style={tw`w-50%`}>
            <RoutePointTablePair name={firstName} value={firstValue}/>
        </View>
        <View>
            <RoutePointTablePair name={secondName} value={secondValue}/>
        </View>
    </View>
}

interface RoutePointTablePairProps {
    name: string
    value: string
}

const RoutePointTablePair: FC<RoutePointTablePairProps> = ({name, value}) => {
    return <View style={tw`pt-3`}>
        <View>
            <Text style={tw`font-semibold`}>{name}</Text>
        </View>
        <View>
            <Text>{value}</Text>
        </View>
    </View>
}