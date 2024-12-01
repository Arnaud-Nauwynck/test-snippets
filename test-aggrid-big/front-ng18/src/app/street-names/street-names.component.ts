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
  pre = "";
  streetType = "";
  determinant = "";
  streetName = "";

  filteredStreetNames$ = computed(() => this.streetNameCriteria$().filter(this.streetNames$()));

  colDefs: ColDef<StreetNameModel>[] = [
    { headerName: "Id", width: 90,
      // hide: true,
      valueGetter: p => p.data?.id,
    },
    { headerName: "PrefixTypeId", minWidth: 80,
      hide: true,
      valueGetter: p => p.data?.streetType?.id,
    },
    { headerName: "Pre", width: 80, minWidth: 80, maxWidth: 80,
      valueGetter: p => p.data?.pre,
    },
    { headerName: "Street Type", width: 120, minWidth: 100, maxWidth: 150,
      valueGetter: p => p.data?.streetType?.streetType,
    },
    { headerName: "Determinant", width: 80, minWidth: 90,
      valueGetter: p => p.data?.streetType?.determinant,
    },
    { headerName: "Name", width: 350, minWidth: 350,
      valueGetter: p => p.data?.name,
    },
    { headerName: "Count Addresses", width: 100, minWidth: 100, maxWidth: 150,
      valueGetter: p => p.data?.countAddress,
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
    const crit = new StreetNameCriteria({
      pre: this.pre,
      streetType: this.streetType,
      determinant: this.determinant,
      name: this.streetName
    });
    this.streetNameCriteria$.set(crit);
  }

  onBtExport() {
    this.gridApi.exportDataAsCsv();
  }
}
