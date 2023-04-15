import {Platform, SafeAreaView, StatusBar, StyleSheet, Text, View} from 'react-native';
import {NavigationContainer} from "@react-navigation/native";
import RootNavigator from "./navigator/RootNavigator";
import {Provider} from "react-redux";
import store from "./redux/store";

export default function App() {
    return (
        <SafeAreaView style={styles.container}>
            <Provider store={store}>
                <NavigationContainer>
                    <RootNavigator/>
                </NavigationContainer>
            </Provider>
        </SafeAreaView>
    );
}

const styles = StyleSheet.create(
    {
        container: {
            flex: 1,
            backgroundColor: '#fff',
            paddingTop: Platform.OS == "android" ? StatusBar.currentHeight : 0,
            paddingBottom: 5
        },
    }
);
