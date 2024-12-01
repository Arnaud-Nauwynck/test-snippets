import {Component, computed, inject, signal, WritableSignal} from '@angular/core';
import {AgGridAngular} from 'ag-grid-angular';
import {FormsModule} from '@angular/forms';
import {BalService} from '../bal.service';
import {CityCriteria, StreetCriteria, StreetModel, StreetNameCriteria,} from '../model/BALModel';
import {ColDef, GridApi, GridReadyEvent} from 'ag-grid-community';
import {LeafletModule} from '@bluehalo/ngx-leaflet';
import {circle, Icon, icon, latLng, marker, polygon, tileLayer, Map as LeafletMap} from 'leaflet';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-streets',
  standalone: true,
  imports: [
    AgGridAngular,
    FormsModule,
    LeafletModule,
    NgIf
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
    { headerName: "City", width: 250, minWidth: 180, maxWidth: 250,
      valueGetter: p => p.data?.city?.name,
    },
    { headerName: "Code", width: 80, // minWidth: 80, maxWidth: 80,
      valueGetter: p => p.data?.city?.zip,
    },
    { headerName: "Pre", width: 70, minWidth: 70, maxWidth: 90,
      valueGetter: p => p.data?.street.pre,
    },
    { headerName: "Street Type", width: 140, minWidth: 100, maxWidth: 150,
      valueGetter: p => p.data?.street.streetType?.streetType,
    },
    { headerName: "Determinant", width: 80, minWidth: 80, maxWidth: 80,
      valueGetter: p => p.data?.street.streetType?.determinant,
    },
    { headerName: "Street Name", width: 400, minWidth: 300,
      // field: "name",
      valueGetter: p => p.data?.street?.name,
    },
    { headerName: "Count Addresses", width: 100, minWidth: 100, maxWidth: 100,
      // field: "name",
      valueGetter: p => p.data?.street?.countAddress,
    },
    { headerName: "Coord", width: 200, minWidth: 100, maxWidth: 300,
      // field: "name",
      valueGetter: p => p.data?.midCoord.toString(),
    },
  ];

  leafletLayer = marker([ 46.879966, -121.726909 ], {
    icon: icon({
      ...Icon.Default.prototype.options,
      iconUrl: 'assets/marker-icon.png',
      iconRetinaUrl: 'assets/marker-icon-2x.png',
      shadowUrl: 'assets/marker-shadow.png'
    })
  });

  leafletOptions = {
    layers: [
      tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 18, attribution: '...' })
    ],
    zoom: 11,
    center: latLng(48.8575, 2.3514) // Paris
  };

  leafletLayersControl = {
    baseLayers: {
      'Open Street Map': tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 18, attribution: '...' }),
      'Open Cycle Map': tileLayer('https://{s}.tile.opencyclemap.org/cycle/{z}/{x}/{y}.png', { maxZoom: 18, attribution: '...' })
    },
    overlays: {
      'Big Circle': circle([ 46.95, -122 ], { radius: 5000 }),
      'Big Square': polygon([[ 46.8, -121.55 ], [ 46.9, -121.55 ], [ 46.9, -121.7 ], [ 46.8, -121.7 ]])
    }
  }

  leafletShowLayer = true;
  leafletLayers = [
    circle([ 48.8575, 2.3514 ], { radius: 5000 }),
    // polygon([[ 48.8575, 2.3514 ], [ 48.8575, 2.36 ], [ 48.86, 2.35 ]]),
    // marker([ 48.8575, 2.3514 ])
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

  onMapReady($event: LeafletMap) {
    // Do stuff with map
  }

}
