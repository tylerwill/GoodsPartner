import * as actionTypes from '../actions/action-types';

let initialCalculate = {
  date: "",
  orders: [
    {
      orderId: null,
      orderNumber: null,
      createdDate: "",
      orderData: {
        clientName: "",
        address: "",
        managerFullName: "",
        products: [
          {
            productName: "",
            amount: null,
            storeName: ""
          }
        ]
      }
    }
  ],
  clients: [
    {
      clientId: null,
      clientName: "",
      addresses: [
        {
          address: "",
          addressTotalWeight: null,
          orders: [
            {
              orderId: null,
              orderNumber: null
            }
          ]
        }
      ]
    }
  ],
  stores: [
    {
      storeId: null,
      storeName: "",
      orders: [
        {
          orderId: null,
          orderNumber: null,
          kg: null
        }
      ]
    }
  ]
}

const calculateReducer = (state = initialCalculate, action) => {
  switch (action.type) {
    case actionTypes.CALCULATE_BY_DATE:
      return {
        date: action.calculatedDataByDate.date,
        orders: action.calculatedDataByDate.orders,
        clients: action.calculatedDataByDate.clients,
        stores: action.calculatedDataByDate.stores
      }
    default:
      return state;
  }
}

export default calculateReducer;