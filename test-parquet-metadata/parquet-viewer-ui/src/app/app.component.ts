import {Component, ViewChild, OnInit, AfterViewInit} from '@angular/core';

import {AgGridAngular} from 'ag-grid-angular';
import {GridOptions, ColDef, RowNode, IRowNode} from 'ag-grid-community';

import {ButtonRendererComponent} from './renderer/button-renderer.component';

import {ParquetMetadataRestService} from './ext/api/api';
import {
  ParquetFileInfoDTO, ParquetSchemaElementDTO,
  ParquetBlockMetadataDTO,
  ParquetColumnChunkMetaDataDTO,
  ScanDirFileMetadatasResultDTO, PartitionAndFileDataInfoDTO, PartitionScanStatisticsDTO,
} from './ext/model/models';

interface SchemaColRow {
  col: ParquetSchemaElementDTO;
  selected: boolean;
}

interface FileRow {
  partitions: string;
  fileName?: string;
  f: ParquetFileInfoDTO;
}

interface RowGroupRow {
  partitions: string;
  fileName?: string;
  f: ParquetFileInfoDTO;
  rg: ParquetBlockMetadataDTO;
}

interface ColumnChunkRow {
  partitions: string;
  fileName?: string;
  f: ParquetFileInfoDTO;
  col: ParquetSchemaElementDTO;
  rg: ParquetBlockMetadataDTO;
  chunk: ParquetColumnChunkMetaDataDTO;
}


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit, AfterViewInit {
  title = 'ParquetViewer';

  inputFile: string = ''; // 'src/test/'
  inputBaseDir: string = '';

  columnName: string = '';

  @ViewChild('schemaColGrid')
  schemaColGrid!: AgGridAngular;

  @ViewChild('fileGrid')
  fileGrid!: AgGridAngular;

  @ViewChild('rowGroupGrid')
  rowGroupGrid!: AgGridAngular;

  @ViewChild('columnChunkGrid')
  columnChunkGrid!: AgGridAngular;

  schemaCols: SchemaColRow[] = [];
  fileInfo: ParquetFileInfoDTO = {schema: [], blocks: []};
  files: FileRow[] = [];
  rowGroups: RowGroupRow[] = [];
  colChunks: ColumnChunkRow[] = [];

  partFileInfos: PartitionAndFileDataInfoDTO[] = [];
  partitionScanStatistics: PartitionScanStatisticsDTO[] = [];


  UNKNOWN_COL: ParquetSchemaElementDTO = {name: 'UNKNOWN', type: 'BYTE_ARRAY'};

  // ---------------------------------------------------------------------------------------------------

  constructor(private apiService: ParquetMetadataRestService) {
  }

  ngOnInit() {
    // console.log('ngOnInit')
  }

  ngAfterViewInit(): void {
    // setTimeout(() => this.onClickLoadSample(), 500);
  }

  // --------------------------------------------------------------------------------------------

  onClickLoadSampleFile(): void {
    let file = 'src/test/data/datapage_V2.snappy.parquet';
    if (!this.inputFile) {
      this.inputFile = file;
    }
    this.loadFileData(file);
  }

  onClickScanSampleDir(): void {
    let dir = 'src/test/data/table1';
    if (!this.inputBaseDir) {
      this.inputBaseDir = dir;
    }
    this.scanDirFileMetadata(dir);
  }

  onClickLoadInputFile(): void {
    if (!this.inputFile) {
      return;
    }
    this.loadFileData(this.inputFile);
  }

  onClickScanBaseDir(): void {
    if (!this.inputBaseDir) {
      return;
    }
    this.scanDirFileMetadata(this.inputBaseDir);
  }

  loadFileData(file: string) {
    this.apiService.readFileMetadata(file).subscribe(data => {
      console.log('load data', data);
      this.fileInfo = data;
      if (data.schema) {
        this.schemaCols = data.schema.map(col => {
          return {col, selected: true};
        });
      } else {
        this.schemaCols = [];
      }
      if (data.blocks) {
        this.files = [{partitions: '', fileName: file, f: data}];
        this.rowGroups = this.toRowGroupRows(file, data, data.blocks);
        this.colChunks = this.toColumnChunks(file, data, data.blocks);
      } else {
        this.rowGroups = [];
        this.colChunks = [];
      }

      if (this.fileGrid.api) {
        this.fileGrid.api.setRowData(this.files);
      }
      if (this.rowGroupGrid.api) {
        this.rowGroupGrid.api.setRowData(this.rowGroups);
      }
      if (this.columnChunkGrid.api) {
        this.columnChunkGrid.api.setRowData(this.colChunks);
      }


    }, err => {
      console.log('Failed to load data', err);
    })
  }

  scanDirFileMetadata(baseDir: string) {
    this.apiService.scanDirFileMetadata(baseDir).subscribe((data: ScanDirFileMetadatasResultDTO) => {
      console.log('scanDirFileMetadataUsingGET =>', data);

      if (data.schema) {
        this.schemaCols = data.schema.map(col => {
          return {col, selected: true};
        });
      } else {
        this.schemaCols = [];
      }
      this.partFileInfos = data.partFileInfos!;
      this.partitionScanStatistics = data.partitionScanStatistics!;

      let schema = data.schema!;
      if (data.partFileInfos) {
        let files: FileRow[] = [];
        let rowGroups: RowGroupRow[] = [];
        let colChunks: ColumnChunkRow[] = [];
        data.partFileInfos.forEach(partFile => {
          let partitions = partFile.partitionValues!.reduce((a, b) => a + '/' + b, '');
          let fileName = partFile.fileName;
          let f: ParquetFileInfoDTO = {schema: schema, blocks: partFile.dataInfo!.rowGroups!};
          let file = {partitions, fileName, f}
          files.push(file);
          partFile.dataInfo!.rowGroups!.forEach(rg => {
            rowGroups.push({...file, rg});
            rg.columns?.forEach((chunk, colIdx) => {
              let col = schema[colIdx];
              colChunks.push({...file, col, rg, chunk});
            })
          })
        })
        this.files = files;
        this.rowGroups = rowGroups;
        this.colChunks = colChunks;
      } else {
        this.files = [];
        this.rowGroups = [];
        this.colChunks = [];
      }

      if (this.fileGrid.api) {
        this.fileGrid.api.setRowData(this.files);
      }
      if (this.rowGroupGrid.api) {
        this.rowGroupGrid.api.setRowData(this.rowGroups);
      }
      if (this.columnChunkGrid.api) {
        this.columnChunkGrid.api.setRowData(this.colChunks);
      }

    }, err => {
      console.log('Failed to load data', err);
    })
  }

  toRowGroupRows(fileName: string, f: ParquetFileInfoDTO, rowGroups: ParquetBlockMetadataDTO[]): RowGroupRow[] {
    return rowGroups.map(rg => {
      return {partitions: '', fileName, f, rg};
    });
  }

  toColumnChunks(fileName: string, f: ParquetFileInfoDTO, rowGroups: ParquetBlockMetadataDTO[]): ColumnChunkRow[] {
    let res: ColumnChunkRow[] = [];
    if (rowGroups) {
      let schema = f.schema!!;
      rowGroups.forEach(rg => {
        rg.columns?.forEach((chunk, colIdx) => {
          let col = schema[colIdx];
          res.push({partitions: '', fileName, f, col, rg, chunk});
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

  // Grid for Schema columns
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
    switch (type) {
      case 'BYTE_ARRAY':
        return 'b[]';
      case 'FIXED_LEN_BYTE_ARRAY':
        return 'b[fixedlen]';
      case 'BOOLEAN':
        return 'bool';
      case 'INT32':
        return 'int';
      case 'INT64':
        return 'long';
      case 'INT96':
        return 'int96';
      case 'DOUBLE':
        return 'double';
      case 'FLOAT':
        return 'float';
      default:
        return type;
    }
  }

  schemaColColumnDefs: ColDef<SchemaColRow>[] = [
    {
      headerName: 'Name', field: 'col.name', width: 250,
      checkboxSelection: true,
    },
    {headerName: 'Selected', field: 'selected', width: 80},
    {
      headerName: '',
      // TODO ?? field: 'toggleSelectedButton',
      cellRenderer: 'buttonRenderer',
      cellRendererParams: {
        onClick: (clickParams: any) => this.onSchemaColToggleSelectedButtonClick(clickParams),
        label: 'Toggle Select'
      }
    },

    {headerName: 'fieldId', field: 'col.fieldId', width: 100},
    {headerName: 'logicalType', field: 'col.logicalType.typeEnum', width: 100},
    {headerName: 'convertedType', field: 'col.convertedType', width: 100},
    {
      headerName: 'type', field: 'col.type', width: 100,
      valueFormatter: p => {
        let col = p.data!!.col;
        return this.colTypeEnumToString(col.type!!);
      }
    },
    {headerName: 'typeLength', field: 'col.typeLength', width: 100},
    {headerName: 'precision', field: 'col.precision', width: 100},
    {headerName: 'scale', field: 'col.scale', width: 100},

    {
      headerName: 'repetitionType', field: 'col.repetitionType', width: 100,
      valueFormatter: p => {
        let col = p.data!!.col;
        switch (col.repetitionType) {
          case 'OPTIONAL':
            return 'opt';
          case 'REQUIRED':
            return '1';
          case 'REPEATED':
            return '*';
          default:
            return '' + col.repetitionType;
        }
      }
    },
    {headerName: 'numChildren', field: 'col.numChildren', width: 100},

  ];

  onSchemaColGridReady(params: any) {
    this.schemaColGrid.api.setRowData(this.schemaCols);
  }

  onSchemaColToggleSelectedButtonClick(clickParams: any) {
    let r = <SchemaColRow>clickParams.rowData;
    r.selected = !r.selected;
    // console.log('toggle col ' + r.col.name + ' => selected:' + r.selected)
    this.schemaColGrid.api.redrawRows();
    // TODO refresh other grids..
  }

  schemaColFilterPass(params: IRowNode): boolean {
    let d = params.data;

    return true;
  }

  schemaColEvalFilterChange() {
    this.schemaColGrid.api.onFilterChanged();
  }


  // Grid for Files
  // ---------------------------------------------------------------------------------------------------

  fileGridOptions: GridOptions = {
    defaultColDef: this.defaultColDef,
    rowSelection: 'multiple',
    isExternalFilterPresent: () => true,
    doesExternalFilterPass: (params) => this.fileFilterPass(params),
    // doesExternalFilterPass: this.doesExternalFilterPass.bind(this)
    // doesExternalFilterPass: this.doesExternalFilterPass, // does not work when not binded to this!!
  };


  fileColumnDefs: ColDef[] = [
    {
      headerName: 'Parts', field: 'partitions', width: 150,
      checkboxSelection: true,
    },
    {headerName: 'FileName', field: 'fileName', width: 90},
//		{headerName: 'rowGroups.count',
//			valueGetter: p => {
//				let r = <RowGroupRow> p.data;
//				return r.rg.colChunks?.length;
//			}
//		},
//		{headerName: 'chunks.count',
//			valueGetter: p => {
//				let r = <RowGroupRow> p.data;
//				return r.rg.colChunks?.length;
//			}
//		},
  ];

  onFileGridReady(params: any) {
    // console.log('onFileGridReady')
    this.fileGrid.api.setRowData(this.files);
  }

  fileFilterPass(params: IRowNode): boolean {
    let d = params.data;

    return true;
  }

  fileReevalFilterChange() {
    this.fileGrid.api.onFilterChanged();
  }

  // Grid for RowGroup
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
    {
      headerName: 'Parts', field: 'partitions', width: 150,
      checkboxSelection: true,
    },
    {headerName: 'FileName', field: 'fileName', width: 90},
    {headerName: 'RowGroup', field: 'rg.ordinal', width: 100},
    {headerName: 'numRows', field: 'rg.numRows', width: 100},
    {headerName: 'totalByteSize', field: 'rg.totalByteSize', width: 100},
    {headerName: 'totalCompressedSize', field: 'rg.totalCompressedSize', width: 100},
    {headerName: 'fileOffset', field: 'rg.fileOffset', width: 100},
    {
      headerName: 'chunks.count',
      valueGetter: p => {
        let r = <RowGroupRow>p.data;
        return r.rg.columns?.length;
      }
    },
  ];

  onRowGroupGridReady(params: any) {
    // console.log('onRowGroupGridReady')
    this.rowGroupGrid.api.setRowData(this.rowGroups);
  }

  onBtnClick1(clickParams: any) {
    let row = clickParams.rowData;
    console.log('onBtnClick1 .. clickParams', clickParams);
    this.msgs.push('onBtnClick1 .. row.make' + row.make);
  }

  isExternalFilterPresent(params: RowNode): boolean {
    return false;
  }

  rowGroupFilterPass(params: IRowNode): boolean {
    let d = params.data;

    return true;
  }

  reevalFilterChange() {
    this.rowGroupGrid.api.onFilterChanged();
  }

  // Grid for ColumnChunk
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

  columnChunkFilterPass(params: IRowNode): boolean {
    let d: ColumnChunkRow = params.data;

    return true;
  }

  columnChunkColumnDefs: ColDef<ColumnChunkRow>[] = [
    {
      headerName: 'Parts', field: 'partitions', width: 150,
      checkboxSelection: true,
    },
    {headerName: 'fileName', field: 'fileName', width: 90},
    {headerName: 'RG#', field: 'rg.ordinal', width: 90},
    {headerName: 'Col', field: 'col.name', width: 120},

    {
      headerName: 'col.type', field: 'col.type', width: 100,
      valueFormatter: p => {
        let col = p.data!!.col;
        return this.colTypeEnumToString(col.type!!);
      }
    },

    {headerName: 'totalSize',
      valueGetter: p => p.data!!.chunk.totalSize, width: 120},
    {headerName: 'UncompressedSize',
      valueGetter: p => p.data!!.chunk.totalUncompressedSize, width: 120},
    {headerName: 'NumValues',
      valueGetter: p => p.data!!.chunk.valueCount, width: 120},

    {headerName: 'stats.nullCount',
      valueGetter: p => p.data!!.chunk.statistics?.nullCount, width: 120},
    {headerName: 'stats.minValue',
      valueGetter: p => p.data!!.chunk.statistics?.minValue, width: 120},
    {headerName: 'stats.maxValue',
      valueGetter: p => p.data!!.chunk.statistics?.maxValue, width: 120},

    // {headerName: 'dataPageOffset', field: 'chunk.metaData.dataPageOffset', width: 120},
    // {headerName: 'dicPageOffset', field: 'chunk.metaData.dicPageOffset', width: 120},
    // {headerName: 'indexPageOffset', field: 'chunk.metaData.indexPageOffset', width: 120},
    // {headerName: 'bloomFilterOffset', field: 'chunk.metaData.bloomFilterOffset', width: 120},
    //
    // {headerName: 'encodingStats', field: 'chunk.metaData.encodingStats', width: 120},
    // {headerName: 'keyValueMetadata', field: 'chunk.metaData.keyValueMetadata', width: 120},
    //
    // {headerName: 'filePath', field: 'chunk.filePath', width: 120, hide: true},
    // {headerName: 'fileOffset', field: 'chunk.fileOffset', width: 120},
    // {headerName: 'colIndexOffset', field: 'chunk.colIndexOffset', width: 120},
    // {headerName: 'colIndexLength', field: 'chunk.colIndexLength', width: 120},
    //
    // {headerName: 'offsetIndexLength', field: 'chunk.offsetIndexLength', width: 120},
    // {headerName: 'offsetIndexOffset', field: 'chunk.offsetIndexOffset', width: 120},
    //
    // {headerName: 'cryptoMetadata', field: 'chunk.cryptoMetadata', width: 120, hide: true},
    // {headerName: 'encryptedColMetadata', field: 'chunk.encryptedColMetadata', width: 120, hide: true},

  ];


}
