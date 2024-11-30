import {Component, computed, inject, signal, WritableSignal} from '@angular/core';

import { AgGridAngular } from 'ag-grid-angular'; // Angular Data Grid Component
import {ColDef, GridApi, GridReadyEvent} from 'ag-grid-community';
import {FormsModule} from '@angular/forms';
import {BalService} from '../bal.service';
import {CityCriteria, CityModel, FirstNameModel, StreetNameCriteria, StreetNameModel} from '../model/BALModel';

@Component({
  selector: 'app-street-names',
  standalone: true,
  imports: [AgGridAngular, FormsModule],
  templateUrl: './street-names.component.html',
})
export class StreetNamesComponent {

  private gridApi!: GridApi<StreetNameModel>;

  readonly balService = inject(BalService);
  streetNames$ = signal(<StreetNameModel[]>[]);

  streetNameCriteria$: WritableSignal<StreetNameCriteria> = signal(new StreetNameCriteria({}));
  streetName = "";

  filteredStreetNames$ = computed(() => this.streetNameCriteria$().filter(this.streetNames$()));

  colDefs: ColDef<StreetNameModel>[] = [
    { headerName: "Id", hide: true,
      valueGetter: p => p.data?.id,
    },
    { headerName: "Name", minWidth: 500,
      // field: "name",
      valueGetter: p => p.data?.name,
    },
  ];

  ngOnInit(): void {
    this.balService.getStreetNames$().subscribe(resp => {
      this.streetNames$.set(resp);
    })
  }

  onGridReady(params: GridReadyEvent<StreetNameModel>) {
    this.gridApi = params.api;
  }

  onFilterChange() {
    const crit = new StreetNameCriteria({name: this.streetName});
    this.streetNameCriteria$.set(crit);
  }

  onBtExport() {
    this.gridApi.exportDataAsCsv();
  }
}
