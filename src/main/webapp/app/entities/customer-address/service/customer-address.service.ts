import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICustomerAddress, getCustomerAddressIdentifier } from '../customer-address.model';

export type EntityResponseType = HttpResponse<ICustomerAddress>;
export type EntityArrayResponseType = HttpResponse<ICustomerAddress[]>;

@Injectable({ providedIn: 'root' })
export class CustomerAddressService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/customer-addresses');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(customerAddress: ICustomerAddress): Observable<EntityResponseType> {
    return this.http.post<ICustomerAddress>(this.resourceUrl, customerAddress, { observe: 'response' });
  }

  update(customerAddress: ICustomerAddress): Observable<EntityResponseType> {
    return this.http.put<ICustomerAddress>(
      `${this.resourceUrl}/${getCustomerAddressIdentifier(customerAddress) as number}`,
      customerAddress,
      { observe: 'response' }
    );
  }

  partialUpdate(customerAddress: ICustomerAddress): Observable<EntityResponseType> {
    return this.http.patch<ICustomerAddress>(
      `${this.resourceUrl}/${getCustomerAddressIdentifier(customerAddress) as number}`,
      customerAddress,
      { observe: 'response' }
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICustomerAddress>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICustomerAddress[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addCustomerAddressToCollectionIfMissing(
    customerAddressCollection: ICustomerAddress[],
    ...customerAddressesToCheck: (ICustomerAddress | null | undefined)[]
  ): ICustomerAddress[] {
    const customerAddresses: ICustomerAddress[] = customerAddressesToCheck.filter(isPresent);
    if (customerAddresses.length > 0) {
      const customerAddressCollectionIdentifiers = customerAddressCollection.map(
        customerAddressItem => getCustomerAddressIdentifier(customerAddressItem)!
      );
      const customerAddressesToAdd = customerAddresses.filter(customerAddressItem => {
        const customerAddressIdentifier = getCustomerAddressIdentifier(customerAddressItem);
        if (customerAddressIdentifier == null || customerAddressCollectionIdentifiers.includes(customerAddressIdentifier)) {
          return false;
        }
        customerAddressCollectionIdentifiers.push(customerAddressIdentifier);
        return true;
      });
      return [...customerAddressesToAdd, ...customerAddressCollection];
    }
    return customerAddressCollection;
  }
}
