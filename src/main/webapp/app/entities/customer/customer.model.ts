import { ICustomerAddress } from 'app/entities/customer-address/customer-address.model';

export interface ICustomer {
  id?: number;
  firstName?: string | null;
  lastName?: string | null;
  email?: string | null;
  telephone?: string | null;
  addressRaw?: string | null;
  address?: ICustomerAddress | null;
}

export class Customer implements ICustomer {
  constructor(
    public id?: number,
    public firstName?: string | null,
    public lastName?: string | null,
    public email?: string | null,
    public telephone?: string | null,
    public addressRaw?: string | null,
    public address?: ICustomerAddress | null
  ) {}
}

export function getCustomerIdentifier(customer: ICustomer): number | undefined {
  return customer.id;
}
