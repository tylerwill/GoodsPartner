import {Text, View} from "react-native";
import Logo from "../components/Logo";
import tw from "twrnc";
import {Button, Input} from "@rneui/themed";
import {useState} from "react";
import {useAuth} from "../auth/AuthProvider";
import {useAppSelector} from "../hooks/redux-hooks";
import {selectAuthError} from "../features/auth/authSlice";

export const LoginScreen = () => {
    const [loginString, setLoginString] = useState("");
    const [password, setPassword] = useState("");

    const error = useAppSelector(selectAuthError);

    const {onLogin} = useAuth();

    return <View style={tw`flex-1 items-center mt-30 p-5`}>
        <View style={tw`mb-7`}>
            <Logo/>
        </View>
        <Input onChangeText={text => setLoginString(text)} placeholder={'Логін'}></Input>
        <Input secureTextEntry onChangeText={text => setPassword(text)} placeholder={'Пароль'}></Input>


        <View style={tw`text-center`}>
            {error ? <Text style={tw`w-100% text-red-500`}>Невірний логін або пароль</Text> : null}
        </View>
        <Button onPress={() => onLogin(loginString, password)}
                color={'primary'} containerStyle={tw`w-100% p-5`}>Увійти</Button>
    </View>

}
