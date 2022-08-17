export const currentHost = () => {
    const host = process.env.NODE_ENV === "development"
        ? process.env.REACT_APP_LOCALHOST_BASE_URL
        : process.env.REACT_APP_HEROKU_BASE_URL

    return host;
}

export const generateReportLink = (type, date) => {
    const reportLink = `${currentHost()}api/v1/reports/${type}?date=${date}`;
    console.log("report link:", reportLink);
    return reportLink;
}
