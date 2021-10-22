import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ICustomerAddress, CustomerAddress } from '../customer-address.model';

import { CustomerAddressService } from './customer-address.service';

describe('CustomerAddress Service', () => {
  let service: CustomerAddressService;
  let httpMock: HttpTestingController;
  let elemDefault: ICustomerAddress;
  let expectedResult: ICustomerAddress | ICustomerAddress[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(CustomerAddressService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      street: 'AAAAAAA',
      city: 'AAAAAAA',
      postcode: 'AAAAAAA',
      country: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a CustomerAddress', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new CustomerAddress()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a CustomerAddress', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          street: 'BBBBBB',
          city: 'BBBBBB',
          postcode: 'BBBBBB',
          country: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a CustomerAddress', () => {
      const patchObject = Object.assign(
        {
          street: 'BBBBBB',
          postcode: 'BBBBBB',
          country: 'BBBBBB',
        },
        new CustomerAddress()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of CustomerAddress', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          street: 'BBBBBB',
          city: 'BBBBBB',
          postcode: 'BBBBBB',
          country: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a CustomerAddress', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addCustomerAddressToCollectionIfMissing', () => {
      it('should add a CustomerAddress to an empty array', () => {
        const customerAddress: ICustomerAddress = { id: 123 };
        expectedResult = service.addCustomerAddressToCollectionIfMissing([], customerAddress);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(customerAddress);
      });

      it('should not add a CustomerAddress to an array that contains it', () => {
        const customerAddress: ICustomerAddress = { id: 123 };
        const customerAddressCollection: ICustomerAddress[] = [
          {
            ...customerAddress,
          },
          { id: 456 },
        ];
        expectedResult = service.addCustomerAddressToCollectionIfMissing(customerAddressCollection, customerAddress);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a CustomerAddress to an array that doesn't contain it", () => {
        const customerAddress: ICustomerAddress = { id: 123 };
        const customerAddressCollection: ICustomerAddress[] = [{ id: 456 }];
        expectedResult = service.addCustomerAddressToCollectionIfMissing(customerAddressCollection, customerAddress);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(customerAddress);
      });

      it('should add only unique CustomerAddress to an array', () => {
        const customerAddressArray: ICustomerAddress[] = [{ id: 123 }, { id: 456 }, { id: 40305 }];
        const customerAddressCollection: ICustomerAddress[] = [{ id: 123 }];
        expectedResult = service.addCustomerAddressToCollectionIfMissing(customerAddressCollection, ...customerAddressArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const customerAddress: ICustomerAddress = { id: 123 };
        const customerAddress2: ICustomerAddress = { id: 456 };
        expectedResult = service.addCustomerAddressToCollectionIfMissing([], customerAddress, customerAddress2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(customerAddress);
        expect(expectedResult).toContain(customerAddress2);
      });

      it('should accept null and undefined values', () => {
        const customerAddress: ICustomerAddress = { id: 123 };
        expectedResult = service.addCustomerAddressToCollectionIfMissing([], null, customerAddress, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(customerAddress);
      });

      it('should return initial array if no CustomerAddress is added', () => {
        const customerAddressCollection: ICustomerAddress[] = [{ id: 123 }];
        expectedResult = service.addCustomerAddressToCollectionIfMissing(customerAddressCollection, undefined, null);
        expect(expectedResult).toEqual(customerAddressCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
