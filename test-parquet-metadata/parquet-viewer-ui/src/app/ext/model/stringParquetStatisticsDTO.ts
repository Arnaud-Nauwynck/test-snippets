/**
 * Api Documentation
 * Api Documentation
 *
 * OpenAPI spec version: 1.0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */
import { ParquetStatisticsDTOobject } from './parquetStatisticsDTOobject';


export interface StringParquetStatisticsDTO extends ParquetStatisticsDTOobject { 
    distinctCount?: number;
    maxValue?: string;
    minValue?: string;
    nullCount?: number;
}
