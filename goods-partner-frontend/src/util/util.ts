import {format} from 'date-fns'

export const currentHost = () => {
    return process.env.NODE_ENV === "development"
        ? process.env.REACT_APP_LOCALHOST_BASE_URL
        : process.env.REACT_APP_HEROKU_BASE_URL;
}

export const apiUrl = `${currentHost()}api/v1`;

export const generateReportLink = (type: any, date: string) => {
    const reportLink = `${currentHost()}api/v1/reports/${type}?date=${date}`;
    console.log("report link:", reportLink);
    return reportLink;
}

export const toHoursAndMinutes = (totalMinutes: number) => {
    const minutes = totalMinutes % 60;
    const hours = Math.floor(totalMinutes / 60);

    return `${hours}год. ${minutes}хв.`;
}

export const reformatDate = (date: string) => {
    return format(new Date(date), 'dd.MM.yy')
}