import * as actionTypes from '../actions/action-types';

let initialCars = {
    cars: []
};

const carReducer = (state = initialCars, action) => {
  switch (action.type) {
    case actionTypes.ALL_CARS:
      return {
        cars: action.payload
      }
    default:
      return state;
  }
}

export default carReducer;