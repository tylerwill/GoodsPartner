import {Linking} from "react-native";

export const currentHost = () => {
    return "http://192.168.0.137:3001/"
}

export const apiUrl = `${currentHost()}api/v1`

export const openGps = (lat, lng) => {
    const latLng = `${lat},${lng}`;
    Linking.openURL(`google.navigation:q=${latLng}`);
}

export const toHoursAndMinutes = (totalMinutes: number) => {
    const minutes = totalMinutes % 60
    const hours = Math.floor(totalMinutes / 60)

    return `${hours}год. ${minutes}хв.`
}
