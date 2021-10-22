import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { CustomerAddressDetailComponent } from './customer-address-detail.component';

describe('CustomerAddress Management Detail Component', () => {
  let comp: CustomerAddressDetailComponent;
  let fixture: ComponentFixture<CustomerAddressDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CustomerAddressDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ customerAddress: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(CustomerAddressDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(CustomerAddressDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load customerAddress on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.customerAddress).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
