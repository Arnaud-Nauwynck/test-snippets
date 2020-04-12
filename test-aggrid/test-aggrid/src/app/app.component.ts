import { Component } from '@angular/core';
import { GridOptions, ColDef, GridApi } from 'ag-grid-community';

import { ButtonRendererComponent } from './renderer/button-renderer.component';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'test-aggrid';

	gridApi: GridApi;
    gridOptions: GridOptions = {
		isExternalFilterPresent: () => true,
		doesExternalFilterPass: (params) => this.doesExternalFilterPass(params),
		// doesExternalFilterPass: this.doesExternalFilterPass.bind(this)
		// doesExternalFilterPass: this.doesExternalFilterPass, // does not work when not binded to this!!
    };

	frameworkComponents = {
      buttonRenderer: ButtonRendererComponent,
	};
	
	makeCrit: string = '';
	modelCrit: string = '';
	priceCrit: string = '';
	
	msgs: string[] = [];
	
    columnDefs: ColDef[] = [
        {field: 'make' },
        {field: 'model' },
        {field: 'price'},
        
        {field:'button',
		  cellRenderer: 'buttonRenderer',
	      cellRendererParams: {
	        onClick: (clickParams) => this.onBtnClick1(clickParams),
	        label: 'Click 1'
	      }
        },

        {field:'button',
		  cellRendererFramework: ButtonRendererComponent,
	      cellRendererParams: {
	        onClick: (clickParams) => this.onBtnClick1(clickParams),
	        label: 'Click 2'
	      }
        }
        
    ];

    rowData = [
        { make: 'Toyota', model: 'Celica', price: 35000 },
        { make: 'Ford', model: 'Mondeo', price: 32000 },
        { make: 'Porsche', model: 'Boxter', price: 72000 }
    ];
    
	onGridReady(params) {
        this.gridApi = params.api;
		params.api.sizeColumnsToFit();
    }

	onBtnClick1(clickParams) {
		let row = clickParams.rowData;
		console.log('onBtnClick1 .. clickParams', clickParams);
		this.msgs.push('onBtnClick1 .. row.make' + row.make);
	}

	isExternalFilterPresent(params): boolean {
		if (this.makeCrit !== '') {
			console.log('isExternalFilterPresent', params)
			return true;
		}
		return false;
	}
	
	doesExternalFilterPass(params): boolean {
		let d = params.data;
		let make = <string>d.make;
		let makeCrit = this.makeCrit;
		if (this.makeCrit) {
			if(-1 === make.indexOf(this.makeCrit)) {
				console.log('FILTER OUT .. ' + make + ' contains ' + this.makeCrit);
				return false;
			}
		}
		console.log('doesExternalFilterPass ' + make + ' contains? ' + makeCrit + ' => ');
		return true;
	}
	
	reevalFilterChange() {
		console.log('reevalFilterChange ' + this.makeCrit)
		this.gridApi.onFilterChanged();		
	}
}