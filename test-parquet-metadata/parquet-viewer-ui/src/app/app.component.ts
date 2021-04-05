import { Component, ViewChild, OnInit, AfterViewInit } from '@angular/core';

import { AgGridAngular } from 'ag-grid-angular';
import { GridOptions, ColDef, RowNode } from 'ag-grid-community';

import { ButtonRendererComponent } from './renderer/button-renderer.component';

import { ParquetMetadataRestControllerService } from './ext/api/api'; 
import { ParquetFileInfoDTO, ParquetSchemaElementDTO,
	ParquetRowGroupDTO,
	ParquetColumnChunkDTO,
	ParquetColumnChunkMetaDataDTO,
	ParquetStatisticsDTOobject
	} from './ext/model/models'; 

interface SchemaColRow {
	col: ParquetSchemaElementDTO;
	selected: boolean;
}

interface RowGroupRow {
	f: ParquetFileInfoDTO;
	rg: ParquetRowGroupDTO;
}

interface ColumnChunkRow {
	f: ParquetFileInfoDTO;
	col: ParquetSchemaElementDTO;
	rg: ParquetRowGroupDTO;
	chunk: ParquetColumnChunkDTO;
}

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit, AfterViewInit {
	title = 'ParquetViewer';

	columnName: string = '';

	@ViewChild('schemaColGrid')
    schemaColGrid!: AgGridAngular;

	@ViewChild('rowGroupGrid')
    rowGroupGrid!: AgGridAngular;

	@ViewChild('columnChunkGrid')
    columnChunkGrid!: AgGridAngular;

	schemaCols: SchemaColRow[] = [];
	fileInfo: ParquetFileInfoDTO = { schema:[], rowGroups: []};
	rowGroups: RowGroupRow[] = [];
	colChunks: ColumnChunkRow[] = [];
	
	UNKNOWN_COL: ParquetSchemaElementDTO = {name:'UNKNOWN', type:'BYTE_ARRAY'};
	
	// ---------------------------------------------------------------------------------------------------

	constructor(private apiService: ParquetMetadataRestControllerService) {
		
	}

	ngOnInit() {
		console.log('ngOnInit')
	}

    ngAfterViewInit(): void {
		console.log('ngAfterViewInit')
		setTimeout(() => {
			this.loadFileData();
			}, 500);
	}
	
	loadFileData() {
		let file = 'src/test/data/datapage_V2.snappy.parquet';
		this.apiService.readFileMetadataUsingGET(file).subscribe(data => {
			console.log('load data', data);
			this.fileInfo = data;
			this.schemaCols = data.schema.map(col => { return { col, selected: true }; });
			this.rowGroups = this.toRowGroupRows(data, data.rowGroups);
			this.colChunks = this.toColumnChunks(data, data.rowGroups);
			console.log("colChunks", this.colChunks);
			
/*			this.rowGroupGrid.api.setRowData(this.rowGroups);
			// this.rowGroupGrid.api.sizeColumnsToFit();
			this.rowGroupGrid.api.redrawRows();
			
			this.columnChunkGrid.api.setRowData(this.colChunks);
			// this.columnChunkGrid.api.sizeColumnsToFit();
			this.columnChunkGrid.api.redrawRows();
*/		}, err => {
			console.log('Failed to load data', err);
		})
	}

	toRowGroupRows(f: ParquetFileInfoDTO, rowGroups: ParquetRowGroupDTO[]): RowGroupRow[] {
		return rowGroups.map(rg => { return { f, rg }; });	
	}
	
	toColumnChunks(f: ParquetFileInfoDTO, rowGroups: ParquetRowGroupDTO[]): ColumnChunkRow[] {
		let res: ColumnChunkRow[] = [];
		if (rowGroups) {
			let colByName = new Map<string,ParquetSchemaElementDTO>();
			f.schema.forEach(c => {
				colByName.set(c.name, c);
			});
			rowGroups.forEach(rg => {
				rg.colChunks?.forEach(chunk => {
					let col: ParquetSchemaElementDTO = colByName.get(chunk.colName)!;
					res.push({ f, col, rg, chunk });
				})
			})
		}
		return res;
	}

	defaultColDef: ColDef = {
		resizable: true,
		sortable: true,
			
	};
	
	frameworkComponents = {
      buttonRenderer: ButtonRendererComponent,
	};
	
	msgs: string[] = [];

	// ---------------------------------------------------------------------------------------------------
	
    schemaColGridOptions: GridOptions = {
		defaultColDef: this.defaultColDef,
	 	rowSelection: 'multiple',
		isExternalFilterPresent: () => true,
		doesExternalFilterPass: (params) => this.schemaColFilterPass(params),
		// doesExternalFilterPass: this.doesExternalFilterPass.bind(this)
		// doesExternalFilterPass: this.doesExternalFilterPass, // does not work when not binded to this!!
    };
	
	colTypeEnumToString(type: ParquetSchemaElementDTO.TypeEnum): string {
		switch(type) {
			case 'BYTE_ARRAY': return 'b[]';
			case 'FIXED_LEN_BYTE_ARRAY': return 'b[fixedlen]';
			case 'BOOLEAN': return 'bool';
			case 'INT32': return 'int';
			case 'INT64': return 'long';
			case 'INT96': return 'int96';
			case 'DOUBLE': return 'double';
			case 'FLOAT': return 'float';
			default: return type;
		}
	}
	
    schemaColColumnDefs: ColDef[] = [
        {headerName: 'Name', field: 'col.name', width: 250,
			checkboxSelection: true,
		},
		{headerName: 'Selected', field: 'selected', width: 80 },
        {headerName: '',
		  field:'toggleSelectedButton',
		  cellRenderer: 'buttonRenderer',
	      cellRendererParams: {
	        onClick: (clickParams: any) => this.onSchemaColToggleSelectedButtonClick(clickParams),
	        label: 'Toggle Select'
	      }
        },

        {headerName: 'fieldId', field: 'col.fieldId', width: 100 },
        {headerName: 'logicalType', field: 'col.logicalType.typeEnum', width: 100 },
        {headerName: 'convertedType', field: 'col.convertedType', width: 100 },
        {headerName: 'type', field: 'col.type', width: 100,
			cellRenderer: p => {
				let col = <ParquetSchemaElementDTO> p.data.col;
				return this.colTypeEnumToString(col.type);
			}
		},
        {headerName: 'typeLength', field: 'col.typeLength', width: 100 },
        {headerName: 'precision', field: 'col.precision', width: 100 },
        {headerName: 'scale', field: 'col.scale', width: 100 },

        {headerName: 'repetitionType', field: 'col.repetitionType', width: 100,
			cellRenderer: p => {
				let col = <ParquetSchemaElementDTO> p.data.col;
				switch(col.repetitionType) {
					case 'OPTIONAL': return 'opt';
					case 'REQUIRED': return '1';
					case 'REPEATED': return '*';
					default: return '' + col.repetitionType;
				}
			}
		},
        {headerName: 'numChildren', field: 'col.numChildren', width: 100 },

    ];

	onSchemaColGridReady(params: any) {
		this.schemaColGrid.api.setRowData(this.schemaCols);
    }

	onSchemaColToggleSelectedButtonClick(clickParams: any) {
		let r = <SchemaColRow> clickParams.rowData;
		r.selected = ! r.selected;
		// console.log('toggle col ' + r.col.name + ' => selected:' + r.selected)
		this.schemaColGrid.api.redrawRows();
		// TODO refresh other grids..
	}

	schemaColFilterPass(params: RowNode): boolean {
		let d = params.data;

		return true;
	}
	
	schemaColEvalFilterChange() {
		this.schemaColGrid.api.onFilterChanged();		
	}
	
	
	// ---------------------------------------------------------------------------------------------------
	
    rowGroupGridOptions: GridOptions = {
		defaultColDef: this.defaultColDef,
	 	rowSelection: 'multiple',
		isExternalFilterPresent: () => true,
		doesExternalFilterPass: (params) => this.rowGroupFilterPass(params),
		// doesExternalFilterPass: this.doesExternalFilterPass.bind(this)
		// doesExternalFilterPass: this.doesExternalFilterPass, // does not work when not binded to this!!
    };

	
    rowGroupColumnDefs: ColDef[] = [
        {headerName: 'ordinal', field: 'rg.ordinal', width: 100,
			checkboxSelection: true,
		},
        {headerName: 'numRows', field: 'rg.numRows', width: 100 },
        {headerName: 'totalByteSize', field: 'rg.totalByteSize', width: 100 },
        {headerName: 'totalCompressedSize', field: 'rg.totalCompressedSize', width: 100 },
        {headerName: 'fileOffset', field: 'rg.fileOffset', width: 100 },
		{headerName: 'chunks.count',
			valueGetter: p => {
				let r = <RowGroupRow> p.data;
				return r.rg.colChunks?.length;
			}
		},

        {field:'button',
		  cellRenderer: 'buttonRenderer',
	      cellRendererParams: {
	        onClick: (clickParams: any) => this.onBtnClick1(clickParams),
	        label: 'Click 1'
	      }
        },

        {field:'button',
		  cellRendererFramework: ButtonRendererComponent,
	      cellRendererParams: {
	        onClick: (clickParams: any) => this.onBtnClick1(clickParams),
	        label: 'Click 2'
	      }
        }
        
    ];

	onRowGroupGridReady(params: any) {
		// console.log('onRowGroupGridReady')
		this.rowGroupGrid.api.setRowData(this.rowGroups);
		// this.rowGroupGrid.api.sizeColumnsToFit();
		// this.rowGroupGrid.api.redrawRows();
    }

	onBtnClick1(clickParams: any) {
		let row = clickParams.rowData;
		console.log('onBtnClick1 .. clickParams', clickParams);
		this.msgs.push('onBtnClick1 .. row.make' + row.make);
	}

	isExternalFilterPresent(params: RowNode): boolean {
		return false;
	}
	
	rowGroupFilterPass(params: RowNode): boolean {
		let d = params.data;

		return true;
	}
	
	reevalFilterChange() {
		this.rowGroupGrid.api.onFilterChanged();		
	}
	
	// ---------------------------------------------------------------------------------------------------
	
	
	columnChunkGridOptions: GridOptions = {
		defaultColDef: this.defaultColDef,
	 	rowSelection: 'multiple',
		isExternalFilterPresent: () => true,
		doesExternalFilterPass: (params) => this.columnChunkFilterPass(params),
		// doesExternalFilterPass: this.doesExternalFilterPass.bind(this)
		// doesExternalFilterPass: this.doesExternalFilterPass, // does not work when not binded to this!!
    };

	onColumnChunkGridReady(params: any) {
		// console.log('onColumnChunkGridReady')
		this.columnChunkGrid.api.setRowData(this.colChunks);
		// this.columnChunkGrid.api.sizeColumnsToFit();
    }

	columnChunkFilterPass(params: RowNode): boolean {
		let d: ColumnChunkRow = params.data;

		return true;
	}
	
    columnChunkColumnDefs: ColDef[] = [
        {headerName: 'RG#', field: 'rg.ordinal', width: 90,
			checkboxSelection: true,
		},
        {headerName: 'col.name', field: 'col.name', width: 120 },
 
        {headerName: 'col.type', field: 'col.type', width: 100,
			cellRenderer: p => {
				let col = <ParquetSchemaElementDTO> p.data.col;
				return this.colTypeEnumToString(col.type);
			}
		},

        {headerName: 'compressedSize', field: 'chunk.metaData.totalCompressedSize', width: 120 },
        {headerName: 'UncompressedSize', field: 'chunk.metaData.totalUncompressedSize', width: 120 },
        {headerName: 'NumValues', field: 'chunk.metaData.numValues', width: 120 },

        {headerName: 'stats.nullCount', field: 'chunk.metaData.statistics.nullCount', width: 120 },
        {headerName: 'stats.minValue', field: 'chunk.metaData.statistics.minValue', width: 120 },
        {headerName: 'stats.maxValue', field: 'chunk.metaData.statistics.maxValue', width: 120 },

        {headerName: 'dataPageOffset', field: 'chunk.metaData.dataPageOffset', width: 120 },
        {headerName: 'dicPageOffset', field: 'chunk.metaData.dicPageOffset', width: 120 },
        {headerName: 'indexPageOffset', field: 'chunk.metaData.indexPageOffset', width: 120 },
        {headerName: 'bloomFilterOffset', field: 'chunk.metaData.bloomFilterOffset', width: 120 },

		{headerName: 'encodingStats', field: 'chunk.metaData.encodingStats', width: 120 },
		{headerName: 'keyValueMetadata', field: 'chunk.metaData.keyValueMetadata', width: 120 },
      
		{headerName: 'filePath', field: 'chunk.filePath', width: 120, hide: true },
		{headerName: 'fileOffset', field: 'chunk.fileOffset', width: 120 },
		{headerName: 'colIndexOffset', field: 'chunk.colIndexOffset', width: 120 },
		{headerName: 'colIndexLength', field: 'chunk.colIndexLength', width: 120 },

		{headerName: 'offsetIndexLength', field: 'chunk.offsetIndexLength', width: 120 },
		{headerName: 'offsetIndexOffset', field: 'chunk.offsetIndexOffset', width: 120 },

		{headerName: 'cryptoMetadata', field: 'chunk.cryptoMetadata', width: 120, hide: true },
		{headerName: 'encryptedColMetadata', field: 'chunk.encryptedColMetadata', width: 120, hide: true},

    ];


}