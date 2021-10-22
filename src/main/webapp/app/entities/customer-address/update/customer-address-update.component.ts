import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ICustomerAddress, CustomerAddress } from '../customer-address.model';
import { CustomerAddressService } from '../service/customer-address.service';

@Component({
  selector: 'jhi-customer-address-update',
  templateUrl: './customer-address-update.component.html',
})
export class CustomerAddressUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    street: [],
    city: [],
    postcode: [null, [Validators.required, Validators.maxLength(10)]],
    country: [null, [Validators.required, Validators.maxLength(2)]],
  });

  constructor(
    protected customerAddressService: CustomerAddressService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ customerAddress }) => {
      this.updateForm(customerAddress);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const customerAddress = this.createFromForm();
    if (customerAddress.id !== undefined) {
      this.subscribeToSaveResponse(this.customerAddressService.update(customerAddress));
    } else {
      this.subscribeToSaveResponse(this.customerAddressService.create(customerAddress));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICustomerAddress>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(customerAddress: ICustomerAddress): void {
    this.editForm.patchValue({
      id: customerAddress.id,
      street: customerAddress.street,
      city: customerAddress.city,
      postcode: customerAddress.postcode,
      country: customerAddress.country,
    });
  }

  protected createFromForm(): ICustomerAddress {
    return {
      ...new CustomerAddress(),
      id: this.editForm.get(['id'])!.value,
      street: this.editForm.get(['street'])!.value,
      city: this.editForm.get(['city'])!.value,
      postcode: this.editForm.get(['postcode'])!.value,
      country: this.editForm.get(['country'])!.value,
    };
  }
}
