import {Component, computed, inject, signal, WritableSignal} from '@angular/core';

import {AgGridAngular} from 'ag-grid-angular'; // Angular Data Grid Component
import {ColDef} from 'ag-grid-community';
import {FormsModule} from '@angular/forms';
import {BalService} from '../bal.service';
import {
  AddressCriteria,
  AddressModel,
  CityCriteria,
  CityModel,
  StreetNameCriteria,
  StringCriteria
} from '../model/BALModel';

@Component({
  selector: 'app-addresses',
  standalone: true,
  imports: [AgGridAngular, FormsModule],
  templateUrl: './addresses.component.html',
})
export class AddressesComponent {

  readonly balService = inject(BalService);
  addresses$ = signal(<AddressModel[]>[]);

  addressCriteria$: WritableSignal<AddressCriteria> = signal(new AddressCriteria(undefined, undefined));
  cityName = "";
  zipCode = "";
  streetName = "";
  streetNumber: number = 1;
  streetNumberSuffix = "";

  filteredAddresses$ = computed(() => this.addressCriteria$().filter(this.addresses$()));

  colDefs: ColDef<AddressModel>[] = [
    { headerName: "City",
      valueGetter: p => p.data?.city?.name,
    },
    { headerName: "ZipCode",
      valueGetter: p => p.data?.city?.zip,
    },
    { headerName: "Num", headerTooltip: "Address number", width: 90,
      valueGetter: p => p.data?.n,
    },
    { headerName: "Suffix", headerTooltip: "Address Number Suffix", width: 90,
      valueGetter: p => p.data?.ns,
    },
    { headerName: "Street Name", minWidth: 500,
      // field: "name",
      valueGetter: p => p.data?.street?.name,
    },
  ];

  ngOnInit(): void {
    this.balService.getAddresses$().subscribe(resp => {
      this.addresses$.set(resp);
    })
  }

  onFilterChange() {
    const cityCrit = new CityCriteria({name: this.cityName, zipCode: this.zipCode});
    const streetCrit = new StreetNameCriteria({name: this.streetName});
    const streetNumberCrit = this.streetNumber;
    const streetNumberSuffixCrit = StringCriteria.of(this.streetNumberSuffix);
    const crit = new AddressCriteria(cityCrit, streetCrit, streetNumberCrit, streetNumberSuffixCrit);
    this.addressCriteria$.set(crit);
  }

}
