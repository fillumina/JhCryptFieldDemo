import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICustomer } from '../customer.model';
import { DataUtils } from 'app/core/util/data-util.service';
import { CustomerService } from '../service/customer.service';

@Component({
  selector: 'jhi-customer-detail',
  templateUrl: './customer-detail.component.html',
})
export class CustomerDetailComponent implements OnInit {
  customer: ICustomer | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute, protected customerService: CustomerService) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ customer }) => {
      this.customer = customer;
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  previousState(): void {
    window.history.back();
  }

  removeAddress(): void {
    if (this.customer) {
      this.customer.address = null;
      this.customerService.update(this.customer).subscribe(() => this.previousState());
    }
  }
}
