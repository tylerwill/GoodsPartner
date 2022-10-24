import axios from "axios";
import {currentHost} from "../util/util";

const host = currentHost();
const defaultOptions = {
    baseURL: `${host}api/v1`
};

export const axiosWithSetting = axios.create(defaultOptions);
