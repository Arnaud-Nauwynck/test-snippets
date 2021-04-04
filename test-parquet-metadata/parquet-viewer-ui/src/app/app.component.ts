import { Component, ViewChild, OnInit } from '@angular/core';

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

interface ColumnChunkRow {
	f: ParquetFileInfoDTO;
	col: ParquetSchemaElementDTO|null; // TODO
	rowGroup: ParquetRowGroupDTO;
	colChunk: ParquetColumnChunkDTO;
}

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
	title = 'ParquetViewer';

	columnName: string = '';

	@ViewChild('rowGroupGrid')
    rowGroupGrid!: AgGridAngular;

	@ViewChild('columnChunkGrid')
    columnChunkGrid!: AgGridAngular;

	fileInfo: ParquetFileInfoDTO = { schema:[], row_groups: []};
	rowGroups: ParquetRowGroupDTO[] = [];
	columnChunks: ColumnChunkRow[] = [];
	
	// ---------------------------------------------------------------------------------------------------

	constructor(private apiService: ParquetMetadataRestControllerService) {
		
	}

	ngOnInit() {
	}

	loadFileData() {
		let file = 'src/test/data/datapage_v2.snappy.parquet';
		this.apiService.readFileMetadataUsingGET(file).subscribe(data => {
			console.log('load data', data);
			this.fileInfo = data;
			this.rowGroups = data.row_groups;
			this.columnChunks = this.toColumnChunks(data, data.row_groups);
			if (this.rowGroupGrid && this.rowGroupGrid.api) {
				console.log('rowGroupGrid.api.setRowData(..)', this.rowGroups)
				this.rowGroupGrid.api.setRowData(this.rowGroups);
				this.rowGroupGrid.api.redrawRows()
			} else {
				console.log('loadFileData.. rowGroupGrid', this.rowGroupGrid);
			}
			if (this.columnChunkGrid && this.columnChunkGrid.api) {
				console.log('columnChunkGrid.api.setRowData(..)', this.columnChunks)
				this.columnChunkGrid.api.setRowData(this.columnChunks);
				this.columnChunkGrid.api.redrawRows()
			} else {
				console.log('loadFileData.. columnChunkGrid', this.columnChunkGrid);
			}	
		}, err => {
			console.log('Failed to load data', err);
		})
	}

	toColumnChunks(f: ParquetFileInfoDTO, rowGroups: ParquetRowGroupDTO[]): ColumnChunkRow[] {
		let res: ColumnChunkRow[] = [];
		if (rowGroups) {
			let colByName = new Map<string,ParquetSchemaElementDTO>();
			f.schema.forEach(c => {
				colByName.set(c.name, c);
			});
			rowGroups.forEach(rowGroup => {
				rowGroup.columns?.forEach(colChunk => {
					let col: ParquetSchemaElementDTO = colByName.get(colChunk.colName)!;
					res.push({ f, col, rowGroup, colChunk });
				})
			})
		}
		return res;
	}

	// ---------------------------------------------------------------------------------------------------
	
    rowGroupGridOptions: GridOptions = {
		isExternalFilterPresent: () => true,
		doesExternalFilterPass: (params) => this.rowGroupFilterPass(params),
		// doesExternalFilterPass: this.doesExternalFilterPass.bind(this)
		// doesExternalFilterPass: this.doesExternalFilterPass, // does not work when not binded to this!!
    };

	frameworkComponents = {
      buttonRenderer: ButtonRendererComponent,
	};
	
	msgs: string[] = [];
	
    rowGroupColumnDefs: ColDef[] = [
        {headerName: 'ordinal', 'field': 'ordinal', width: 100 },
        {headerName: 'num_rows', 'field': 'num_rows', width: 100 },
        {headerName: 'total_byte_size', 'field': 'total_byte_size', width: 100 },
        {headerName: 'total_compressed_size', 'field': 'total_compressed_size', width: 100 },
        {headerName: 'file_offset', 'field': 'file_offset', width: 100 },
		// {headerName: '#chunks'}

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
		this.loadFileData();
/*        // this.gridApi = this.agGrid.api;
		if (! this.rowGroupGrid.api) {
			this.rowGroupGrid.api = params.api; 
		}
		this.rowGroupGrid.api.setRowData(this.rowGroups);
		this.rowGroupGrid.api.sizeColumnsToFit();
*/    }

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
		isExternalFilterPresent: () => true,
		doesExternalFilterPass: (params) => this.columnChunkFilterPass(params),
		// doesExternalFilterPass: this.doesExternalFilterPass.bind(this)
		// doesExternalFilterPass: this.doesExternalFilterPass, // does not work when not binded to this!!
    };

	onColumnChunkGridReady(params: any) {
        // this.gridApi = this.agGrid.api;
/*
		if (! this.columnChunkGrid.api) {
			this.columnChunkGrid.api = params.api; 
		}
		this.columnChunkGrid.api.setRowData(this.columnChunks);
		this.columnChunkGrid.api.sizeColumnsToFit();
*/		
    }

	columnChunkFilterPass(params: RowNode): boolean {
		let d: ColumnChunkRow = params.data;

		return true;
	}
	
    columnChunkColumnDefs: ColDef[] = [
        {headerName: 'RG#', field: 'rowGroup.ordinal', width: 90 },
        {headerName: 'colName', field: 'colChunk.colName', width: 120 },
 
//    columnIndexLength?: number;
//    columnIndexOffset?: number;
//    cryptoMetadata?: ParquetColumnCryptoMetaDataDTO;
//    encryptedColumnMetadata?: string;
//    fileOffset?: number;
//    filePath?: string;
//    metaData?: ParquetColumnChunkMetaDataDTO;
//    offsetIndexLength?: number;
//    offsetIndexOffset?: number;
 
        {headerName: 'compressedSize', field: 'colChunk.meta_data.totalCompressedSize', width: 120 },
        {headerName: 'UncompressedSize', field: 'colChunk.meta_data.totalUncompressedSize', width: 120 },
        {headerName: 'NumValues', field: 'colChunk.meta_data.numValues', width: 120 },
        {headerName: 'bloomFilterOffset', field: 'colChunk.meta_data.bloomFilterOffset', width: 120 },
		
//    dataPageOffset?: number;
//    dictionaryPageOffset?: number;
//    encodingStats?: Array<ParquetPageEncodingStatsDTO>;
//    indexPageOffset?: number;
//    keyValueMetadata?: { [key: string]: string; };
        {headerName: 'stats.nullCount', field: 'colChunk.meta_data.statistics.nullCount', width: 120 },
        {headerName: 'stats.minValue', field: 'colChunk.meta_data.statistics.min_value', width: 120 },
        {headerName: 'stats.maxValue', field: 'colChunk.meta_data.statistics.max_value', width: 120 },


      
    ];


}