import React, {useState} from "react";
import {Button, Card, CardContent, Stack, Typography} from "@mui/material";
import Box from "@mui/material/Box";
import Grid from "@mui/material/Grid";
import RouteDetails from "./RouteDetails/RouteDetails";
import RouteBody from "./RouteBody/RouteBody";

const RoutesList = ({date, routes, routeAddresses, changeRoutePointStatus, changeRouteStatus, updateRoute}) => {

  const initialRoute = routes === null ? '' : routes[0].id;
  const [activeRoute, setActiveRoute] = useState(initialRoute);

  const turnOnRouteTable = (routeId) => {
    setActiveRoute(routeId);
  }

  return (<Card sx={{minWidth: 275}}>
      <CardContent>
        <Stack spacing={2}>
          {
            date
              ? <>
                <Typography variant="h5" gutterBottom component="div">
                  Список маршрутів на {date}
                </Typography>
                <Box sx={{flexGrow: 1}}>
                  <Grid container spacing={2}>
                    <Grid item xs={3}>
                      <Stack spacing={0.5}>
                        {
                          routes.map((route, index) =>
                            <Button size="small"
                                    key = {"routeButton" + route.id}
                                    variant={route.id === activeRoute ? "contained" : "outlined"}
                                    onClick={() => turnOnRouteTable(route.routeId)}
                            >
                              МАРШРУТ №{index + 1}
                            </Button>)
                        }
                      </Stack>
                    </Grid>
                    <Grid item xs={9}>
                      <Stack spacing={2}>
                          <Stack>
                            {
                              routes.map(route => route.id === activeRoute
                                ? <RouteDetails
                                      route={route}
                                      date={date}
                                      routeAddresses={routeAddresses}
                                      changeRouteStatus = {changeRouteStatus}
                                      updateRoute = {updateRoute}
                                  />
                                : ''
                              )
                            }
                          </Stack>
                      </Stack>
                    </Grid>
                  </Grid>
                  <Box>
                    {
                      routes.map(route => route.id === activeRoute
                          ? <RouteBody
                              changeRoutePointStatus = {changeRoutePointStatus}
                              storeStatus={route.status}
                              routePoints={route.routePoints}
                              storeName={route.storeName}
                              storeAddress={route.storeAddress}
                          />
                          : ''
                      )
                    }
                  </Box>
                </Box>
              </>
              : <Typography variant="h5" gutterBottom component="div">
                Оберіть дату та натисніть "РОЗРАХУВАТИ"
              </Typography>
          }
        </Stack>
      </CardContent>
    </Card>
  );
}

export default RoutesList;