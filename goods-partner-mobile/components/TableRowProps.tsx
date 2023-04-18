import {FC} from "react";
import {Text, View} from "react-native";
import tw from "twrnc";

interface TableRowProps {
    firstName: string
    firstValue: any
    secondName: string
    secondValue: any
}


export const TableRow: FC<TableRowProps> = ({firstName, firstValue, secondName, secondValue}) => {
    return <View style={tw`pt-3 flex flex-row`}>
        <View style={tw`w-50%`}>
            <TablePair name={firstName} value={firstValue}/>
        </View>
        <View>
            <TablePair name={secondName} value={secondValue}/>
        </View>
    </View>
}

interface TablePairProps {
    name: string
    value: any
}

export const TablePair: FC<TablePairProps> = ({name, value}) => {
    return <View style={tw`pt-3`}>
        <View>
            <Text style={tw`font-semibold`}>{name}</Text>
        </View>
        <View>
            <Text>{value}</Text>
        </View>
    </View>
}
