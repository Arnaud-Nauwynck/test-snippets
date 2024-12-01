import {
  AddressDTO,
  CityLightDTO,
  CityStreetLightDTO,
  CoordDTO,
  FirstnameDTO, PrefixStreetTypeDTO,
  StreetNameDTO
} from '../generated/rest-bal';

export class CoordModel {
  public readonly lng: number;
  public readonly lat: number;

  constructor(src: CoordDTO) {
    this.lng = src.lg!;
    this.lat = src.la!;
  }

  toString(): string {
    return this.lat + ', ' + this.lng;
  }
}

export class StringCriteria {
  public contains?: string;
  public startsWith?: string;
  public regexp?: RegExp;

  static of(text: string|null|undefined): (StringCriteria|undefined) {
    if (!text) return undefined;
    const res = new StringCriteria();
    if (text.startsWith("/")) { // regexp
      res.regexp = new RegExp(text.substring(1));
    } else if (text.startsWith("^")) {
      res.startsWith = text.substring(1);
    } else {
      res.contains = text;
    }
    return res;
  }


  match(src: string) {
    if (this.contains && -1 === src.indexOf(this.contains)) {
      return false;
    }
    if (this.startsWith && !src.startsWith(this.startsWith)) {
      return false;
    }
    if (this.regexp && !this.regexp.test(src)) {
      return false;
    }
    return true;
  }
}

export class CityCriteria {
  public nameCrit?: StringCriteria;
  public zipCodeCrit?: StringCriteria;

  constructor(opts: {name?: string, zipCode?:string}) {
    this.nameCrit = StringCriteria.of(opts.name);
    this.zipCodeCrit = StringCriteria.of(opts.zipCode);
  }

  filter(src: CityModel[]): CityModel[] {
    return src.filter(x => this.match(x));
  }

  match(src: CityModel) {
    if (this.nameCrit && ! this.nameCrit.match(src.name)) {
      return false;
    }
    if (this.zipCodeCrit && ! this.zipCodeCrit.match(src.zip)) {
      return false;
    }
    return true;
  }

}


/**
 * immutable model for City
 */
export class CityModel {
  public readonly id: number;
  public readonly name: string;
  public readonly zip: string;
  public readonly count: number;
  public readonly midCoord: CoordModel;

  constructor(src: CityLightDTO) {
    this.id = src.id!!;
    this.name = src.n!!;
    this.zip = src.z!!;
    this.count = src.c!!;
    this.midCoord = new CoordModel(src.midCoord!);
  }

}



export class StreetNameCriteria {
  public preCrit?: StringCriteria;
  public streetTypeCrit?: StringCriteria;
  public determinantCrit?: StringCriteria;
  public nameCrit?: StringCriteria;

  constructor(opts: {pre?: string, streetType?: string, determinant?: string, name?: string}) {
    this.preCrit = StringCriteria.of(opts.pre);
    this.streetTypeCrit = StringCriteria.of(opts.streetType);
    this.determinantCrit = StringCriteria.of(opts.determinant);
    this.nameCrit = StringCriteria.of(opts.name);
  }

  filter(src: StreetNameModel[]): StreetNameModel[] {
    return src.filter(x => this.match(x));
  }

  match(src: StreetNameModel) {
    if (this.preCrit && (!src.pre || ! this.preCrit.match(src.pre))) {
      return false;
    }
    if (this.streetTypeCrit && ! this.streetTypeCrit.match(src.streetType?.streetType)) {
      return false;
    }
    if (this.determinantCrit && ! this.determinantCrit.match(src.streetType?.determinant)) {
      return false;
    }
    if (this.nameCrit && ! this.nameCrit.match(src.name)) {
      return false;
    }
    return true;
  }

}

export class PrefixStreetTypeModel {
  public readonly id: number;
  public readonly streetType: string;
  public readonly determinant: string;
  // redundant, useless?
  // public readonly streetTypeAndDeterminant: string;

  constructor(src: PrefixStreetTypeDTO) {
    this.id = src.id!;
    this.streetType = src.streetType!;
    this.determinant = src.addressPrefix!;
    // this.streetTypeAndDeterminant = src.streetTypeAndPrefix!;
  }
}

/**
 * immutable model for Street Name
 */
export class StreetNameModel {
  public readonly id: number;
  public readonly pre?: string;
  public readonly streetType: PrefixStreetTypeModel;
  public readonly name: string;

  private readonly _c: number;

  public get countAddress(): number { return this._c };
//  countByCityZipCode?: { [key: string]: number; };

  constructor(src: StreetNameDTO, streetType: PrefixStreetTypeModel) {
    this.id = src.id!!;
    this.pre = src.pre;
    this.streetType = streetType;
    this.name = src.name!!;
    this._c = src.countAddress!!;
  }
}



export class StreetCriteria {

  constructor(
    public cityCrit?: CityCriteria,
    public streetCrit?: StreetNameCriteria
  ) {}

  filter(src: StreetModel[]): StreetModel[] {
    return src.filter(x => this.match(x));
  }

  match(src: StreetModel) {
    if (this.cityCrit && ! this.cityCrit.match(src.city)) {
      return false;
    }
    if (this.streetCrit && ! this.streetCrit.match(src.street)) {
      return false;
    }
    return true;
  }

}

/**
 * immutable model for City-Street
 */
export class StreetModel {
  public readonly city: CityModel;
  public readonly street: StreetNameModel;
  public readonly midCoord: CoordModel;

  constructor(src: CityStreetLightDTO,
              cityById: Map<number,CityModel>,
              streetById: Map<number,StreetNameModel>
  ) {
    this.city = cityById.get(src.c!!)!!;
    this.street = streetById.get(src.s!!)!!;
    this.midCoord = new CoordModel({lg: src.lng!, la: src.lat!});
  }

}



export class AddressCriteria {

  constructor(
    public cityCrit?: CityCriteria,
    public streetCrit?: StreetNameCriteria,
    public streetNumber?: number,
    public streetNumberSuffix?: StringCriteria
  ) {}

  filter(src: AddressModel[]): AddressModel[] {
    return src.filter(x => this.match(x));
  }

  match(src: AddressModel) {
    if (this.cityCrit && ! this.cityCrit.match(src.city)) {
      return false;
    }
    if (this.streetCrit && ! this.streetCrit.match(src.street)) {
      return false;
    }
    if (this.streetNumber && this.streetNumber !== src.n) {
      return false;
    }
    if (this.streetNumberSuffix && (! src.ns || ! this.streetNumberSuffix.match(src.ns))) {
      return false;
    }
    return true;
  }

}

/**
 * immutable model for Address
 */
export class AddressModel {
  public readonly city: CityModel;
  public readonly street: StreetNameModel;
  public readonly n: number;
  public readonly ns?: string;
  // public readonly coord: CoordDTO;

  constructor(src: AddressDTO,
              cityById: Map<number,CityModel>,
              streetById: Map<number,StreetNameModel>
              ) {
    this.city = cityById.get(src.c!!)!!;
    this.street = streetById.get(src.s!!)!!;
    this.n = src.n!!;
    this.ns = src.ns;
  }

}





export class FirstNameCriteria {
  public nameCrit?: StringCriteria;
  public genreCrit?: StringCriteria;
  public langageCrit?: StringCriteria;

  constructor(opts: {name?: string, genre?: string, langage?: string}) {
    this.nameCrit = StringCriteria.of(opts.name);
    this.genreCrit = StringCriteria.of(opts.genre);
    this.langageCrit = StringCriteria.of(opts.langage);
  }

  filter(src: FirstNameModel[]): FirstNameModel[] {
    return src.filter(x => this.match(x));
  }

  match(src: FirstNameModel) {
    if (this.nameCrit && ! this.nameCrit.match(src.name)) {
      return false;
    }
    if (this.genreCrit && ! this.genreCrit.match(src.genre)) {
      return false;
    }
    if (this.langageCrit && ! this.langageCrit.match(src.langage)) {
      return false;
    }
    return true;
  }

}

/**
 * immutable model for FirstName
 */
export class FirstNameModel {
  public readonly name: string;
  public readonly genre: string;
  public readonly langage: string;
  public readonly freq: number;

  constructor(src: FirstnameDTO) {
    this.name = src.prenom!!;
    this.genre = src.genre!!;
    this.langage = src.langage!!;
    this.freq = src.frequence!!;
  }

}
