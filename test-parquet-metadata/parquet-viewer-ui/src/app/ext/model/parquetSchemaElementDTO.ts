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
import { ParquetLogicalType } from './parquetLogicalType';


export interface ParquetSchemaElementDTO { 
    convertedType?: ParquetSchemaElementDTO.ConvertedTypeEnum;
    fieldId?: number;
    logicalType?: ParquetLogicalType;
    name: string;
    numChildren?: number;
    precision?: number;
    repetitionType?: ParquetSchemaElementDTO.RepetitionTypeEnum;
    scale?: number;
    type: ParquetSchemaElementDTO.TypeEnum;
    typeLength?: number;
}
export namespace ParquetSchemaElementDTO {
    export type ConvertedTypeEnum = 'UTF8' | 'MAP' | 'MAP_KEY_VALUE' | 'LIST' | 'ENUM' | 'DECIMAL' | 'DATE' | 'TIME_MILLIS' | 'TIME_MICROS' | 'TIMESTAMP_MILLIS' | 'TIMESTAMP_MICROS' | 'UINT_8' | 'UINT_16' | 'UINT_32' | 'UINT_64' | 'INT_8' | 'INT_16' | 'INT_32' | 'INT_64' | 'JSON' | 'BSON' | 'INTERVAL';
    export const ConvertedTypeEnum = {
        UTF8: 'UTF8' as ConvertedTypeEnum,
        MAP: 'MAP' as ConvertedTypeEnum,
        MAPKEYVALUE: 'MAP_KEY_VALUE' as ConvertedTypeEnum,
        LIST: 'LIST' as ConvertedTypeEnum,
        ENUM: 'ENUM' as ConvertedTypeEnum,
        DECIMAL: 'DECIMAL' as ConvertedTypeEnum,
        DATE: 'DATE' as ConvertedTypeEnum,
        TIMEMILLIS: 'TIME_MILLIS' as ConvertedTypeEnum,
        TIMEMICROS: 'TIME_MICROS' as ConvertedTypeEnum,
        TIMESTAMPMILLIS: 'TIMESTAMP_MILLIS' as ConvertedTypeEnum,
        TIMESTAMPMICROS: 'TIMESTAMP_MICROS' as ConvertedTypeEnum,
        UINT8: 'UINT_8' as ConvertedTypeEnum,
        UINT16: 'UINT_16' as ConvertedTypeEnum,
        UINT32: 'UINT_32' as ConvertedTypeEnum,
        UINT64: 'UINT_64' as ConvertedTypeEnum,
        INT8: 'INT_8' as ConvertedTypeEnum,
        INT16: 'INT_16' as ConvertedTypeEnum,
        INT32: 'INT_32' as ConvertedTypeEnum,
        INT64: 'INT_64' as ConvertedTypeEnum,
        JSON: 'JSON' as ConvertedTypeEnum,
        BSON: 'BSON' as ConvertedTypeEnum,
        INTERVAL: 'INTERVAL' as ConvertedTypeEnum
    };
    export type RepetitionTypeEnum = 'REQUIRED' | 'OPTIONAL' | 'REPEATED';
    export const RepetitionTypeEnum = {
        REQUIRED: 'REQUIRED' as RepetitionTypeEnum,
        OPTIONAL: 'OPTIONAL' as RepetitionTypeEnum,
        REPEATED: 'REPEATED' as RepetitionTypeEnum
    };
    export type TypeEnum = 'BOOLEAN' | 'INT32' | 'INT64' | 'INT96' | 'FLOAT' | 'DOUBLE' | 'BYTE_ARRAY' | 'FIXED_LEN_BYTE_ARRAY';
    export const TypeEnum = {
        BOOLEAN: 'BOOLEAN' as TypeEnum,
        INT32: 'INT32' as TypeEnum,
        INT64: 'INT64' as TypeEnum,
        INT96: 'INT96' as TypeEnum,
        FLOAT: 'FLOAT' as TypeEnum,
        DOUBLE: 'DOUBLE' as TypeEnum,
        BYTEARRAY: 'BYTE_ARRAY' as TypeEnum,
        FIXEDLENBYTEARRAY: 'FIXED_LEN_BYTE_ARRAY' as TypeEnum
    };
}
