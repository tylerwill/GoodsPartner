import React, {useState} from "react";
import {Button, Card, CardContent, Stack, Typography} from "@mui/material";
import Box from "@mui/material/Box";
import Grid from "@mui/material/Grid";
import RouteTable from "./RouteTable/RouteTable";

const RoutesList = ({date, routes, routeAddresses}) => {

  const initialRoute = routes === null ? '' : routes[0].routeId;
  const [activeRoute, setActiveRoute] = useState(initialRoute);

  const turnOnRouteTable = (routeId) => {
    setActiveRoute(routeId);
  }

  console.log("api key", process.env.REACT_APP_GOOGLE_MAPS_API_VAR);
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
                                    key = {"routeButton" + route.routeId}
                                    variant={route.routeId === activeRoute ? "contained" : "outlined"}
                                    onClick={() => turnOnRouteTable(route.routeId)}
                            >
                              МАРШРУТ №{index + 1}
                            </Button>)
                        }
                      </Stack>
                    </Grid>
                    <Grid item xs={9}>
                      <Stack spacing={2}>
                        <Typography variant="h6" component="h1">
                          <Stack>
                            {
                              routes.map(route => route.routeId === activeRoute
                                ? <RouteTable
                                      key = {"routeTable" + route.routeId}
                                  date={date}
                                  route={route}
                                  routeAddresses={routeAddresses}
                                />
                                : ''
                              )
                            }
                          </Stack>
                        </Typography>
                      </Stack>
                    </Grid>
                  </Grid>
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