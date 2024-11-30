import {Component, computed, inject, OnInit, signal, WritableSignal} from '@angular/core';

import {AgGridAngular} from 'ag-grid-angular'; // Angular Data Grid Component
import {ColDef, GridApi, GridReadyEvent} from 'ag-grid-community';
import {BalService} from '../bal.service';
import {CityModel, FirstNameCriteria, FirstNameModel} from '../model/BALModel';
import {FormsModule} from '@angular/forms';


@Component({
  selector: 'app-firstnames',
  standalone: true,
  imports: [AgGridAngular, FormsModule],
  templateUrl: './first-names.component.html',
})
export class FirstNamesComponent {

  private gridApi!: GridApi<FirstNameModel>;

  readonly balService = inject(BalService);
  firstNames$ = signal(<FirstNameModel[]>[]);

  firstNameCriteria$: WritableSignal<FirstNameCriteria> = signal(new FirstNameCriteria({}));
  name = "";
  genre = "";
  langage = "";

  filteredFirstNames$ = computed(() => this.firstNameCriteria$().filter(this.firstNames$()));

  colDefs: ColDef<FirstNameModel>[] = [
    { headerName: "Name", minWidth: 200,
      // field: "name",
      valueGetter: p => p.data?.name,
    },
    { headerName: "Genre", width: 100, minWidth: 100,
      valueGetter: p => p.data?.genre,
    },
    { headerName: "Langage", headerTooltip: "Langage", width: 130,
      valueGetter: p => p.data?.langage,
    },
    { headerName: "Freq", headerTooltip: "Frequency", width: 130,
      valueGetter: p => p.data?.freq,
    }
  ];

  ngOnInit(): void {
    this.balService.getFirstNames$().subscribe(resp => {
      this.firstNames$.set(resp);
    })
  }

  onGridReady(params: GridReadyEvent<FirstNameModel>) {
    this.gridApi = params.api;
  }

  onFilterChange() {
    const crit = new FirstNameCriteria({name: this.name, genre: this.genre, langage: this.langage});
    this.firstNameCriteria$.set(crit);
  }

  onBtExport() {
    this.gridApi.exportDataAsCsv();
  }
}
