import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICustomerAddress, CustomerAddress } from '../customer-address.model';
import { CustomerAddressService } from '../service/customer-address.service';

@Injectable({ providedIn: 'root' })
export class CustomerAddressRoutingResolveService implements Resolve<ICustomerAddress> {
  constructor(protected service: CustomerAddressService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICustomerAddress> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((customerAddress: HttpResponse<CustomerAddress>) => {
          if (customerAddress.body) {
            return of(customerAddress.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new CustomerAddress());
  }
}
