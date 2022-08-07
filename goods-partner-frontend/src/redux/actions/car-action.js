import * as actionTypes from './action-types';

export const setCars = (cars) => {
  return {
    type: actionTypes.ALL_CARS,
    payload : cars
  }
}