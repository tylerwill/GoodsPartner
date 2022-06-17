import {calculateApi} from "../../api/api";
import {calculateAllDataByDate} from "../actions/calculate-action";

export const getCalculatedDataByDate = (ordersDate) => async dispatch => {
  let calculatedData = await calculateApi.calculateByDateRequest(ordersDate);
  // dispatch(calculateAllDataByDate(calculatedData));
  dispatch(calculateAllDataByDate(calculatedData.data));
}