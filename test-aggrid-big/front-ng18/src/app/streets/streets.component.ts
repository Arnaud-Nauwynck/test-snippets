import {Component, computed, inject, signal, WritableSignal} from '@angular/core';
import {AgGridAngular} from 'ag-grid-angular';
import {FormsModule} from '@angular/forms';
import {BalService} from '../bal.service';
import {
  CityCriteria, FirstNameModel, StreetCriteria,
  StreetModel,
  StreetNameCriteria, StreetNameModel,
} from '../model/BALModel';
import {ColDef, GridApi, GridReadyEvent} from 'ag-grid-community';

@Component({
  selector: 'app-streets',
  standalone: true,
  imports: [
    AgGridAngular,
    FormsModule
  ],
  templateUrl: './streets.component.html',
})
export class StreetsComponent {

  private gridApi!: GridApi<StreetModel>;

  readonly balService = inject(BalService);
  streets$ = signal(<StreetModel[]>[]);

  streetCriteria$: WritableSignal<StreetCriteria> = signal(new StreetCriteria(undefined, undefined));
  cityName = "";
  zipCode = "";
  streetName = "";

  filteredStreets$ = computed(() => this.streetCriteria$().filter(this.streets$()));

  colDefs: ColDef<StreetModel>[] = [
    { headerName: "City",
      valueGetter: p => p.data?.city?.name,
    },
    { headerName: "ZipCode",
      valueGetter: p => p.data?.city?.zip,
    },
    { headerName: "Street Name", minWidth: 500,
      // field: "name",
      valueGetter: p => p.data?.street?.name,
    },
  ];

  ngOnInit(): void {
    this.balService.getStreets$().subscribe(resp => {
      this.streets$.set(resp);
    })
  }

  onGridReady(params: GridReadyEvent<StreetModel>) {
    this.gridApi = params.api;
  }

  onFilterChange() {
    const cityCrit = new CityCriteria({name: this.cityName, zipCode: this.zipCode});
    const streetCrit = new StreetNameCriteria({name: this.streetName});
    const crit = new StreetCriteria(cityCrit, streetCrit);
    this.streetCriteria$.set(crit);
  }

  onBtExport() {
    this.gridApi.exportDataAsCsv();
  }

}
