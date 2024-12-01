import {inject, Injectable} from '@angular/core';
import {
  AddressModel,
  CityModel,
  FirstNameModel,
  PrefixStreetTypeModel,
  StreetModel,
  StreetNameModel
} from './model/BALModel';
import {BehaviorSubject, combineLatest, map, Observable} from 'rxjs';
import {AddressDTO, BalRestControllerService, CityStreetLightDTO} from './generated/rest-bal';
import {CachedSubject} from './util/CachedSubject';

@Injectable({
  providedIn: 'root'
})
export class BalService {

  balRestApi = inject(BalRestControllerService);

  private refresh$ = new BehaviorSubject<void>(undefined);

  private cachedCities$: CachedSubject<CityModel[]> = new CachedSubject(
    () => this.doGetCities$());

  // loading with same request as SteetNames
  private cachedPrefixStreetTypes$ = new BehaviorSubject<PrefixStreetTypeModel[]>([]);

  private cachedStreetNames$: CachedSubject<StreetNameModel[]> = new CachedSubject(
    () => this.doGetStreetNames$());

  private cachedFirstNames$: CachedSubject<FirstNameModel[]> = new CachedSubject(
    () => this.doGetFirstNames$()
  );

  private cachedStreetDTOs$: CachedSubject<CityStreetLightDTO[]> = new CachedSubject(
    () => this.doGetStreetDTOs$()
  );

  private cachedAddressDTOs$: CachedSubject<AddressDTO[]> = new CachedSubject(
    () => this.doGetAddressDTOs$()
  );


  constructor() { }

  refresh() {
    this.refresh$.next();
  }

  private doGetCities$(): Observable<CityModel[]> {
    return this.balRestApi.getCitiesLight().pipe(map(
      dtos => dtos.map(dto => new CityModel(dto)))
    );
  }

  getCities$(): Observable<CityModel[]> {
    return this.cachedCities$.asObservable();
  }


  private doGetStreetNames$(): Observable<StreetNameModel[]> {
    return this.balRestApi.getStreetNameAndPrefixTypes().pipe(map(
      resp => {
        const prefixStreetTypes= resp.prefixStreetTypes!.map(
          dto => new PrefixStreetTypeModel(dto));
        this.cachedPrefixStreetTypes$.next(prefixStreetTypes);
        return resp.streetNames!.map(dto => new StreetNameModel(dto, prefixStreetTypes[dto.typeId!]))
      })
    );
  }

  getStreetNames$(): Observable<StreetNameModel[]> {
    return this.cachedStreetNames$.asObservable();
  }

  private doGetFirstNames$(): Observable<FirstNameModel[]> {
    return this.balRestApi.getFirstNames().pipe(map(
      dtos => dtos.map(dto => new FirstNameModel(dto)))
    );
  }

  getFirstNames$(): Observable<FirstNameModel[]> {
    return this.cachedFirstNames$.asObservable();
  }


  private doGetStreetDTOs$(): Observable<CityStreetLightDTO[]> {
    return this.balRestApi.getStreetsLight();
  }

  private getStreetDTOs$(): Observable<CityStreetLightDTO[]> {
    return this.cachedStreetDTOs$.asObservable();
  }

  getStreets$(): Observable<StreetModel[]> {
    return combineLatest([this.getStreetDTOs$(), this.getCities$(), this.getStreetNames$()]).pipe(
      map(([streetDTOs,cities,streetNames]) => {
        const cityById = new Map<number,CityModel>();
        cities.forEach(city => cityById.set(city.id, city));
        const streetNameById = new Map<number,StreetNameModel>();
        streetNames.forEach(street => streetNameById.set(street.id, street));
        const streetModels = streetDTOs.map(streetDTO => new StreetModel(streetDTO, cityById, streetNameById));
        return streetModels;
      })
    );
  }

  private doGetAddressDTOs$(): Observable<AddressDTO[]> {
      return this.balRestApi.getAddresses(0);
  }

  private getAddressDTOs$(): Observable<AddressDTO[]> {
    return this.cachedAddressDTOs$.asObservable();
  }

  getAddresses$(): Observable<AddressModel[]> {
    return combineLatest([this.getAddressDTOs$(), this.getCities$(), this.getStreetNames$()]).pipe(
      map(([addressDTOs,cities,streets]) => {
        const cityById = new Map<number,CityModel>();
        cities.forEach(city => cityById.set(city.id, city));
        const streetById = new Map<number,StreetNameModel>();
        streets.forEach(street => streetById.set(street.id, street));
        const addressModels = addressDTOs.map(addressDTO => new AddressModel(addressDTO, cityById, streetById));
        return addressModels;
      })
    );
  }
}
