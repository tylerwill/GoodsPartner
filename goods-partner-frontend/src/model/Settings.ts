export interface Settings {
    clientProperties:         ClientProperties;
    clientRoutingProperties:  ClientRoutingProperties;
    clientBusinessProperties: ClientBusinessProperties;
    googleGeocodeProperties:  GoogleGeocodeProperties;
}

export interface ClientBusinessProperties {
    prePacking:  Postal;
    selfService: Postal;
    postal:      Postal;
}

export interface Postal {
    address:  null;
    keywords: string[];
}

export interface ClientProperties {
    clientServerURL:    string;
    server1CUriPrefix:  string;
    login:              string;
    password:           string;
    documentsUriPrefix: string;
}

export interface ClientRoutingProperties {
    unloadingTimeMinutes:             number;
    maxRouteTimeMinutes:              number;
    depotStartTime:                   string;
    depotFinishTime:                  string;
    defaultDeliveryStartTime:         string;
    defaultDeliveryFinishTime:        string;
    maxTimeProcessingSolutionSeconds: number;
}

export interface GoogleGeocodeProperties {
    apiKey: string;
}
