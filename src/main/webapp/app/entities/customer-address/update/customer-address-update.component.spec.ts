jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { CustomerAddressService } from '../service/customer-address.service';
import { ICustomerAddress, CustomerAddress } from '../customer-address.model';

import { CustomerAddressUpdateComponent } from './customer-address-update.component';

describe('CustomerAddress Management Update Component', () => {
  let comp: CustomerAddressUpdateComponent;
  let fixture: ComponentFixture<CustomerAddressUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let customerAddressService: CustomerAddressService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [CustomerAddressUpdateComponent],
      providers: [FormBuilder, ActivatedRoute],
    })
      .overrideTemplate(CustomerAddressUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CustomerAddressUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    customerAddressService = TestBed.inject(CustomerAddressService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const customerAddress: ICustomerAddress = { id: 456 };

      activatedRoute.data = of({ customerAddress });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(customerAddress));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<CustomerAddress>>();
      const customerAddress = { id: 123 };
      jest.spyOn(customerAddressService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ customerAddress });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: customerAddress }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(customerAddressService.update).toHaveBeenCalledWith(customerAddress);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<CustomerAddress>>();
      const customerAddress = new CustomerAddress();
      jest.spyOn(customerAddressService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ customerAddress });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: customerAddress }));
      saveSubject.complete();

      // THEN
      expect(customerAddressService.create).toHaveBeenCalledWith(customerAddress);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<CustomerAddress>>();
      const customerAddress = { id: 123 };
      jest.spyOn(customerAddressService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ customerAddress });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(customerAddressService.update).toHaveBeenCalledWith(customerAddress);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
