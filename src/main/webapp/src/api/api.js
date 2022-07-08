import axios from "axios";

const defaultOptions = {
  baseURL: process.env.HEROKU_BASE_URL !== undefined
      ? process.env.HEROKU_BASE_URL
      : process.env.REACT_APP_LOCALHOST_BASE_URL
};

let axiosWithSetting = axios.create(defaultOptions);

let mockedOrders = {
  data: {
    date: "2022-05-24",
    orders: [
      {
        orderId: 105,
        orderNumber: 32455,
        createdDate: "2022-02-14",
        orderData: {
          clientName: "Novus",
          address: "м. Київ, вул. Межигірська, 5Б",
          managerFullName: "Petya Bamper",
          products: [
            {
              productName: "Margarine",
              amount: 105,
              storeName: "Central Store"
            },
            {
              productName: "Butter",
              amount: 60,
              storeName: "Addition Store"
            }
          ]
        }
      },
      {
        orderId: 155,
        orderNumber: 36755,
        createdDate: "2022-04-10",
        orderData: {
          clientName: "FOP Pupkin",
          address: "м. Київ, вул. Хрещатик, 12",
          managerFullName: "Vasya Manager",
          products: [
            {
              productName: "Sugar",
              amount: 570,
              storeName: "Central Store"
            },
            {
              productName: "Jam",
              amount: 45,
              storeName: "Addition Store"
            }
          ]
        }
      }
    ]
  }
}
let mockedRoutes = {
  data: {
    date: "2022-05-24",
    routes: [
      {
        routeId: 12,
        status: "done",
        totalWeight: 444,
        totalPoints: 10,
        totalOrders: 15,
        distance: 230,
        estimatedTime: "7:35:40",
        startTime: "9:24:15",
        finishTime: "16:24:15",
        spentTime: "7:00:00",
        routeLink: "https://osm.ua/build-route-12",
        storeName: "main store",
        storeAddress: "м. Фастів, вул. Закопай орка, 1",
        clients: [
          {
            clientId: 234,
            clientName: "Novus",
            addresses: [
              {
                address: "м. Київ, вул. Межигірська, 5Б",
                addressTotalWeight: 244,
                orders: [
                  {
                    orderId: 103,
                    orderNumber: 12335,
                    orderTotalWeight: 44
                  },
                  {
                    orderId: 205,
                    orderNumber: 15654,
                    orderTotalWeight: 200
                  }
                ]
              },
              {
                address: "м. Київ, вул. Хрещатик, 10",
                addressTotalWeight: 589,
                orders: [
                  {
                    orderId: 255,
                    orderNumber: 153325,
                    orderTotalWeight: 44
                  },
                  {
                    orderId: 289,
                    orderNumber: 2053455,
                    orderTotalWeight: 44
                  },
                  {
                    orderId: 278,
                    orderNumber: 17688,
                    orderTotalWeight: 424
                  }
                ]
              }
            ]
          },
          {
            clientId: 432,
            clientName: "FOP Pupkin",
            addresses: [
              {
                address: "м. Київ, вул. Межигірська, 5Б",
                addressTotalWeight: 244,
                orders: [
                  {
                    orderId: 103,
                    orderNumber: 12335,
                    orderTotalWeight: 554
                  },
                  {
                    orderId: 205,
                    orderNumber: 15654,
                    orderTotalWeight: 5768
                  }
                ]
              }
            ]
          }
        ]
      },
      {
        routeId: 13,
        status: "in progress",
        totalWeight: 568,
        totalPoints: 15,
        totalOrders: 20,
        distance: 157,
        estimatedTime: "7:35:40",
        startTime: "9:24:15",
        finishTime: "",
        spentTime: "",
        routeLink: "https://osm.ua/build-route-12",
        storeName: "main store",
        storeAddress: "м. Фастів, вул. Закопай орка, 1",
        clients: [
          {
            clientId: 234,
            clientName: "Novus",
            addresses: [
              {
                address: "м. Київ, вул. Межигірська, 5Б",
                addressTotalWeight: 244,
                orders: [
                  {
                    orderId: 103,
                    orderNumber: 12335,
                    orderTotalWeight: 44
                  },
                  {
                    orderId: 205,
                    orderNumber: 15654,
                    orderTotalWeight: 200
                  }
                ]
              },
              {
                address: "м. Київ, вул. Хрещатик, 10",
                addressTotalWeight: 589,
                orders: [
                  {
                    orderId: 255,
                    orderNumber: 153325,
                    orderTotalWeight: 44
                  },
                  {
                    orderId: 289,
                    orderNumber: 2053455,
                    orderTotalWeight: 44
                  },
                  {
                    orderId: 278,
                    orderNumber: 17688,
                    orderTotalWeight: 424
                  }
                ]
              }
            ]
          },
          {
            clientId: 432,
            clientName: "FOP Pupkin",
            addresses: [
              {
                address: "м. Київ, вул. Межигірська, 5Б",
                addressTotalWeight: 244,
                orders: [
                  {
                    orderId: 103,
                    orderNumber: 12335,
                    orderTotalWeight: 554
                  },
                  {
                    orderId: 205,
                    orderNumber: 15654,
                    orderTotalWeight: 5768
                  }
                ]
              }
            ]
          }
        ]
      }
    ]
  }
}
let mockedStores = {
  data: {
    date: "2022-05-24",
    stores: [
      {
        storeId: 1,
        storeName: "Main Store",
        orders: [
          {
            orderId: 103,
            orderNumber: 12335,
            totalOrderWeight: 107
          },
          {
            orderId: 205,
            orderNumber: 15654,
            totalOrderWeight: 205
          }
        ]
      },
      {
        storeId: 2,
        storeName: "Addition Store",
        orders: [
          {
            orderId: 173,
            orderNumber: 574345,
            totalOrderWeight: 405
          },
          {
            orderId: 245,
            orderNumber: 365675,
            totalOrderWeight: 120
          }
        ]
      }
    ]
  }
}

export const orderApi = {
  getOrdersByDateRequest(ordersDate) {
    // return axiosWithSetting.get(`calculate/orders?date=` + ordersDate);
    mockedOrders.data.date = ordersDate;
    return mockedOrders;
  }
}

export const routeApi = {
  getRoutesByDateRequest(routesDate) {
    // return axiosWithSetting.get(`calculate/routes?date=` + routesDate);
    mockedRoutes.data.date = routesDate;
    return mockedRoutes;
  }
}

export const storeApi = {
  getStoresByDateRequest(storesDate) {
    // return axiosWithSetting.get(`calculate/stores?date=` + storesDate);
    mockedStores.data.date = storesDate;
    return mockedStores;
  }
}