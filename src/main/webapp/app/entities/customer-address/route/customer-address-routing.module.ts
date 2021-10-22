import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { CustomerAddressComponent } from '../list/customer-address.component';
import { CustomerAddressDetailComponent } from '../detail/customer-address-detail.component';
import { CustomerAddressUpdateComponent } from '../update/customer-address-update.component';
import { CustomerAddressRoutingResolveService } from './customer-address-routing-resolve.service';

const customerAddressRoute: Routes = [
  {
    path: '',
    component: CustomerAddressComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CustomerAddressDetailComponent,
    resolve: {
      customerAddress: CustomerAddressRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CustomerAddressUpdateComponent,
    resolve: {
      customerAddress: CustomerAddressRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CustomerAddressUpdateComponent,
    resolve: {
      customerAddress: CustomerAddressRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(customerAddressRoute)],
  exports: [RouterModule],
})
export class CustomerAddressRoutingModule {}
