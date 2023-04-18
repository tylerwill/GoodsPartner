import {Image, Text, View} from "react-native";
import tw from "twrnc";


const Logo = () => {
    return (
        <View style={tw`flex-row items-center gap-2`}>
            <Image
                source={require('../assets/favicon-32x32.png')}
            />
            <Text style={{
                textTransform: 'uppercase',
                color: 'rgba(0, 0, 0, 0.87)',
                fontWeight: 'bold',
                fontSize: 20

            }}>
                Goods partner
            </Text>
        </View>
    )
}

export default Logo
