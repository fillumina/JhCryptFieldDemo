export interface ICustomerAddress {
  id?: number;
  street?: string | null;
  city?: string | null;
  postcode?: string;
  country?: string;
}

export class CustomerAddress implements ICustomerAddress {
  constructor(
    public id?: number,
    public street?: string | null,
    public city?: string | null,
    public postcode?: string,
    public country?: string
  ) {}
}

export function getCustomerAddressIdentifier(customerAddress: ICustomerAddress): number | undefined {
  return customerAddress.id;
}
