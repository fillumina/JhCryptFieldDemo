import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { CustomerAddressComponent } from './list/customer-address.component';
import { CustomerAddressDetailComponent } from './detail/customer-address-detail.component';
import { CustomerAddressUpdateComponent } from './update/customer-address-update.component';
import { CustomerAddressDeleteDialogComponent } from './delete/customer-address-delete-dialog.component';
import { CustomerAddressRoutingModule } from './route/customer-address-routing.module';

@NgModule({
  imports: [SharedModule, CustomerAddressRoutingModule],
  declarations: [
    CustomerAddressComponent,
    CustomerAddressDetailComponent,
    CustomerAddressUpdateComponent,
    CustomerAddressDeleteDialogComponent,
  ],
  entryComponents: [CustomerAddressDeleteDialogComponent],
})
export class CustomerAddressModule {}
