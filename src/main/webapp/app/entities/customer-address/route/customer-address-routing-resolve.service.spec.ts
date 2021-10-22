jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { ICustomerAddress, CustomerAddress } from '../customer-address.model';
import { CustomerAddressService } from '../service/customer-address.service';

import { CustomerAddressRoutingResolveService } from './customer-address-routing-resolve.service';

describe('CustomerAddress routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: CustomerAddressRoutingResolveService;
  let service: CustomerAddressService;
  let resultCustomerAddress: ICustomerAddress | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [Router, ActivatedRouteSnapshot],
    });
    mockRouter = TestBed.inject(Router);
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
    routingResolveService = TestBed.inject(CustomerAddressRoutingResolveService);
    service = TestBed.inject(CustomerAddressService);
    resultCustomerAddress = undefined;
  });

  describe('resolve', () => {
    it('should return ICustomerAddress returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultCustomerAddress = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultCustomerAddress).toEqual({ id: 123 });
    });

    it('should return new ICustomerAddress if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultCustomerAddress = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultCustomerAddress).toEqual(new CustomerAddress());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as CustomerAddress })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultCustomerAddress = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultCustomerAddress).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
