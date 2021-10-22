import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICustomerAddress } from '../customer-address.model';

@Component({
  selector: 'jhi-customer-address-detail',
  templateUrl: './customer-address-detail.component.html',
})
export class CustomerAddressDetailComponent implements OnInit {
  customerAddress: ICustomerAddress | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ customerAddress }) => {
      this.customerAddress = customerAddress;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
