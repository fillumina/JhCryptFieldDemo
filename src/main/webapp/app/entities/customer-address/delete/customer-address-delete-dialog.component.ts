import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ICustomerAddress } from '../customer-address.model';
import { CustomerAddressService } from '../service/customer-address.service';

@Component({
  templateUrl: './customer-address-delete-dialog.component.html',
})
export class CustomerAddressDeleteDialogComponent {
  customerAddress?: ICustomerAddress;

  constructor(protected customerAddressService: CustomerAddressService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.customerAddressService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
