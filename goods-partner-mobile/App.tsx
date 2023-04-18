import {Platform, SafeAreaView, StatusBar, StyleSheet, Text, View} from 'react-native';
import {NavigationContainer} from "@react-navigation/native";
import RootNavigator from "./navigator/RootNavigator";
import {Provider} from "react-redux";
import store from "./redux/store";
import {AuthProvider} from "./auth/AuthProvider";
import {AppNavigator} from "./navigator/AppNavigator";

// TODO: "react-native-loading-spinner-overlay" - this vs ActivityIndicator

export default function App() {
    return (
        <Provider store={store}>
            <AuthProvider>
                <SafeAreaView style={styles.container}>
                    <AppNavigator/>
                </SafeAreaView>
            </AuthProvider>
        </Provider>
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
