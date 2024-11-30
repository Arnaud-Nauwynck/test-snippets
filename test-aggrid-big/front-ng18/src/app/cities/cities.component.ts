import {Component, computed, inject, OnInit, signal, WritableSignal} from '@angular/core';

import {AgGridAngular} from 'ag-grid-angular'; // Angular Data Grid Component
import {GridApi,GridReadyEvent,ColDef} from 'ag-grid-community';
import {BalService} from '../bal.service';
import {CityCriteria, CityModel, StringCriteria} from '../model/BALModel';
import {FormsModule} from '@angular/forms';

// import { CsvExportModule } from "@ag-grid-community/csv-export";
// import { ExcelExportModule } from "@ag-grid-enterprise/excel-export";
// import { MenuModule } from "@ag-grid-enterprise/menu";

@Component({
  selector: 'app-cities',
  standalone: true,
  imports: [AgGridAngular, FormsModule],
  templateUrl: './cities.component.html',
})
export class CitiesComponent implements OnInit {

  private gridApi!: GridApi<CityModel>;

  readonly balService = inject(BalService);
  cities$ = signal(<CityModel[]>[]);

  cityCriteria$: WritableSignal<CityCriteria> = signal(new CityCriteria({}));
  cityName = "";
  zipCode = "";

  filteredCities$ = computed(() => this.cityCriteria$().filter(this.cities$()));

  colDefs: ColDef<CityModel>[] = [
    { headerName: "Id", hide: true,
      valueGetter: p => p.data?.id,
    },
    { headerName: "Name", minWidth: 500,
      // field: "name",
      valueGetter: p => p.data?.name,
    },
    { headerName: "Zip Code", width: 100, minWidth: 100,
      valueGetter: p => p.data?.zip,
    },
    { headerName: "#Addresses", headerTooltip: "Number of Addresses in City", width: 130,
      valueGetter: p => p.data?.count,
    }
  ];

  ngOnInit(): void {
    this.balService.getCities$().subscribe(resp => {
      this.cities$.set(resp);
    })
  }

  onGridReady(params: GridReadyEvent<CityModel>) {
    this.gridApi = params.api;
  }

  onFilterChange() {
    const crit = new CityCriteria({name: this.cityName, zipCode: this.zipCode});
    this.cityCriteria$.set(crit);
  }

  onBtExport() {
    this.gridApi.exportDataAsCsv();
  }

}
