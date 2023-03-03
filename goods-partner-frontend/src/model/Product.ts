export default interface Product {
    amount: number
    coefficient: number
    measure: string
    productName: string
    storeName: string
    totalProductWeight: number
    unitWeight: number
    refKey: string
    productUnit: ProductMeasureDetails
    productPackaging: ProductMeasureDetails
}

export interface ProductMeasureDetails {
    amount: number
    coefficientStandard: number
    measureStandard: string
}